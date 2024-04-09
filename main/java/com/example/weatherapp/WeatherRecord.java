package com.example.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class WeatherRecord {
    private double humidity;
    private double temperature;
    private double windspeed;

    public WeatherRecord(double humidity, double temperature, double windspeed) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.windspeed = windspeed;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(double windspeed) {
        this.windspeed = windspeed;
    }

    public static ArrayList<WeatherRecord> fromJsonArray(JSONObject jsonObject) {
        ArrayList<WeatherRecord> records = new ArrayList<>();
        try {
            JSONArray timeArray = jsonObject.getJSONArray("time");
            JSONArray temperatureArray = jsonObject.getJSONArray("temperature_2m");
            JSONArray humidityArray = jsonObject.getJSONArray("relativehumidity_2m");
            JSONArray windspeedArray = jsonObject.getJSONArray("windspeed_10m");

            int length = Math.min(timeArray.length(), Math.min(temperatureArray.length(), Math.min(humidityArray.length(), windspeedArray.length())));

            for (int i = 0; i < length; i++) {
                double temperature = temperatureArray.getDouble(i);
                double humidity = humidityArray.getDouble(i);
                double windspeed = windspeedArray.getDouble(i);
                records.add(new WeatherRecord(humidity, temperature, windspeed));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return records;
    }

}