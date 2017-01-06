package com.fengniao.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by a1 on 2017/1/6.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
