package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

public class Update {
    //天气更新时间
    @SerializedName("loc")
    public String updateTime;
}
