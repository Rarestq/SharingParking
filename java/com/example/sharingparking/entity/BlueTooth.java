package com.example.sharingparking.entity;

/**
 * 实体类：蓝牙
 * Created by Lizhiguo on 2018/3/26.
 */

public class BlueTooth {
    private Integer bluetoothId;    //蓝牙编号
    private String bluetoothName;   //蓝牙名称
    private String bluetoothPassword;   //蓝牙密码
    private Integer bluetoothState;  //蓝牙状态
    private String bluetoothMAC;    //蓝牙MAC地址

    public Integer getBluetoothId() {
        return bluetoothId;
    }

    public void setBluetoothId(Integer bluetoothId) {
        this.bluetoothId = bluetoothId;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothPassword() {
        return bluetoothPassword;
    }

    public void setBluetoothPassword(String bluetoothPassword) {
        this.bluetoothPassword = bluetoothPassword;
    }

    public Integer getBluetoothState() {
        return bluetoothState;
    }

    public void setBluetoothState(Integer bluetoothState) {
        this.bluetoothState = bluetoothState;
    }

    public String getBluetoothMAC() {
        return bluetoothMAC;
    }

    public void setBluetoothMAC(String bluetoothMAC) {
        this.bluetoothMAC = bluetoothMAC;
    }
}
