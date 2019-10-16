package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 背景图片类
 */
public class BackgroundImage {
    //图片状态码
    @SerializedName("code")
    public String status;
    @SerializedName("imgurl")
    public String imageUrl;
    @SerializedName("width")
    public String width;
    @SerializedName("height")
    public String height;
}
