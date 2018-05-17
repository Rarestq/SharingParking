package com.example.sharingparking.interfaces;

import com.example.sharingparking.entity.User;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by Lizhiguo on 2017/11/21.
 */

public abstract class UserCallback extends Callback<User> {

    @Override
    public User parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        User user = new Gson().fromJson(string,User.class);

        return user;
    }
}
