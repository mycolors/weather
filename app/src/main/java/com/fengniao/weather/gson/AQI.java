package com.fengniao.weather.gson;

/**
 * Created by a1 on 2017/1/6.
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
