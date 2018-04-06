package com.example.android.myyearbook;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {

    final static String WEATHER_API_URL =
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
            return Utils.fetchWeatherData(urls[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> result) {
            // Update the information displayed to the user.
            updateUi(result);
        }
    }

    private void updateUi(ArrayList<Weather> result) {
        if (weatherAdapter == null) {
            weatherAdapter = new WeatherAdapter(this, result);
            ((ListView) findViewById(R.id.weather_list)).setAdapter(weatherAdapter);
        } else {
            weatherAdapter.notifyDataSetChanged();
        }
    }
}
