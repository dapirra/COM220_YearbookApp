package com.example.android.myyearbook;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;

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

public class WeatherActivity extends AppCompatActivity {

    // Dark Sky API Documentation: https://darksky.net/dev/docs
    private final static String WEATHER_API_URL =
            "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/40.775840,-73.02511?exclude=minutely,hourly,alerts,flags";

    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Create an Async task to download the weather data and update the UI
        new WeatherAsyncTask().execute(WEATHER_API_URL);
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
