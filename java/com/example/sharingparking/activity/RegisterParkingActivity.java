package com.example.sharingparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharingparking.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.LOCK_REGISTER_ERROR;
import static com.example.sharingparking.common.Common.LOCK_REGISTER_FAIL;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.CommonUtil.toast;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;

/**
 * Created by Lizhiguo on 2018/4/19.
 */

public class RegisterParkingActivity extends AppCompatActivity{
    private String TAG;

    EditText etLockId;

    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerparking);

        init();

    }

    private void init() {

        userId = getIntent().getIntExtra("userId",0);
        etLockId = (EditText) findViewById(R.id.et_facilityID);
    }

    public void registerLock(View view) {
        if("".equals(etLockId.getText().toString())){
            toast(RegisterParkingActivity.this,"请输入车位锁ID");
        }else{
            requestRegister();
        }
    }


    //请求注册车位锁
    private void requestRegister() {

        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/bindexistlock")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"userId\":" + userId + ",\"lockId\":"
                        + Integer.parseInt(etLockId.getText().toString()) + "}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(RegisterParkingActivity.this,LOCK_REGISTER_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        String msg = handleMessageResponse(response);
                        if(msg != null){
                            if("success".equals(msg)){
                                onBackPressed();
                            }
                            //提示信息
                            Toast.makeText(RegisterParkingActivity.this,msg,
                                        Toast.LENGTH_SHORT).show();

                        }else{
                            //车位注册失败
                            Toast.makeText(RegisterParkingActivity.this,LOCK_REGISTER_FAIL,
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    //跳转到用户自行注册界面
    public void userRegister(View view){
        Intent intent = new Intent(RegisterParkingActivity.this,RegisterParkingByUserActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

}
