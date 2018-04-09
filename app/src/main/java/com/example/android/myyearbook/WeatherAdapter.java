package com.example.android.myyearbook;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter {

    public WeatherAdapter(Context context, ArrayList<Weather> forecast) {
        super(context, 0, forecast);
    }

    // Get the data from the weather array and put it into each item in the ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the weather_item layout
        View weatherItem = convertView;
        if (weatherItem == null) {
            weatherItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.weather_item, parent, false);
        }

        // Get the weather object at the current position
        final Weather weather = (Weather) getItem(position);

        // Set the summary text
        ((TextView) weatherItem.findViewById(R.id.summary_text)).setText(weather.getSummary());

        // Set the date text
        ((TextView) weatherItem.findViewById(R.id.date_text)).setText(weather.getFormattedDate());

        // Set the temperature text
        ((TextView) weatherItem.findViewById(R.id.temp_text)).setText(weather.getFormattedTemp());

        // Set the icon
        int imageResource;
        switch (weather.getIcon()) {
            case "clear-day": imageResource = R.drawable.day_sunny; break;
            case "clear-night": imageResource = R.drawable.night_clear; break;
            case "rain": imageResource = R.drawable.rain; break;
            case "snow": imageResource = R.drawable.snow; break;
            case "sleet": imageResource = R.drawable.sleet; break;
            case "wind": imageResource = R.drawable.cloudy_windy; break;
            case "fog": imageResource = R.drawable.fog; break;
            case "cloudy": imageResource = R.drawable.cloudy; break;
            case "partly-cloudy-day": imageResource = R.drawable.day_cloudy; break;
            case "partly-cloudy-night": imageResource = R.drawable.night_alt_cloudy; break;
            case "hail": imageResource = R.drawable.hail; break;
            case "thunderstorm": imageResource = R.drawable.thunderstorm; break;
            case "tornado": imageResource = R.drawable.tornado; break;
            default: imageResource = R.drawable.cloudy;
        }
        ((ImageView) weatherItem.findViewById(R.id.weather_image)).setImageResource(imageResource);

        // Set a listener for when an item is clicked
        weatherItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(weather.getFormattedDate())
                        .setMessage(weather.getSummary())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return weatherItem;
    }
}
