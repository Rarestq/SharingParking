package com.example.sharingparking.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.sharingparking.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.NET_URL_HEADER;


/**
 * 通过网络控制车位
 * Created by Lizhiguo on 2018/4/17.
 */

public class ControlParkingByNetActivity extends AppCompatActivity {
    private String TAG = "ControlParkingByNet";

    private int userId;
    private int lockId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlparking_net);
        init();
    }

    private void init() {
        userId = getIntent().getIntExtra("userId",0);
        lockId = getIntent().getIntExtra("lockId",0);
    }

    /**
     * 控制车位上升
     */
    public void up(View view){
        //发送‘1#’控制上升
        Log.d(TAG,"上升！");
        Log.d(TAG,"{\"msg\":"+ "\"" + lockId + "1#\"}");
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/temp")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"msg\":"+ "\"" + lockId + "1#\"}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        toast(ControlParkingByNetActivity.this,);
//                        //取消刷新效果
//                        mSwipeRefreshLayout.setRefreshing(false);
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);

                    }
                });
    }

    /**
     * 控制车位下降
     */
    public void down(View view){
        //发送‘0#’控制下降
        //发送‘1#’控制上升
        Log.d(TAG,"下降！");
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/temp")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"msg\":"+ "\"" + lockId + "0#\"}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        toast(ControlParkingByNetActivity.this,);
//                        //取消刷新效果
//                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {


                    }
                });
    }

    public void controlByBluetooth(View view){
        onBackPressed();
    }


}
