package com.example.sharingparking.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.example.sharingparking.R;
import com.example.sharingparking.entity.Publish;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.INPUT_NOT_COMPLETE;
import static com.example.sharingparking.common.Common.LOCK_PUBLISH_ERROR;
import static com.example.sharingparking.common.Common.LOCK_PUBLISH_FAIL;
import static com.example.sharingparking.common.Common.LOCK_PUBLISH_SUCCESS;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.CommonUtil.isDoubleNumber;
import static com.example.sharingparking.utils.CommonUtil.toast;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;
import static com.example.sharingparking.utils.Utility.handlePublishObjectResponse;

/**
 * 发布车位
 * Created by Lizhiguo on 2018/4/17.
 */

public class PublishActivity extends SetTimeActivity {

    private String TAG = "PublishActivity";

    private int userId;
    private int lockId;
    private EditText etParkingPrice;

    //重写保存时间
    @Override
    protected void protectedSaveTime() {
        //检查是否为空
        if(isEmpty()){
            toast(PublishActivity.this,INPUT_NOT_COMPLETE);
        }else if(!isDoubleNumber(etParkingPrice.getText().toString())){
            //检查输入的值是否为double
            toast(PublishActivity.this,INPUT_NOT_COMPLETE);
        }
        else{
            //不为空，请求服务器注册
            requestPublish();
        }
    }

    private void requestPublish() {
        Publish publish = new Publish();

        publish.setLockId(lockId);
        publish.setUserId(userId);
        publish.setPublishStartTime(startTime);
        publish.setPublishEndTime(endTime);
        publish.setParkingMoney(Double.parseDouble(etParkingPrice.getText().toString()));
        publish.setWay(1);

        Log.d(TAG,JSON.toJSONString(publish));

        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "publish/dopublish")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(JSON.toJSONString(publish))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(PublishActivity.this,LOCK_PUBLISH_ERROR);
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        if(handlePublishObjectResponse(response) != null){
                            toast(PublishActivity.this,LOCK_PUBLISH_SUCCESS);
                            onBackPressed();
                        } else if(handleMessageResponse(response) != null){
                            toast(PublishActivity.this,handleMessageResponse(response));
                        }else {
                            toast(PublishActivity.this,LOCK_PUBLISH_FAIL);
                        }

                    }
                });
        

    }

    private boolean isEmpty() {
        if("点击按钮设置开始时间".equals(txtStartTime.getText().toString()) ||
                "点击按钮设置截止时间".equals(txtEndTime.getText().toString())){
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //调用父类方法
        super.onCreate(savedInstanceState);
        init();


    }

    private void init(){

        userId = getIntent().getIntExtra("userId",0);
        lockId = getIntent().getIntExtra("lockId",0);

        etParkingPrice = (EditText) findViewById(R.id.et_parking_price);
    }

}
