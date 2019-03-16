package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 一些天气相关的生活建议
 */
public class Suggestion {
    //建议类型
    @SerializedName("type")
    public String type;
    //类型描述
    @SerializedName("brf")
    public String brf;
    //建议信息
    @SerializedName("txt")
    public String txt;

}
