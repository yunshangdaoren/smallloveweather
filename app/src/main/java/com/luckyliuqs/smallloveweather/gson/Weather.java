package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    //请求的状态
    @SerializedName("status")
    public String status;
    //定义城市的基本信息
    @SerializedName("basic")
    public Basic basic;
    //定义城市天气更新时间
    @SerializedName("update")
    public Update update;
    //当前空气质量情况
    //public AQI aqi;
    //当前天气信息
    @SerializedName("now")
    public Now now;
    //些天气相关的生活建议
    //@SerializedName("lifestyle")
   // public Suggestion suggestion;
    //来几天天气情况
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
