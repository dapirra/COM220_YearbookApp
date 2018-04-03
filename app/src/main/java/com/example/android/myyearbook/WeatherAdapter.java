package com.example.android.myyearbook;

import android.content.Context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View weatherItem = convertView;
        if (weatherItem == null) {
            weatherItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.weather_item, parent, false);
        }

        final Weather weather = (Weather) getItem(position);

        ((TextView) weatherItem.findViewById(R.id.summary_text)).setText(weather.getSummary());

        ((TextView) weatherItem.findViewById(R.id.date_text)).setText(weather.getDate());

        ((TextView) weatherItem.findViewById(R.id.temp_text)).setText(
                String.format("%d° - %d°", Math.round(weather.getTempLow()), Math.round(weather.getTempHigh())));

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

        return weatherItem;
    }
}
