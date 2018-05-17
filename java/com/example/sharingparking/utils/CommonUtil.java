package com.example.sharingparking.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.zyao89.view.zloading.ZLoadingDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类：公共工具类
 * Created by Lizhiguo on 2018/3/17.
 */

public class CommonUtil {

    /**
     * 显示Toast
     */
    public static void toast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }

    /**
     * 计算租用时间
     */
    public static String caculateRentTime(String endTime,String startTime){
        return 10 + "";
    }

    /**
     * 将车位地址链接
     * 下划线分割
     */
    public static String linkParkingAddress(String locationAddress,String detailAddress){

        String address = locationAddress + "_" + detailAddress;

        return address;
    }


    /**
     * 将车位地址拆分
     */
    public static String[] splitParkingAddress(String address){

        String[] addresses = address.split("_");

        return addresses;
    }

    /**
     * 初始化title
     */
    public  static void initTitle(TextView txtTitle,String titleText){
        txtTitle.setText(titleText);//设置标题
    }

    /**
     * 验证是否为Double字符串
     */
    public static boolean isDoubleNumber(String string){

        try {
            Double number = Double.parseDouble(string);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 将发布类型 数字转换为相应的文字
     */
    public static String handlePublishStyle(Integer way){
        if(1 == way){
            return "时间可分割";
        }else if(2 == way){
            return "时间不可分割";
        }else {
            return "";
        }
    }

    /**
     * 将Date转换为yy-mm-dd hh
     */
    public static String dateToFormDate(Date date){
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return mFormatter.format(date);
    }

    //设置1秒后，取消dialog显示
    public static void cancelSecondDialog(ZLoadingDialog dialog){
        new Handler().postDelayed(new Runnable(){
            public void run() {
                dialog.cancel();
            }
        }, 1000);
    }



}
