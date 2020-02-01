package com.wenzhi.knowyouweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherid = preferences.getString("weather_id", null);

        if (weatherid != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("weather_id", weatherid);
            startActivity(intent);
        }
    }
}
