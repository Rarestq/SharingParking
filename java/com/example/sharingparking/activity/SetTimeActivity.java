package com.example.sharingparking.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.sharingparking.R;
import com.example.sharingparking.widget.CustomDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 设置开始时间和截止时间 父类
 * Created by Lizhiguo on 2018/4/17.
 */

public class SetTimeActivity extends AppCompatActivity{

    protected SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    protected TextView txtTitle;
    protected TextView txtStartTime;
    protected TextView txtEndTime;

    protected Date startTime;
    protected Date endTime;

    private CustomDatePicker startDatePicker, endDatePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_time);

        init();


    }


    private void init() {
        txtTitle = (TextView) findViewById(R.id.txt_title_common);

        txtTitle.setText(getIntent().getStringExtra("text_title"));
        txtStartTime = (TextView) findViewById(R.id.et_start_time);
        txtEndTime = (TextView) findViewById(R.id.et_end_time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        txtStartTime.setText(now);
        txtEndTime.setText(now);

        endDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                txtEndTime.setText(time);
                try {
                    endTime = mFormatter.parse(time);
                    endTime.setMinutes(0);
                    endTime.setSeconds(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, "2010-01-01 00:00", "2050-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        endDatePicker.showSpecificTime(true); // 显示时和分
        endDatePicker.setIsLoop(true); // 允许循环滚动

        startDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                txtStartTime.setText(time);
                try {
                    startTime = mFormatter.parse(time);
                    startTime.setMinutes(0);
                    startTime.setSeconds(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, "2010-01-01 00:00", "2050-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        startDatePicker.showSpecificTime(true); // 显示时和分
        startDatePicker.setIsLoop(true); // 允许循环滚动

    }


    //点击开始时间editText
    public void setStartTime(View view){
        //显示化时间选择器
        // 日期格式为yyyy-MM-dd HH:mm
        startDatePicker.show(txtStartTime.getText().toString());

    }
    //点击截止时间editText
    public void setEndTime(View view){
        // 日期格式为yyyy-MM-dd HH:mm
        endDatePicker.show(txtEndTime.getText().toString());
    }

    //点击保存
    public void saveTime(View view){
        protectedSaveTime();
    }

    //子类重写点击保存的事件
    protected void protectedSaveTime() {

    }


}
