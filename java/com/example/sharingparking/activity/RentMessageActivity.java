package com.example.sharingparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.adapter.RentMessageAdapter;
import com.example.sharingparking.entity.Ordering;
import com.example.sharingparking.entity.ParkingLock;
import com.example.sharingparking.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动：租用详情
 * Created by Lizhiguo on 2018/3/17.
 */

public class RentMessageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Ordering> mOrderingList = new ArrayList<>();

    private RentMessageAdapter mRentMessageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentmessage);

        //添加活动到ActivityList中(安全退出)
        SysApplication.getInstance().addActivity(this);

        initRentMessage();
        init();

    }

    private void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_rent_message);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_rent_message);

        //配置刷新列表
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                refreshParking();
            }
        });

        //适配车位信息到RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRentMessageAdapter = new RentMessageAdapter(mOrderingList);
        mRecyclerView.setAdapter(mRentMessageAdapter);
    }

    /**
     * 跳转到控制车位活动
     */
    public void controlParking(View view){
        Intent intent = new Intent(this,ControlParkingActivity.class);
        startActivity(intent);
    }

    /**
     * 结束租用
     */
    public void stopRent(View view){

    }


    private void refreshParking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //刷新UI界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //实际应该请求数据
                        initRentMessage();
                        mRentMessageAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initRentMessage() {
        Ordering ordering = new Ordering();
        User user = new User();
        ParkingLock parkingLock = new ParkingLock();



        mOrderingList.add(ordering);
    }
}
