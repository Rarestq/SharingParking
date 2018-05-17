package com.example.sharingparking.entity;

/**
 * 锁的地址
 * Created by Lizhiguo on 2018/4/16.
 */

public class LockAddress {

    private Integer lockAddressId;//锁的地址ID
    private String lockLocationAddress;//锁的定位地址
    private String lockDetailAddress;//锁的详细地址

    public Integer getLockAddressId() {
        return lockAddressId;
    }

    public void setLockAddressId(Integer lockAddressId) {
        this.lockAddressId = lockAddressId;
    }

    public String getLockLocationAddress() {
        return lockLocationAddress;
    }

    public void setLockLocationAddress(String lockLocationAddress) {
        this.lockLocationAddress = lockLocationAddress;
    }

    public String getLockDetailAddress() {
        return lockDetailAddress;
    }

    public void setLockDetailAddress(String lockDetailAddress) {
        this.lockDetailAddress = lockDetailAddress;
    }
}
