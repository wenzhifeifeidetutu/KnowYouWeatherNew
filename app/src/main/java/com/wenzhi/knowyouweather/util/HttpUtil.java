package com.wenzhi.knowyouweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Description: okhttp的封装
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 14:13
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 14:13
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(address).build();

        client.newCall(request).enqueue(callback);
    }
}
