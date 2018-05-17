package com.example.sharingparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;

/**
 * Created by Lizhiguo on 2017/10/19.
 */

public class MainActivity extends AppCompatActivity{

    private String TAG = "MainActivity";

    private TextView txtUserName;   //用户名

    private String userName;    //用户名
    private int userId;     //用户ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //添加活动到ActivityList中
        SysApplication.getInstance().addActivity(this);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        initView();

    }

    /**
     * 初始化控件
     */
    private void initView(){
        //接收前一个活动传的用户名,用户ID
        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        userId = intent.getIntExtra("userId",0);

        Log.d(TAG,"userName：" + userName + "userId：" + userId);

        txtUserName = (TextView) findViewById(R.id.txt_username);
        txtUserName.setText(userName);
    }


    //跳转到我的钱包
    public void showWallet(View view){
        Intent intent = new Intent(this,WalletActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.my_wallet));
        intent.putExtra("userId",userId);
        startActivity(intent);

    }

    //跳转到我的订单
    public void showOrder(View view){
        Intent intent = new Intent(this,OrderActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.ordering_parking));
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    //跳转到我的优惠
    public void showBenefit(View view){
        Intent intent = new Intent(this,BenefitActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.my_benifit));
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    //跳转到我的车位
    public void showParking(View view){
        Intent intent = new Intent(this,ParkingActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.my_parking));
        intent.putExtra("userId",userId);
        startActivity(sendUserId(intent));
    }

    //跳转到设置
    public void showSetting(View view){
        Intent intent = new Intent(this,SettingActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.setting));
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    //从主界面跳转到租用详情
    public void toRentMessage(View view){
        Intent intent = new Intent(this,RentMessageActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.rent_parking_message));
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    //从主界面跳转到发布详情
    public void toPublishParking(View view){
        Intent intent = new Intent(this,PublishedActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.publish_parking_message));
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    //从主界面跳转到预定车位
    public void toOrderingParking(View view){
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("title_text",getResources().getText(R.string.ordering_parking));
        intent.putExtra("userId",userId);

//        Intent intent = new Intent(this, MapActivity.class);

        startActivity(intent);
    }

    /**
     * 将用户ID传入下一个活动
     * @param intent
     * @return
     */
    private Intent sendUserId(Intent intent){
        Bundle bundle = new Bundle();
        bundle.putInt("userId",userId);
        intent.putExtra("data",bundle);

        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //返回键，销毁所有活动
        SysApplication.getInstance().exit();
    }
}
