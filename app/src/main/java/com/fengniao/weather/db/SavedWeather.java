package com.fengniao.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by a1 on 2017/1/11.
 */

public class SavedWeather extends DataSupport {
    private int id;
    private String weatherId;
    private String weatherData;
    private String countyName;


    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(String weatherData) {
        this.weatherData = weatherData;
    }
}

