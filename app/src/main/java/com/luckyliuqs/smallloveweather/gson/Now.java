package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 当前天气信息
 */
public class Now {
    //天气温度
    @SerializedName("tmp")
    public String temperature;
    //天气情况
    @SerializedName("cond_txt")
    public String weatherInfo;
    //相对湿度
    @SerializedName("hum")
    public String humidity;
    //能见度
    @SerializedName("vis")
    public String visibility;
}
