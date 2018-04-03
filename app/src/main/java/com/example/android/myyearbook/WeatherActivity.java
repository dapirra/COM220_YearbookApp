package com.example.android.myyearbook;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {

    final static String WEATHER_API_URL =
            "https://api.darksky.net/forecast/115983dea73677f9a74319ccfd8a9c81/40.7657,-73.0152?exclude=minutely,hourly,alerts,flags";

    final static String FILE_NAME = "Weather.json";

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
            saveData();
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

    // Incomplete
    private void loadData() {
        try {
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(openFileInput(FILE_NAME), "UTF-8"));
            updateUi(Utils.extractWeatherFromJson(inputStream.readLine()));
            inputStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save the JSON data to the save file
    private void saveData() {
        try {
            FileOutputStream outputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(Utils.jsonWeather.getBytes()); // Convert to binary and write
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
