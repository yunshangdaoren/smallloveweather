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
    @SerializedName("wind_dir")
    public String weatherInfo;

}
