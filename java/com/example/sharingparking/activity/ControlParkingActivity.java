package com.example.sharingparking.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.entity.BlueTooth;
import com.example.sharingparking.utils.BluetoothChatService;
import com.example.sharingparking.utils.BluetoothReceiver;
import com.example.sharingparking.utils.ClsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.BT_REQUEST_ERROR;
import static com.example.sharingparking.common.Common.BT_REQUEST_FAIL;
import static com.example.sharingparking.common.Common.LOCK_BAR_STATE;
import static com.example.sharingparking.common.Common.LOCK_DOWN;
import static com.example.sharingparking.common.Common.LOCK_DOWNING_STATE;
import static com.example.sharingparking.common.Common.LOCK_DOWN_STATE;
import static com.example.sharingparking.common.Common.LOCK_OVER_UP_STATE;
import static com.example.sharingparking.common.Common.LOCK_UP;
import static com.example.sharingparking.common.Common.LOCK_UPPING_STATE;
import static com.example.sharingparking.common.Common.LOCK_UP_STATE;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.CommonUtil.toast;
import static com.example.sharingparking.utils.Utility.handleBlueToothResponse;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;
import static com.zyao89.view.zloading.Z_TYPE.DOUBLE_CIRCLE;

/**
 * 活动：通过蓝牙控制车位
 * Created by Lizhiguo on 2018/3/18.
 */

public class ControlParkingActivity extends AppCompatActivity implements BluetoothReceiver.BluetoothReceiverMessage{
    private String TAG = "ControlParkingActivity";

    private int userId;
    private int lockId;
    private int bluetoothId;

    // 来自BluetoothChatService Handler的信息类型
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //请求打开蓝牙回应码
    private static final int BLUETOOTH_RESPONSE = 1;
    //加载Dialog第三方类
    private ZLoadingDialog dialog;

    private BluetoothChatService mChatService = null;

    //蓝牙设备名称
    private String mConnectedDeviceName = "SHARKING_LOCK";
    //蓝牙设备地址
    private String mConnectedDeviceAddress = "98:D3:32:30:CA:36";
    //蓝牙设备
    private BluetoothDevice device = null;
    //车位锁状态
    private String lockState = "0";
    //蓝牙设备PIN
    private String PIN = "1111";
    //蓝牙广播接收器
    private BluetoothReceiver mBluetoothReceiver;
    //title控件
    private TextView txtTitleText;

    public static final String ACTION_INIT_DATA = "com.example.sharingparking.utils.BluetoothReceiver";

    //标识障碍物状态的变化标志
    private int barFlag = 0;

    @Override
    protected void onStart() {
        super.onStart();

        //请求打开蓝牙
        openBlueTooth();

        //请求判断蓝牙是否提前打开
        //如果没有提前打开则不会执行这句，执行openBluetooth的响应结果
        //如果提前打开则执行下面
        if(mBluetoothAdapter.isEnabled()){
            //请求服务器锁的信息
            requestBluetooth();
        }
    }

    //蓝牙未打开则打开蓝牙
    private void openBlueTooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            //通过这个方法来请求打开我们的蓝牙设备
           Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           startActivityForResult(intent,BLUETOOTH_RESPONSE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlparking);

