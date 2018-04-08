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

    private final static String WEATHER_API_URL =
            "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/40.775840,-73.02511?exclude=minutely,hourly,alerts,flags";

    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        new WeatherAsyncTask().execute(WEATHER_API_URL);
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, ArrayList<Weather>> {

        @Override
        protected ArrayList<Weather> doInBackground(String... urls) {
            try {
                return parseJSONData(downloadJSONData(urls[0]));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            // Update the information displayed to the user.
            updateUi(result);
        }
    }

    private static String downloadJSONData(String url) throws IOException {
        String jsonData;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                reader = new BufferedReader(inputStreamReader);
                jsonData = reader.readLine();
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new IOException();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
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
        JSONObject baseJsonResponse = new JSONObject(weatherJSON);
        JSONObject daily = baseJsonResponse.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject weather = (JSONObject) data.get(i);
            forecast.add(new Weather(
                    new Date(weather.getLong("time") * 1000),
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
                weatherAdapter = new WeatherAdapter(this, result);
                ((ListView) findViewById(R.id.weather_list)).setAdapter(weatherAdapter);
            } else {
                weatherAdapter.notifyDataSetChanged();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to download data")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onNavigateUp();
                        }
                    })
                    .show();
        }
    }
}
