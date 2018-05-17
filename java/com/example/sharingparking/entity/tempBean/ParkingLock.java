package com.example.sharingparking.entity.tempBean;

import java.io.Serializable;

public class ParkingLock implements Serializable {
    private Integer lockId;         //锁编号
    private Integer userId;         //锁的拥有者ID
    private Integer blueToothId;    //蓝牙id
    private String address;         //车位位置
    private Integer lockState;      //锁的状态
    private Integer infrared;       //红外线状态
    private Integer led;            //LED灯状态
    private Integer battery;        //电池状态

    @Override
    public String toString() {
        return "ParkingLock{" +
                "lockId=" + lockId +
                ", userId=" + userId +
                ", blueToothId=" + blueToothId +
                ", address='" + address + '\'' +
                ", lockState=" + lockState +
                ", infrared=" + infrared +
                ", led=" + led +
                ", battery=" + battery +
                '}';
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getBlueToothId() {
        return blueToothId;
    }

    public void setBlueToothId(Integer blueToothId) {
        this.blueToothId = blueToothId;
    }
}
