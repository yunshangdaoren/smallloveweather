package com.luckyliuqs.smallloveweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luckyliuqs.smallloveweather.gson.Forecast;
import com.luckyliuqs.smallloveweather.gson.Weather;
import com.luckyliuqs.smallloveweather.util.HttpUtil;
import com.luckyliuqs.smallloveweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView travelText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String refreshCountyName;
    private DrawerLayout drawerLayout;
    //按钮：用于显示滑动菜单
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断版本号是否大于21
        if(Build.VERSION.SDK_INT >= 21){
            //获取当前活动的DecorView
            View decorVeiw = getWindow().getDecorView();
            //设置系统UI的显示，表示活动的布局会显示在状态栏上面
            decorVeiw.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            Log.i("WeatherActivity", "本地有天气的缓存数据，那就读取数据吧 ");
            refreshCountyName = weather.basic.countyName;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气情况
            refreshCountyName = getIntent().getStringExtra("countyName");
            //String countyName = getIntent().getStringExtra("countyName");
            weatherLayout.setVisibility(View.INVISIBLE);
            Log.i("WeatherActivity", "没有天气的缓存数据，准备从服务器请求数据 ");
            requestWeather(refreshCountyName);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(refreshCountyName);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPicImg != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

    }
    /**
     * 初始化控件
     */
    public void initView(){
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        travelText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
    }
    /**
     * 根据城市名称请求城市天气信息
     */
    public void requestWeather(final String countyName){
        final String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + countyName + "&key=dbc6bdff6cf9425a9fd2e65c5da6a918";
        Log.i("WeatherActivity", "requestWeather（）方法：开始请求数据 ");
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            Log.i("WeatherActivity", "获取到的weather不为空 ");
                            //如果服务器返回的status是OK，则说明请求天气信息成功
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //将返回的数据缓存到SharedPreferences当中
                            editor.putString("weather",responseText);
                            editor.apply();
                            refreshCountyName = weather.basic.countyName;
                            //内容显示
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取到的weather为，空请求方法里面的获取天气信息失败！",Toast.LENGTH_SHORT).show();;
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        //加载必应每日一图图片
        loadBingPic();
    }
    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        Log.i("WeatherActivity", "进入显示数据方法 ");
        String countyName = weather.basic.countyName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.weatherInfo;
        titleCity.setText(countyName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(int i = 0;i<weather.forecastList.size();i++){
            Forecast forecast = weather.forecastList.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherInfo_day);
            maxText.setText(forecast.temperature_max + "℃");
            minText.setText(forecast.temperature_min + "℃");
            forecastLayout.addView(view);
        }
       // if(weather.aqi != null){
           // aqiText.setText(weather.aqi.city.aqi);
           // pm25Text.setText(weather.aqi.city.pm25);
       // }
       // String comfort = "舒适度："+weather.suggestion.comfort;
       // String sport = "运动指数："+weather.suggestion.sport;
        //String traveling = "旅游指数："+weather.suggestion.traveling;
        //comfortText.setText(comfort);
        //sportText.setText(sport);
        //travelText.setText(traveling);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    /**
     * 加载必应每日一图图片
     */
    public void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}


