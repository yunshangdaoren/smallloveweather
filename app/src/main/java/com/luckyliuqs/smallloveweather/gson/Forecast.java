package com.luckyliuqs.smallloveweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来几天天气情况
 */
public class Forecast {
    //预报的日期
    @SerializedName("date")
    public String date;
    //白天天气情况
    @SerializedName("cond_txt_d")
    public String weatherInfo_day;
    //最高温
    @SerializedName("tmp_max")
    public String temperature_max;
    //最低温
    @SerializedName("tmp_min")
    public String temperature_min;


}
