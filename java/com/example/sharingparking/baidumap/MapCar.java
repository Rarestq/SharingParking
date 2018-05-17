package com.example.sharingparking.baidumap;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapCar implements Serializable {
    private double latitude;   //纬度
    private double longitude;  //经度
    private String userName;   //车位主
    private String parkingPrice;  //车位价格
    private String parkingAddress; //车位地址

    public MapCar() {

    }

    public MapCar(String userName, String parkingPrice, String parkingAddress) {
        this.userName = userName;
        this.parkingPrice = parkingPrice;
        this.parkingAddress = parkingAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getParkingPrice() {
        return parkingPrice;
    }

    public void setParkingPrice(String parkingPrice) {
        this.parkingPrice = parkingPrice;
    }

    public String getParkingAddress() {
        return parkingAddress;
    }

    public void setParkingAddress(String parkingAddress) {
        this.parkingAddress = parkingAddress;
    }

    /**
     * 内容接口描述，默认返回0即可
     * @return
     *//*
    @Override
    public int describeContents() {
        return 0;
    }

    *//**
     * 将对象序列化成一个Parcel对象，也就是将对象存入Parcel中
     * @param dest
     * @param flags
     *//*
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.userName);
        dest.writeString(this.parkingPrice);
        dest.writeString(this.parkingAddress);
    }

    public MapCar() {
    }

    *//**
     * 这里的读的顺序必须与writeToParcel(Parcel dest, int flags)方法中
     * 写的顺序一致，否则数据会有差错
     * @param in
     *//*
    protected MapCar(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.userName = in.readString();
        this.parkingPrice = in.readString();
        this.parkingAddress = in.readString();
    }

    public static final Creator<MapCar> CREATOR = new Creator<MapCar>() {

        *//**
         * 从Parcel中读取数据
         *//*
        @Override
        public MapCar createFromParcel(Parcel source) {
            return new MapCar(source);
        }

        *//**
         * 供外部类反序列化本类数组使用
         *//*
        @Override
        public MapCar[] newArray(int size) {
            return new MapCar[size];
        }
    };*/

    @Override
    public String toString() {
        return "MapCar{" +
                "latitude = " + latitude +
                "longitude = " + longitude +
                "userName = " + userName +
                ", parkingPrice = " + parkingPrice +
                ", parkingAddress" + parkingAddress +
                '\'' +
                '}';
    }
}
