package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherWorker extends Worker {
    private static final String TAG = "WeatherWorker";
    public static final String WEATHER_BROADCAST = "com.example.weatherapp.weather.broadcast";
    public static final String WEATHER_JSON_PAYLOAD = "com.example.weatherapp.weather.payload";
    private Context context;

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Attempting to make HTTP request...");


        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=53.4375&longitude=-7.9375&hourly=temperature_2m,relativehumidity_2m,windspeed_10m&current_weather=true&timeformat=unixtime&past_days=2";
        try {
            String jsonData = fetchDataFromApi(apiUrl);
            Log.d(TAG, "Received JSON data from API: " + jsonData);

            sendWeatherBroadcast(jsonData);
            return Result.success();
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            return Result.failure();
        }
    }

    private String fetchDataFromApi(String apiUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "HTTP error: " + response.code() + " - " + response.message());
                return null;
            }

            return response.body().string();
        }
    }

    private void sendWeatherBroadcast(String jsonData) {
        Intent weatherIntent = new Intent(WEATHER_BROADCAST);
        weatherIntent.putExtra(WEATHER_JSON_PAYLOAD, jsonData);
        LocalBroadcastManager.getInstance(context).sendBroadcast(weatherIntent);
    }
}