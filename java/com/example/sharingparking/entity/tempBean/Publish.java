package com.example.sharingparking.entity.tempBean;

import java.io.Serializable;
import java.util.Date;

public class Publish implements Serializable {
    private Integer publishId;             //发布编号
    private Date publishStartTime;         //发布开始时间
    private Date publishEndTime;           //发布截止时间
    private User user;                     //锁的拥有者
    private ParkingLock lock;              //车锁
    private Double parkingMoney;           //车位金额 每小时
    private Integer publishState;          //发布状态    1有效 2自动失效 3用户取消 [1-10）
                                           //不可分割发布 11    12      13      （10-20）

    @Override
    public String toString() {
        return "Publish{" +
                "publishId=" + publishId +
                ", publishStartTime=" + publishStartTime +
                ", publishEndTime=" + publishEndTime +
                ", parkingMoney=" + parkingMoney +
                ", publishState=" + publishState +
                '}';
    }

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

    public Double getParkingMoney() {
        return parkingMoney;
    }

    public void setParkingMoney(Double parkingMoney) {
        this.parkingMoney = parkingMoney;
    }

    public Integer getPublishState() {
        return publishState;
    }

    public void setPublishState(Integer publishState) {
        this.publishState = publishState;
    }
}
