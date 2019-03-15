package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 一些天气相关的生活建议
 */
public class Suggestion {
    //舒适度
    @SerializedName("comf")
    public String comfort;
    //运动指数
    @SerializedName("sport")
    public String sport;
    //旅游指数
    @SerializedName("trav")
    public String traveling;
}
