package com.example.android.myyearbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    // Dark Sky API Documentation: https://darksky.net/dev/docs
//    private final static String WEATHER_API_URL =
//            "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/40.775840,-73.02511?exclude=minutely,hourly,alerts,flags";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

//    private double latitude = 40.775840;
//    private double longitude = -73.02511;

    private WeatherAdapter weatherAdapter;
    protected Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);

        if (checkPermissions()) {
            new DebugLog("Get last location");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
        } else {
            new DebugLog("Get permissions");
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(WeatherActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            weatherTime(mLastLocation);
                        } else {
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.weather_list);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    Location mLastLocation = task.getResult();
                                    weatherTime(mLastLocation);
                                }
                            }
                        });
            }
        } else {
            weatherTime();
        }
    }

    private void weatherTime(Location location) {
        if (location != null) {
            prefs.edit().putString("lastLat", Double.toString(location.getLatitude())).apply();
            prefs.edit().putString("lastLong", Double.toString(location.getLongitude())).apply();

            // Create an Async task to download the weather data and update the UI
            new WeatherAsyncTask().execute(String.format(new Locale("US"),
                    "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/%f,%f" +
                            "?exclude=minutely,hourly,alerts,flags",
                    location.getLatitude(), location.getLongitude()));
        } else {
            weatherTime();
        }
    }

    private void weatherTime() {
        String lastLat, lastLong;
        lastLat = prefs.getString("lastLat", null);
        lastLong = prefs.getString("lastLong", null);

        if (lastLat == null || lastLong == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to access GPS")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            // Create an Async task to download the weather data and update the UI
            new WeatherAsyncTask().execute(String.format(new Locale("US"),
                    "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/%s,%s" +
                            "?exclude=minutely,hourly,alerts,flags",
                    lastLat, lastLong));
        }

    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, ArrayList<Weather>> {

        @Override
        protected ArrayList<Weather> doInBackground(String... urls) {
            try {
                return parseJSONData(downloadJSONData(urls[0])); // Try to download and parse the weather data
            } catch (JSONException | IOException e) {
                return null; // Return null if there's an error
            }
        }

        // The result parameter comes from what is returned from doInBackground()
        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            updateUi(result); // Update the information displayed to the user.
        }
    }

    private static String downloadJSONData(String url) throws IOException {
        String jsonData;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Connect to the API url
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input and return the response.
            // Otherwise, throw an exception
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                reader = new BufferedReader(inputStreamReader);
                // Since all the json data is on 1 line, there's no need to create a loop to read multiple lines
                jsonData = reader.readLine();
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new IOException();
        } finally {
            // Close everything regardless of whether there was an exception or not

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                // Closing the BufferedReader should be good enough
                // https://stackoverflow.com/questions/12199142/closing-bufferedreader-and-inputstreamreader
                reader.close();
            }
        }
        return jsonData;
    }

    private static ArrayList<Weather> parseJSONData(String weatherJSON) throws JSONException {
        // If the JSON string is empty or null, then throw a JSONException.
        if (TextUtils.isEmpty(weatherJSON)) {
            throw new JSONException("Input string is null or empty");
        }

        ArrayList<Weather> forecast = new ArrayList<>();
        JSONObject baseJsonResponse = new JSONObject(weatherJSON); // Convert the weatherJSON String into a JSONObject
        JSONObject daily = baseJsonResponse.getJSONObject("daily"); // Get the daily object
        JSONArray data = daily.getJSONArray("data"); // Get the data for each day of the week

        // Loop through the data for each day of the week
        for (int i = 0; i < data.length(); i++) {
            JSONObject weather = (JSONObject) data.get(i);
            // Create new Weather objects from the JSON and add them to the forecast ArrayList
            forecast.add(new Weather(
                    new Date(weather.getLong("time") * 1000), // Add a few zeros to fix the UTC time
                    weather.getString("summary"),
                    weather.getString("icon"),
                    weather.getDouble("temperatureHigh"),
                    weather.getDouble("temperatureLow")
            ));
        }

        return forecast;
    }

    private void updateUi(ArrayList<Weather> result) {
        if (result != null) {
            if (weatherAdapter == null) {
                // Create a new WeatherAdapter and set it to be the adapter of the ListView
                weatherAdapter = new WeatherAdapter(this, result);
                ((ListView) findViewById(R.id.weather_list)).setAdapter(weatherAdapter);
            } else {
                // Notify the adapter that the data in the ArrayList has changed
                weatherAdapter.notifyDataSetChanged();
            }
        } else {
            // Show an error dialog if the result is null
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to download data")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            onNavigateUp(); // This method causes the Up button to be triggered
                        }
                    })
                    .show();
        }
    }
}
