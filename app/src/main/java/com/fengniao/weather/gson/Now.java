package com.fengniao.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a1 on 2017/1/6.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
