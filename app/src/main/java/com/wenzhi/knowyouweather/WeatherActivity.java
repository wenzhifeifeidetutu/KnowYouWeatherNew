package com.wenzhi.knowyouweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wenzhi.knowyouweather.gson.AqiWeather;
import com.wenzhi.knowyouweather.gson.Froecast;
import com.wenzhi.knowyouweather.gson.FroecastWeather;
import com.wenzhi.knowyouweather.gson.LifeStyle;
import com.wenzhi.knowyouweather.gson.LifeStyleWeather;
import com.wenzhi.knowyouweather.gson.Now;
import com.wenzhi.knowyouweather.gson.NowWeather;
import com.wenzhi.knowyouweather.util.HttpUtil;
import com.wenzhi.knowyouweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherlayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private String TAG  = getClass().getSimpleName();


    private ImageView bingImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //进行状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            //5.0以上
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化控件

        weatherlayout =(ScrollView)findViewById(R.id.weather_layout);

        titleCity = (TextView)findViewById(R.id.title_city);

        titleUpdateTime = (TextView)findViewById(R.id.update_time_titile);

        degreeText = (TextView)findViewById(R.id.degree_text);

        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);

        aqiText = (TextView) findViewById(R.id.aqi_text);

        pm25Text = (TextView) findViewById(R.id.pm25_text);

        comfortText =(TextView)findViewById(R.id.comfort_text);

        carWashText = (TextView) findViewById(R.id.car_wash_text);

        sportText = (TextView)findViewById(R.id.sport_text);

        bingImageView = (ImageView)findViewById(R.id.bing_pc_img);

        String bingpic = PreferenceManager.getDefaultSharedPreferences(this).getString("bing_pic", null);

        if (bingpic != null) {
            //已经缓存过地址
            Glide.with(this).load(bingpic).into(bingImageView);
        }else {
            //进行服务器请求获取这个图片地址
            loadBingImage();
        }


        aqiText.setText("暂无");
        pm25Text.setText("暂无");
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        String weatherString = preferences.getString("weather", null);
//
//        if (weatherString != null) {
//            //有缓存直接解析天气数据
//            NowWeather nowWeather = Utility.handleWeatherResponse(weatherString);
//            showNowWeatherInfo(nowWeather);
//        }else {
//            去服务器查询天气数据
            String weatherId  = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
//        }
    }

    //请求背景图片的image背景
    private void loadBingImage() {
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                if (!TextUtils.isEmpty(bingPic)) {
                    editor.putString("bing_pic", bingPic);
                    editor.apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingImageView);
                        }
                    });
                }
            }
        });
    }

    //请求现在的天气信息
    private void requestWeather(String weatherId) {
        String weatherNowUrl = "https://free-api.heweather.net/s6/weather/now?location="+weatherId
                +"&key=cf22e815904f41c0964338d1389cc684";

        String weatherLifeStyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location="+weatherId
                +"&key=cf22e815904f41c0964338d1389cc684";

        String forecastWeatherUrl = "https://free-api.heweather.net/s6/weather/forecast?location="+weatherId
                +"&key=cf22e815904f41c0964338d1389cc684";

        String airUrl = "https://free-api.heweather.net/s6/air/now?location="+weatherId+"&key=cf22e815904f41c0964338d1389cc684";

        Log.d(TAG, "requestWeather: "+airUrl);

        //现在的天气
        HttpUtil.sendOkHttpRequest(weatherNowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                Log.d(TAG, "onResponse: "+responseText);
                final NowWeather nowWeather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nowWeather != null && "ok".equals(nowWeather.status)) {
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
//                                    .edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
                            //展示
                            showNowWeatherInfo(nowWeather);

                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //lifeStyleList
        HttpUtil.sendOkHttpRequest(weatherLifeStyleUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                Log.d(TAG, "onResponse: "+responseText);

                final LifeStyleWeather lifeStyleWeather = Utility.handleLifeStyleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeStyleWeather != null && "ok".equals(lifeStyleWeather.status)) {
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
//                                    .edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
                            //展示
                            showLifeStyleWeatherInfo(lifeStyleWeather);

                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //3天
        HttpUtil.sendOkHttpRequest(forecastWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                Log.d(TAG, "onResponse: "+responseText);
                final FroecastWeather froecastWeather = Utility.handleFroecatWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (froecastWeather != null && "ok".equals(froecastWeather.status)) {
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
//                                    .edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
                            //展示
                            showFroecastWeather(froecastWeather);

                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //展示aqi信息
        HttpUtil.sendOkHttpRequest(airUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                Log.d(TAG, "onResponse: "+responseText);
                final AqiWeather aqiWeather = Utility.handleAqiWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (aqiWeather != null && "ok".equals(aqiWeather.status)) {
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
//                                    .edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
                            //展示
                            showAirWeather(aqiWeather);

                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //请求背景图片
        loadBingImage();

    }

    //显示天气质量信息
    private void showAirWeather(AqiWeather aqiWeather) {
        if (!TextUtils.isEmpty(aqiWeather.airNowCity.aqi)) {
            aqiText.setText(aqiWeather.airNowCity.aqi);

        }
        if (!TextUtils.isEmpty(aqiWeather.airNowCity.pm25)) {
            pm25Text.setText(aqiWeather.airNowCity.pm25);

        }
    }


    //显示详细天气信息
    private void showFroecastWeather(FroecastWeather froecastWeather) {
        forecastLayout.removeAllViews();
        for (Froecast froecast : froecastWeather.froecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.data_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);

            //设置数据
            dateText.setText(froecast.date);
            infoText.setText("白天："+froecast.weatherTextNight+"\n晚上："+froecast.weatherTextNight);
            maxText.setText(froecast.temMax);
            minText.setText(froecast.temMin);

            forecastLayout.addView(view);
        }

    }


    //展示list的信息
    private void showLifeStyleWeatherInfo(LifeStyleWeather lifeStyleWeather) {
        for (LifeStyle lifeStyle : lifeStyleWeather.lifeStyleList) {
            if ("comf".equals(lifeStyle.type)) {
                //舒服度指数
                comfortText.setText("舒适度: "+lifeStyle.typeText+"\n "+lifeStyle.sugessionText);
            }else if ("cw".equals(lifeStyle.type)) {
                //洗车指数
                carWashText.setText("洗车指数："+lifeStyle.typeText+"\n "+lifeStyle.sugessionText);
            }else if ("sport".equals(lifeStyle.type)) {
                //运动指数
                sportText.setText("运动指数："+lifeStyle.typeText+"\n "+lifeStyle.sugessionText);
            }
        }

        weatherlayout.setVisibility(View.VISIBLE);

    }

    //展示天气信息
    private void showNowWeatherInfo(NowWeather nowWeather) {
        String cityName = nowWeather.basic.cityName;
        String updaTime = nowWeather.update.updateTime.split(" ")[0];
        String degree = nowWeather.now.templerature + "℃";
        String weatherInfo = nowWeather.now.weatherText;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updaTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
    }




}
