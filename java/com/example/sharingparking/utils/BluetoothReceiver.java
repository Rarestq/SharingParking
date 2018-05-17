package com.example.sharingparking.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.sharingparking.activity.ControlParkingActivity.ACTION_INIT_DATA;

/**
 * 蓝牙广播接收器
 * Created by Lizhiguo on 2018/4/9.
 */

public class BluetoothReceiver extends BroadcastReceiver {
    private String TAG = "BluetoothReceiver";
    //蓝牙设备对象
    private BluetoothDevice device;
    //蓝牙名字
//    private String btName = "SHARKING_LOCK";
    private String btName;
    //蓝牙地址
//    private String btAddress = "98:D3:32:30:CA:36";
    private String btAddress;
    //蓝牙PIN
//    private String pin = "1111";
    private String pin;
    //蓝牙绑定状态
    public static int BLUETOOTH_BONDED = 1;//蓝牙已绑定

    private BluetoothReceiverMessage mReceiverMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        //获得action
        String action=intent.getAction();

        if(ACTION_INIT_DATA.equals(action)){
            //接收从activity中传来的数据
            btName = intent.getStringExtra("bluetoothName");
            btAddress = intent.getStringExtra("bluetoothMAC");
            pin = intent.getStringExtra("pin");
            Log.d(TAG,btName);
        }else{
            //获取设备
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //获取搜索到的设备的名称和地址
            String name = device.getName();
            String address = device.getAddress();

            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                Log.d(TAG, "发现设备:" + name);

                if ((name != null && btName.equals(name)) || btAddress.equals(address)) {
                    //判断远程设备是否已经被绑定
                    if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                        Log.d(TAG, "发现目标设备，开始配对!");
                        try {
                            // 调用配对的方法，此方法是异步的，系统会触发BluetoothDevice.ACTION_PAIRING_REQUEST的广播
                            // 收到此广播后，设置配对的密码
                            ClsUtils.createBond(BluetoothDevice.class, device);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        //远程设备已经被绑定，取消搜索，返回信息

                    }


                }

            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                //搜索结束

            }else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                //蓝牙开关状态改变
            }else if(action.equals("android.bluetooth.device.action.PAIRING_REQUEST")){
                //createBond后再次得到action，就会等于PAIRING_REQUEST
                Log.d(TAG,"action2 = " + action);
                Log.d(TAG, "发现设备:" + device.getAddress() + " " + device.getName());

                if ((name != null && name.equals(btName)) || address.equals(btAddress)) {
                    Log.d(TAG, "发现目标设备，开始配对!");
                    try {

                        //1.确认配对
//                    ClsUtils.setPairingConfirmation(device.getClass(), device, true);
//                    //2.终止有序广播
//                    Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                        abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                        //3.调用setPin方法进行配对...
                        boolean ret = ClsUtils.setPin(device.getClass(), device, pin);

                        mReceiverMessage.setMessage(device);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG,"action2 = 配对失败！");
                    }
                }else {
                    //提示不是目标设备
                    Log.d(TAG,"action2 = 不是目标设备！");
                }
            }
        }

        Log.d(TAG,"Receiver结束");
    }

    public interface BluetoothReceiverMessage{
        public void setMessage(BluetoothDevice device);
    }

    public void setReceiverMessageListener(BluetoothReceiverMessage bluetoothReceiverMessage){
        this.mReceiverMessage = bluetoothReceiverMessage;
    }

}
