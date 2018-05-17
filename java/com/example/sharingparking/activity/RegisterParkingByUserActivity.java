package com.example.sharingparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.adapter.LockAdapter;
import com.example.sharingparking.entity.ParkingLock;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.LOCK_REGISTER_ERROR;
import static com.example.sharingparking.common.Common.LOCK_REGISTER_FAIL;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.CommonUtil.linkParkingAddress;
import static com.example.sharingparking.utils.CommonUtil.toast;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;
import static com.example.sharingparking.utils.Utility.handleRegisterLockResponse;

/**
 * 注册车位
 */

public class RegisterParkingByUserActivity extends AppCompatActivity {
    private String TAG = "RegisterParkingByUser";

    private static final int BAIDU_READ_PHONE_STATE =100;

    private ImageButton btn_location;

    private EditText et_facilityID_user;

    private EditText et_carParking_address_user;

    private EditText et_carParking_detail_address_user;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<ParkingLock> mParkingLockList = new ArrayList<>();
    private int userId;     //用户ID
    private int facilityId;//车位id
    private String facilityAddr;//车位地址
    private String facilityDetailAddr;//车位详细地址

    private LockAdapter mLockAdapter;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public String currentLocation = "";  //当前位置

    //private ArrayAdapter<CarParkingInfo> mAdapter;

    //private static final String KEY_CARPARKING_LIST = "carParkingList"; //车位信息列表常量

  /*  private TextView txt_lock_no;

    private TextView txt_lock_address;

    private TextView txt_lock_state;

    private Button btn_publish;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerparking_user);
        startLocate();


        init();


        //添加活动到ActivityList中(安全退出)
        SysApplication.getInstance().addActivity(this);
    }

    private void init() {
        userId = getIntent().getIntExtra("userId",0);

        et_facilityID_user = (EditText) findViewById(R.id.et_facilityID_user);

        btn_location = (ImageButton) findViewById(R.id.btn_location);

        et_carParking_address_user = (EditText) findViewById(R.id.et_carParking_address_user);

        et_carParking_detail_address_user = (EditText) findViewById(R.id.et_carParking_detail_address_user);

    }

    /**
     * 定位
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuffer sb = new StringBuffer(256);
            /*sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());*/
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                Toast.makeText(getApplicationContext(), "GPS定位成功", Toast.LENGTH_SHORT).show();
                /*sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
*/
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
               Toast.makeText(getApplicationContext(), "网络定位成功", Toast.LENGTH_SHORT).show();
                /*sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");*/
            } /*else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }*/
            /*sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息*/

            //获取地理位置信息
            String currentAddr = location.getProvince()
                    + location.getCity()
                    + location.getDistrict()
                    + location.getStreet()
                    + location.getStreetNumber();
            sb.append(currentAddr);
            currentLocation = sb.toString();

           /* List<Poi> list = location.getPoiList();// POI数据
                if (list != null) {
                    //sb.append("\npoilist size = : ");
                    //sb.append(list.size());
                    for (Poi p : list) {
                        //sb.append("\npoi= : ");
                        //sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                        sb.append(p.getName());
                    }
            }
            currentLocation = sb.toString();*/
            Log.e("描述：", sb.toString());
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 点击imageButton，定位当前位置
     * @param view
     */
    public void locateCurrentAddr(View view) {

        et_carParking_address_user.setText(currentLocation);

    }


    /**
     * 点击保存按钮，保存车位信息到recyclerView中
     * @param view
     */
    public void toCarParkingRecyclerView(View view) {

        facilityId = Integer.parseInt(et_facilityID_user.getText().toString().trim());
        facilityAddr = et_carParking_address_user.getText().toString().trim();
        facilityDetailAddr = et_carParking_detail_address_user.getText().toString().trim();

        /**
         * 注册车位，发起http请求
         */
        registerParking();



    }

    /**
     *注册车位请求
     */
    private void registerParking() {
        Log.d(TAG,this.userId + "");
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/bindlock")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(new ParkingLock(this.facilityId,this.userId,
                        linkParkingAddress(this.facilityAddr,this.facilityDetailAddr))))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(RegisterParkingByUserActivity.this,LOCK_REGISTER_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        ParkingLock parkingLocks = handleRegisterLockResponse(response);
                        if(parkingLocks != null){
                            //车位注册成功,返回我的车位界面
                            Intent intent = new Intent(RegisterParkingByUserActivity.this,ParkingActivity.class);
                            startActivity(intent);

                        }else if(handleMessageResponse(response) != null){
                            //提示错误信息
                            Toast.makeText(RegisterParkingByUserActivity.this,handleMessageResponse(response),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            //车位注册失败
                            Toast.makeText(RegisterParkingByUserActivity.this,LOCK_REGISTER_FAIL,
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    //保存车位信息(SharedPreferences)
    /*public void saveCarParkingList() {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences(RegisterParkingByUserActivity.class.getName(), Context.MODE_PRIVATE).edit();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            String parkingInfo = mAdapter.getItem(i).getFacilityID()
                    + "\n" + mAdapter.getItem(i).getFacilityAddress()
                    + "\n" + mAdapter.getItem(i).getStatus();
            stringBuffer.append(parkingInfo).append(",");
        }

        if (stringBuffer.length() > 1) {  //车位信息列表为空时
            String content =  stringBuffer.toString().substring(0, stringBuffer.length()-1);
            editor.putString(KEY_CARPARKING_LIST, content);
            //System.out.println(content);
        } else {
            editor.putString(KEY_CARPARKING_LIST, null);
        }

        editor.commit();
    }

    //读取已保存的车位数据
    private void readSavedCarParkingList() {

        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(RegisterParkingByUserActivity.class.getName(), Context.MODE_PRIVATE);

        if (preferences != null) {
            String content = preferences.getString(KEY_CARPARKING_LIST, null);
            if (content != null) {
                String[] carParkingString = content.split(",");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(carParkingString.toString()
                                .getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
                String line;
                StringBuffer buffer = new StringBuffer();
                try {
                    while ((line = br.readLine()) != null) {
                        if(!line.trim().equals("")){
                            buffer.append(line+"\n");
                        }
                    }
                    //mAdapter.add(new CarParkingInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


    /*private class FacilityIDOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == et_facilityID.getId()) {
                Toast.makeText(getApplicationContext(), "请输入车位编号", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //editText获取焦点事件
    private class AddressOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == et_carParking_address.getId()) {
                et_carParking_address.setText(currentLocation);
            }
        }
    }*/

    /*public static class CarParkingInfo {  //车位信息

        String facilityID = "";   //设备id

        String facilityAddress = "";  //设备地址

        int status = 0;   //设备状态

        public CarParkingInfo(String facilityID, String facilityAddress, int status) {

            this.facilityID = facilityID;
            this.facilityAddress = facilityAddress;
            this.status = status;
        }

        public String getFacilityID() {
            return facilityID;
        }

        public void setFacilityID(String facilityID) {
            this.facilityID = facilityID;
        }

        public String getFacilityAddress() {
            return facilityAddress;
        }

        public void setFacilityAddress(String facilityAddress) {
            this.facilityAddress = facilityAddress;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "设备ID: " + this.getFacilityID()
                    + "\n设备地址: " + this.getFacilityAddress()
                    + "\n设备状态: " + this.getStatus();
        }
    }*/

    //工作人员注册
    public void staffRegister(){
        onBackPressed();
    }
}
