package com.example.sharingparking.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 工具类：用于和服务器交互
 * Created by Lizhiguo on 2018/3/15.
 */

public class HttpUtil {
    //向服务器发起请求
    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
