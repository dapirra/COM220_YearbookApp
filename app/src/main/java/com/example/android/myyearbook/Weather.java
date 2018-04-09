package com.example.android.myyearbook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {
    private Date date;
    private String summary;
    private String icon;
    private double tempHigh;
    private double tempLow;

    /**
     * This object holds weather information for a particular day.
     *
     * @param date A Date object that holds the day.
     * @param summary A short summary of the weather.
     * @param icon A string that says which icon should represent this day.
     * @param tempHigh The highest temperature of this day.
     * @param tempLow The lowest temperature of this day.
     */
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
