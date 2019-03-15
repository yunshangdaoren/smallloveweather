package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 定义城市的基本信息
 */
public class Basic {
    @SerializedName("location")
    public String cityName;     //城市名称
}
