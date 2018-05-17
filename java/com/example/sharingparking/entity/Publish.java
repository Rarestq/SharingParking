package com.example.sharingparking.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类：车位发布
 * Created by Lizhiguo on 2018/3/26.
 */

public class Publish implements Serializable{
    private Integer publishId;          //发布编号
    private Date publishStartTime;   //发布开始时间
    private Date publishEndTime;  //发布截止时间
    private Integer userId;     //用户ID
    private Integer lockId;   //锁
    private double parkingMoney;   //车位金额
    private Integer publishState;   //发布状态
    private Integer way;   //发布方式
    private User user;
    private ParkingLock lock;

    public Integer getPublishId() {
        return publishId;
    }

    public void setPublishId(Integer publishId) {
        this.publishId = publishId;
    }

    public Date getPublishStartTime() {
        return publishStartTime;
    }

    public void setPublishStartTime(Date publishStartTime) {
        this.publishStartTime = publishStartTime;
    }

    public Date getPublishEndTime() {
        return publishEndTime;
    }

    public void setPublishEndTime(Date publishEndTime) {
        this.publishEndTime = publishEndTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public double getParkingMoney() {
        return parkingMoney;
    }

    public void setParkingMoney(double parkingMoney) {
        this.parkingMoney = parkingMoney;
    }

    public Integer getPublishState() {
        return publishState;
    }

    public void setPublishState(Integer publishState) {
        this.publishState = publishState;
    }

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParkingLock getLock() {
        return lock;
    }

    public void setLock(ParkingLock lock) {
        this.lock = lock;
    }
}
