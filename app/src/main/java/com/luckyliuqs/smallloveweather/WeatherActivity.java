package com.luckyliuqs.smallloveweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            Log.i("WeatherActivity", "本地有天气的缓存数据，那就读取数据吧 ");
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气情况
            String countyName = getIntent().getStringExtra("countyName");
            weatherLayout.setVisibility(View.INVISIBLE);
            Log.i("WeatherActivity", "没有天气的缓存数据，准备从服务器请求数据 ");
            requestWeather(countyName);
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
    }
    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String countyName){
        String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + countyName + "&key=dbc6bdff6cf9425a9fd2e65c5da6a918";
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
                            //如果服务器返回的status是OK，则说明请求天气信息成功
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //将返回的数据缓存到SharedPreferences当中
                            editor.putString("weather",responseText);
                            editor.apply();
                            Log.i("WeatherActivity", "==============weather不为空========== ");
                            //内容显示
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"请求方法里面的获取天气信息失败！",Toast.LENGTH_SHORT).show();;
                        }
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
                    }
                });
            }
        });

    }
    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        Log.i("WeatherActivity", "进入显示数据方法 ");
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.weatherInfo;
        titleCity.setText(cityName);
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
            maxText.setText(forecast.temperature_max);
            minText.setText(forecast.temperature_min);
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

}


