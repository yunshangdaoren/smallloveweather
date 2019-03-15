package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 当前空气质量情况
 */
public class AQI {
    @SerializedName("city")
    public AQICity city;
    public class AQICity{
        @SerializedName("aqi")
        public String aqi;  //AQI指数
        @SerializedName("pm25")
        public String pm25;  //PM2.5施主
    }
}