        init();

    }

    private void init() {

        mBluetoothReceiver = new BluetoothReceiver();
        // 动态注册注册广播接收器。接收蓝牙发现讯息
        IntentFilter btFilter = new IntentFilter();
        btFilter.setPriority(1000);
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        btFilter.addAction(ACTION_INIT_DATA);
        this.registerReceiver(mBluetoothReceiver,btFilter);

        userId = getIntent().getIntExtra("userId",0);
        lockId = getIntent().getIntExtra("lockId",0);
        bluetoothId = getIntent().getIntExtra("blueToothId",bluetoothId);

        //设置广播信息接口监听器
        mBluetoothReceiver.setReceiverMessageListener(this);


        //添加活动到ActivityList中(安全退出)
        SysApplication.getInstance().addActivity(this);

        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        txtTitleText = (TextView) findViewById(R.id.txt_title_common);
        txtTitleText.setText(getIntent().getStringExtra("text_title"));
    }


    //蓝牙信息获取失败
    private void requestFail(){
        dialog.cancel();
        duringDialog("车位锁连接失败！");
        cancleSecondDialog();
        mBluetoothAdapter.cancelDiscovery();
    }

    private void requestBluetooth() {
        Log.d(TAG,bluetoothId + "intentBluetoothId");
        //请求蓝牙信息
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/getbluetoothbyid")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"blueToothId\":" + bluetoothId + "}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(ControlParkingActivity.this,BT_REQUEST_ERROR);
                        e.printStackTrace();
                        requestFail();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        BlueTooth bluetooth = handleBlueToothResponse(response);
                        if(bluetooth != null){

                            //蓝牙信息请求成功，将信息发送给广播
                            Intent intent = new Intent();
                            intent.setAction(ACTION_INIT_DATA);
                            intent.putExtra("bluetoothMAC",bluetooth.getBluetoothMAC());
                            intent.putExtra("bluetoothName",bluetooth.getBluetoothName());
                            intent.putExtra("pin",bluetooth.getBluetoothPassword());
                            sendBroadcast(intent);

                            //前台进行连接
                            duringDialog("正在连接蓝牙！");
                            searchBlueTooth();

                        }else if(handleMessageResponse(response) != null){
                            //提示错误信息
                            Toast.makeText(ControlParkingActivity.this,handleMessageResponse(response),
                                    Toast.LENGTH_SHORT).show();
                            requestFail();
                        }else{
                            //车位信息请求失败
                            Toast.makeText(ControlParkingActivity.this,BT_REQUEST_FAIL,
                                    Toast.LENGTH_SHORT).show();
                            requestFail();
                        }
                    }
                });

    }

    //搜索蓝牙设备
    private void searchBlueTooth() {
        //判断蓝牙是否已经绑定
        if(isBond()){
            Log.d(TAG,"蓝牙已经绑定");
            //已经绑定，直接连接蓝牙
            Log.d(TAG,device.getName() + "1");

        }else{
            Log.d(TAG,"搜索蓝牙中");
            //搜索蓝牙设备
            mBluetoothAdapter.startDiscovery();
            Log.d(TAG,mBluetoothAdapter.startDiscovery() + "");
    }
    }

    //判断蓝牙是否已经绑定
    private boolean isBond() {
        Set<BluetoothDevice> bondDevices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG,bondDevices.size() + "");
        for(BluetoothDevice bond : bondDevices){
            //设备已经配对
            Log.d(TAG,bond.getName());
            if(bond.getName().equals(mConnectedDeviceName)){
                device = bond;
                Log.d(TAG,device.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    /**
     * 控制车位上升
     */
    public void up(View view){
        //发送‘1#’控制上升
        sendMessage(LOCK_UP);
    }

    /**
     * 控制车位下降
     */
    public void down(View view){
        //发送‘0#’控制下降
        sendMessage(LOCK_DOWN);
    }

    /**
     * 重新连接蓝牙
     */
    public void reconnect(View view){
        if(!mBluetoothAdapter.isEnabled()){

            //请求打开蓝牙
            openBlueTooth();

        }

        //请求服务器锁的信息
        requestBluetooth();
    }

    //dialog动画
    private void duringDialog(String dialogText){
        dialog = new ZLoadingDialog(ControlParkingActivity.this);
        dialog.setLoadingBuilder(DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLACK)//颜色
                .setHintText(dialogText)
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setCanceledOnTouchOutside(false)
                .show();
    }

    //设置1秒后，取消dialog显示
    public void cancleSecondDialog(){
        new Handler().postDelayed(new Runnable(){
            public void run() {
                dialog.cancel();
            }
        }, 1000);
    }

    // 通过Handle获取BluetoothService返回的信息
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //蓝牙已连接

                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //蓝牙正在连接

                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //蓝牙未连接

                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //发送数据返回的结果

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //读取数据
                    Log.d(TAG,readMessage);

                    if(LOCK_BAR_STATE.equals(readMessage)){
                        //有障碍物
                        if(barFlag == 0){
                            //第一次接受有障碍物的消息，取消正在打开的dialog，设置障碍物dialog
                            dialog.cancel();
                            duringDialog("上方有障碍物！");
                            lockState = LOCK_BAR_STATE;
                            barFlag = 1;
                        }

                    }else {
                        if(LOCK_DOWN_STATE.equals(lockState)){
                            //锁处于0度状态
                            Log.d(TAG,readMessage);
                            if(LOCK_UPPING_STATE.equals(readMessage)){
                                //接收到上升回应值
                                Log.d(TAG,readMessage);
                                duringDialog("锁正在打开！");
                                lockState = LOCK_UPPING_STATE;
                            }
                        }else if(LOCK_UPPING_STATE.equals(lockState)){
                            //如果处于上升或下降状态
                            if(LOCK_UP_STATE.equals(readMessage)){
                                //上升成功
                                dialog.cancel();
                                duringDialog("打开成功");
                                cancleSecondDialog();
                                lockState = LOCK_UP_STATE;
                            }else if(LOCK_DOWN_STATE.equals(readMessage)){
                                //下降成功
                                dialog.cancel();
                                duringDialog("关闭成功");
                                cancleSecondDialog();
                                lockState = LOCK_DOWN_STATE;
                            }else{
                                //上升或者下降过程无操作
                            }
                        }else if(LOCK_UP_STATE.equals(lockState) || LOCK_OVER_UP_STATE.equals(lockState)){
                            //锁处于90度或者大于90度
                            if(LOCK_DOWNING_STATE.equals(readMessage)){
                                //接收到正在下降回应值
                                Log.d(TAG,readMessage);
                                duringDialog("锁正在关闭！");
                                lockState = LOCK_DOWNING_STATE;
                            }
                        }else if(LOCK_BAR_STATE.equals(lockState)){
                            dialog.cancel();
                            barFlag = 0;
                        }

                        lockState = readMessage;
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // 保存已连接的设备名称
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //取消搜索dialog
                    dialog.cancel();
                    //显示蓝牙连接成功dialog
                    duringDialog("蓝牙连接成功");
                    //蓝牙连接成功，0.5s后取消dialogUI显示
                    cancleSecondDialog();

                    break;
                case MESSAGE_TOAST:

                    break;
            }
        }
    };

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case BLUETOOTH_RESPONSE:
                if(resultCode == RESULT_OK){
                    //获得蓝牙权限成功
                    //需要再写一次，oncreate里的下面的方法无法执行
                    requestBluetooth();
                }else if(resultCode == RESULT_CANCELED){
                    //获得蓝牙权限失败
                    toast(ControlParkingActivity.this,"蓝牙权限获取失败，请打开蓝牙！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setMessage(BluetoothDevice device) {
        if(device != null){
            Log.d(TAG,"蓝牙绑定成功，开始连接！");
            this.device = device;
            mChatService.connect(device,true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothAdapter.cancelDiscovery();
        try {
            Log.d(TAG,device + "  " + device.getBondState());
            ClsUtils.removeBond(device.getClass(), device);
        } catch (Exception e) {
            Log.d(TAG,"解除绑定失败！");
            e.printStackTrace();
        }
    }

    /**
     * 切换到网络控制车位
     */
    public void controlByNet(View view){
        Intent intent = new Intent(ControlParkingActivity.this,ControlParkingByNetActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("lockId",lockId);
        startActivity(intent);
    }

}
