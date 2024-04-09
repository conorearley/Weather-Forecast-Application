package com.example.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private LineChart lineChart;
    private BroadcastReceiver weatherReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.line_chart);
        lineChart.getDescription().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setGranularity(1f);

        weatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonData = intent.getStringExtra(WeatherWorker.WEATHER_JSON_PAYLOAD);
                Log.d(TAG, "Received JSON data: " + jsonData);


                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject hourlyData = jsonObject.getJSONObject("hourly");

                    ArrayList<WeatherRecord> weatherRecords = WeatherRecord.fromJsonArray(hourlyData);

                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<Entry> humidityEntries = new ArrayList<>();
                    ArrayList<Entry> windspeedEntries = new ArrayList<>();

                    for (int i = 0; i < weatherRecords.size(); i++) {
                        float temperature = (float) weatherRecords.get(i).getTemperature();
                        float humidity = (float) weatherRecords.get(i).getHumidity();
                        float windspeed = (float) weatherRecords.get(i).getWindspeed();
                        Log.d(TAG, "Hour " + i + ": Temperature " + temperature + ", Humidity " + humidity + ", Windspeed " + windspeed);

                        entries.add(new Entry(i, temperature));
                        humidityEntries.add(new Entry(i, humidity));
                        windspeedEntries.add(new Entry(i, windspeed));
                    }

                    LineDataSet temperatureDataSet = new LineDataSet(entries, "Hourly Temperature (°C)");
                    temperatureDataSet.setColors(Color.RED);
                    LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Hourly Humidity (%)");
                    humidityDataSet.setColors(Color.BLUE);
                    LineDataSet windspeedDataSet = new LineDataSet(windspeedEntries, "Hourly Windspeed (km/h)");
                    windspeedDataSet.setColors(Color.GREEN);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(temperatureDataSet);
                    dataSets.add(humidityDataSet);
                    dataSets.add(windspeedDataSet);

                    LineData lineData = new LineData(dataSets);
                    lineChart.setData(lineData);
                    lineChart.invalidate();

                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            }
        };

        IntentFilter filter = new IntentFilter(WeatherWorker.WEATHER_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(WeatherWorker.WEATHER_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }
}