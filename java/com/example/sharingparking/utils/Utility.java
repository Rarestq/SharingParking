package com.example.sharingparking.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.sharingparking.entity.BlueTooth;
import com.example.sharingparking.entity.Ordering;
import com.example.sharingparking.entity.ParkingLock;
import com.example.sharingparking.entity.Publish;
import com.example.sharingparking.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 解析和处理各种Json数据
 * Created by Lizhiguo on 2018/3/15.
 */

public class Utility {
    public static final String UTILITY_TAG = "Utility";
    /**
     * 解析和处理用户Json数据
     * @param response
     * @return
     */
    public static User handleUserResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                Log.d(UTILITY_TAG,"user : " + response);
                JSONObject userObject = new JSONObject(response);
                User user = getUserJson(userObject);
                return user;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取用户Json数据
     * @param jsonObject
     * @return
     */
    private static User getUserJson(JSONObject jsonObject){
        User user = new User();

        try {
            user.setUserName(jsonObject.getString("userName"));
            user.setUserId(handleInteger(jsonObject,"userId"));
            user.setPhoneNumber(jsonObject.getString("phoneNumber"));
            user.setPassword(jsonObject.getString("password"));
            user.setUserMoney(jsonObject.getDouble("userMoney"));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BlueTooth handleBlueToothResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject bluetoothObject = new JSONObject(response);
                BlueTooth blueTooth = getBlueToothJson(bluetoothObject);

                return blueTooth;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析注册锁的信息
     * @param response
     * @return
     */
    public static ParkingLock handleRegisterLockResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {

                JSONObject locksJSONObject = new JSONObject(response);
                ParkingLock parkingLock = getParkingLockJson(locksJSONObject);

                return parkingLock;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 解析和处理车位锁Json数据
     * @param response
     * @return
     */
    public static List<ParkingLock> handleLockResponse(String response){

        if(!TextUtils.isEmpty(response)){
            try {
                List<ParkingLock> list = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0;i < jsonArray.length();i++){
                    JSONObject locksJSONObject = jsonArray.getJSONObject(i);
                    ParkingLock parkingLock = getParkingLockJson(locksJSONObject);
                    list.add(parkingLock);
                }
                return list;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取车位锁json数据
     * @param jsonObject
     * @return
     */
    private static ParkingLock getParkingLockJson(JSONObject jsonObject){

        try {
            ParkingLock parkingLock = new ParkingLock();
            parkingLock.setLockId(handleInteger(jsonObject,"lockId"));
            parkingLock.setUserId(handleInteger(jsonObject,"userId"));
            parkingLock.setBlueToothId(handleInteger(jsonObject,"blueToothId"));
            parkingLock.setAddress(jsonObject.getString("address"));
            parkingLock.setBattery(handleInteger(jsonObject,"battery"));
            parkingLock.setInfrared(handleInteger(jsonObject,"infrared"));
            parkingLock.setLed(handleInteger(jsonObject,"led"));
            parkingLock.setLockState(handleInteger(jsonObject,"lockState"));

            return parkingLock;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理和解析车位发布Json信息
     * @param response
     * @return
     */
    public static List<Publish> handlePublishResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                List<com.example.sharingparking.entity.tempBean.Publish> tempList = JSON.
                        parseArray(response, com.example.sharingparking.entity.tempBean.Publish.class);

                List<Publish> list = new ArrayList<>();
                for(int i = 0;i < tempList.size();i++){
                    Publish publish = getPublishJson(tempList.get(i));

                    list.add(publish);
                }
                return list;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 处理和解析车位发布Json信息
     * @param response
     * @return
     */
    public static Publish handlePublishObjectResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                com.example.sharingparking.entity.tempBean.Publish tempPublish =
                        JSON.parseObject(response, com.example.sharingparking.entity.tempBean.Publish.class);
                Publish publish = getPublishJson(tempPublish);

                return publish;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        return null;
    }

    /**
     * 获取车位锁发布json数据
     * @param
     * @return
     */
    private static Publish getPublishJson(com.example.sharingparking.entity.tempBean.Publish tempPublish) {
        Publish publish = new Publish();
        publish.setUserId(tempPublish.getUser().getUserId());
        publish.setLockId(tempPublish.getLock().getLockId());
        publish.setParkingMoney(tempPublish.getParkingMoney());
        publish.setPublishStartTime(tempPublish.getPublishStartTime());
        publish.setPublishEndTime(tempPublish.getPublishEndTime());
        publish.setPublishId(tempPublish.getPublishId());
        publish.setPublishState(tempPublish.getPublishState());
        User user  = new User();
        user.setUserId(tempPublish.getUser().getUserId());
        user.setUserName(tempPublish.getUser().getUserName());
        user.setPhoneNumber(tempPublish.getUser().getPhoneNumber());
        publish.setUser(user);
        ParkingLock parkingLock = new ParkingLock();
        parkingLock.setLockId(tempPublish.getLock().getLockId());
        parkingLock.setAddress(tempPublish.getLock().getAddress());
        parkingLock.setLockState(tempPublish.getLock().getLockState());
        publish.setLock(parkingLock);
        return publish;
    }

    public static List<Ordering> handleOrderResponse(String response){

        if(!TextUtils.isEmpty(response)){
            try{
                List<com.example.sharingparking.entity.tempBean.Publish> tempList = JSON.
                        parseArray(response, com.example.sharingparking.entity.tempBean.Publish.class);

                List<Ordering> list = new ArrayList<>();
                for(int i = 0;i < tempList.size();i++){
                    Ordering  ordering = getOrderingJson(tempList.get(i));

                    list.add(ordering);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        return  null;
    }

    /**
     * 获取车位锁预定json数据
     * @param
     * @return
     */
    private static Ordering getOrderingJson(com.example.sharingparking.entity.tempBean.Publish tempPublish) {
        Ordering ordering = new Ordering();

        ParkingLock parkingLock = new ParkingLock();
        parkingLock.setLockId(tempPublish.getLock().getLockId());
        parkingLock.setAddress(tempPublish.getLock().getAddress());
        parkingLock.setLockState(tempPublish.getLock().getLockState());
        ordering.setLock(parkingLock);
        User user  = new User();
        user.setUserId(tempPublish.getUser().getUserId());
        user.setUserName(tempPublish.getUser().getUserName());
        user.setPhoneNumber(tempPublish.getUser().getPhoneNumber());
        ordering.setUser(user);
        ordering.setStartTime(tempPublish.getPublishStartTime());
        ordering.setEndTime(tempPublish.getPublishEndTime());
        ordering.setExpense(tempPublish.getParkingMoney());

        return ordering;
    }


    /**
     * 解析日期
     */
    private static Date handleDate(String dateString) {
        Date date = new Date(Long.valueOf(dateString));

        return date;
    }

    /**
     * 处理和解析异常Json提示信息
     * @param response
     * @return
     */
    public static String handleMessageResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                Log.d(UTILITY_TAG,"error : " + response);
                JSONObject userObject = new JSONObject(response);
                String message = userObject.getString("msg");
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /**
     * 获取蓝牙json数据
     * @param jsonObject
     * @return
     */
    private static BlueTooth getBlueToothJson(JSONObject jsonObject){
        BlueTooth blueTooth = new BlueTooth();
        try {
            blueTooth.setBluetoothId(handleInteger(jsonObject,"blueToothId"));
            blueTooth.setBluetoothName(jsonObject.getString("blueToothName"));
            blueTooth.setBluetoothPassword(jsonObject.getString("blueToothPassword"));
            blueTooth.setBluetoothState(handleInteger(jsonObject,"bluetoothState"));
            blueTooth.setBluetoothMAC(jsonObject.getString("macAddress"));

            return blueTooth;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 处理Integer数据
     * @param jsonObject
     * @param key
     * @return
     */
    private static Integer handleInteger(JSONObject jsonObject,String key){

        try {
            Integer data = jsonObject.getInt(key);
            return data;
        } catch (JSONException e) {
            return null;
        }

    }

}
