package com.fengniao.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a1 on 2017/1/6.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
