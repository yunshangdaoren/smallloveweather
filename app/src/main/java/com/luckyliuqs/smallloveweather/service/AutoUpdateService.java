package com.luckyliuqs.smallloveweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import com.luckyliuqs.smallloveweather.gson.Weather;
import com.luckyliuqs.smallloveweather.util.HttpUtil;
import com.luckyliuqs.smallloveweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用于后台自动更新天气
 */
public class AutoUpdateService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager =(AlarmManager) getSystemService(ALARM_SERVICE);
        //设置后台更新天气间隔8个小时
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    /**
     * 更新天气
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //读取天气信息
        final String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有天气数据缓存时直接解析数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String countyName = weather.basic.countyName;
            String weatherUrl = "https://free-api.heweather.net/s6/weather?location=" + countyName + "&key=dbc6bdff6cf9425a9fd2e65c5da6a918";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather != null && weather.status.equals("ok")){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
