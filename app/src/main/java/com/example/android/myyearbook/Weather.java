package com.example.android.myyearbook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {
    private Date date;
    private String summary;
    private String icon;
    private double tempHigh;
    private double tempLow;

    public Weather(Date date, String summary, String icon, double tempHigh, double tempLow) {
        this.date = date;
        this.summary = summary;
        this.icon = icon;
        this.tempHigh = tempHigh;
        this.tempLow = tempLow;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(double tempHigh) {
        this.tempHigh = tempHigh;
    }

    public double getTempLow() {
        return tempLow;
    }

    public void setTempLow(double tempLow) {
        this.tempLow = tempLow;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("EEE, MMM d").format(date);
    }

    public String getFormattedTemp() {
        return String.format("%d° - %d°", Math.round(this.tempLow), Math.round(this.tempHigh));
    }
}
