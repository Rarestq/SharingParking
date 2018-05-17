package com.example.sharingparking.entity;

import java.io.Serializable;

/**
 * 实体类：锁信息
 * Created by Lizhiguo on 2018/3/15.
 */

public class ParkingLock implements Serializable{
    private Integer lockId;     //锁编号
    private Integer userId;     //用户ID
    private Integer blueToothId;    //蓝牙ID
    private String address;     //车位位置
    private Integer lockState;  //锁的状态
    private Integer infrared;   //红外线状态
    private Integer led;        //LED灯状态
    private Integer battery;    //电池状态

    public ParkingLock() {
    }

    public ParkingLock(Integer lockId, Integer userId, String address) {
        this.lockId = lockId;
        this.userId = userId;
        this.address = address;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBlueToothId() {
        return blueToothId;
    }

    public void setBlueToothId(Integer blueToothId) {
        this.blueToothId = blueToothId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLockState() {
        return lockState;
    }

    public void setLockState(Integer lockState) {
        this.lockState = lockState;
    }

    public Integer getInfrared() {
        return infrared;
    }

    public void setInfrared(Integer infrared) {
        this.infrared = infrared;
    }

    public Integer getLed() {
        return led;
    }

    public void setLed(Integer led) {
        this.led = led;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }
}
