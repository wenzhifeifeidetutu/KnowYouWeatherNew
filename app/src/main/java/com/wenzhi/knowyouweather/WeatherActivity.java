package com.wenzhi.knowyouweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.wenzhi.knowyouweather.gson.AqiWeather;
import com.wenzhi.knowyouweather.gson.Froecast;
import com.wenzhi.knowyouweather.gson.FroecastWeather;
import com.wenzhi.knowyouweather.gson.LifeStyle;
import com.wenzhi.knowyouweather.gson.LifeStyleWeather;
import com.wenzhi.knowyouweather.gson.Now;
import com.wenzhi.knowyouweather.gson.NowWeather;
import com.wenzhi.knowyouweather.service.AutoUpdateService;
import com.wenzhi.knowyouweather.util.HttpUtil;
import com.wenzhi.knowyouweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    //下拉刷新
    public SwipeRefreshLayout swipeRefreshLayout;

    //侧滑布局
    private Button navButton;

    public DrawerLayout drawerLayout;

    //天气诗句
    public TextView contentText;

    //作者
    public TextView authroText;

    private String[] weathershi = new String[]{
            "风","云","雨","雪","霜","露", "雾","雷","晴","阴"

    };

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);

        navButton = (Button)findViewById(R.id.nav_button);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        contentText = (TextView)findViewById(R.id.content_text);

        authroText = (TextView)findViewById(R.id.author);


        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开侧滑栏

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //获取已经缓存的背景图片
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

//       去服务器查询天气数据
        final String weatherId = getIntent().getStringExtra("weather_id");

        weatherlayout.setVisibility(View.VISIBLE);
        //刷新天气数据
        requestWeather(weatherId);

//        增加下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences mysp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String sp = mysp.getString("weather_id", null);
                if (sp != null) {
                    requestWeather(sp);
                }else {
                    requestWeather(weatherId);

                }
            }
        });

        //打开定时任务
        Intent autoInten = new Intent(this, AutoUpdateService.class);
        startService(autoInten);

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
    public void requestWeather(String weatherId) {

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

                //获取诗句
                for (int i = 0; i < weathershi.length; i++) {
                    if (nowWeather.now.weatherText.contains(weathershi[i])) {
//                        请求诗句
                        String shiuri = "http://api.tianapi.com/txapi/tianqishiju/index?key=b89464eb3a045df413329379481743b6&tqtype="+(i+1);
                        HttpUtil.sendOkHttpRequest(shiuri, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                contentText.setVisibility(View.GONE);
                                authroText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                contentText.setVisibility(View.VISIBLE);
                                authroText.setVisibility(View.VISIBLE);
                                try {

                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    JSONArray jsonArray = jsonObject.getJSONArray("newslist");
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                    final String content = jsonObject1.getString("content");
                                    final String author = jsonObject1.getString("author");
                                    final String source = jsonObject1.getString("source");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (!TextUtils.isEmpty(content)) {
                                                contentText.setText(content);
                                            }

                                            if (!TextUtils.isEmpty(author)) {
                                                authroText.setText("-- " + author + "《" + source + "》");
                                            }


                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                }


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
                        aqiText.setText("暂无");
                        pm25Text.setText("暂无");
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
                            //展示
                            showAirWeather(aqiWeather);

                        }else {
                            aqiText.setText("暂无");
                            pm25Text.setText("暂无");
                        }
                    }
                });
            }
        });

        //请求背景图片
        loadBingImage();
        //调用刷新完成的时间
        swipeRefreshLayout.setRefreshing(false);




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
        String cityName = nowWeather.basic.locationName;
        String updaTime = nowWeather.update.updateTime.split(" ")[1];
        String degree = nowWeather.now.templerature + "℃";
        String weatherInfo = nowWeather.now.weatherText;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updaTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
    }




}
