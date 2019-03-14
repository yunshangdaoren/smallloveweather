package com.luckyliuqs.smallloveweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    //县id
    private int id;
    //县名称
    private String countyName;
    //县对应的天气id
    private String weatherId;
    //县所属市id
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
