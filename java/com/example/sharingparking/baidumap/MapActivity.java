package com.example.sharingparking.baidumap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.sharingparking.R;
import com.example.sharingparking.entity.Publish;
import com.example.sharingparking.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements BaiduMap.OnMarkerClickListener,
        BaiduMap.OnMapClickListener, BaiduMap.OnMapStatusChangeListener, OnGetGeoCoderResultListener {

    @BindView(R.id.mMapView)
    MapView mMapView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private static final String TAG = "MapActivity";
    @BindView(R.id.dh)
    FloatingActionButton dh;
    @BindView(R.id.mn)
    FloatingActionButton mn;

    @BindView(R.id.detail_address)
    EditText editDetailAddress; //搜索框中的地址
    /* @BindView(R.id.geocodekey)
     EditText editGeoCodeKey;*/
    @BindView(R.id.lat)
    EditText lat;
    @BindView(R.id.lon)
    EditText lon;
    @BindView(R.id.geocode)
    Button searchAddress;
    @BindView(R.id.reversegeocode)
    Button reversegeocode;

    GeoCoder mSearch = null;  //搜索模块
    String detail_address = "";   //要搜索的地址
    GeoCodeOption mOption;

   // private AlertDialog dialog;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLoacationListener myLocationListener;
    private boolean isFirstIn = true;

    private double mLatitude;
    private double mLongitude;
    private BitmapDescriptor mLoacationBitmap;
    private String address= "";  //除图标外的地址
    private String markerAddress = "";  //图标地址

    private MyOrentationListener myOrentationListener;
    private float mCurrentX;//当前位置

    private MyLocationConfiguration.LocationMode mLocationMode;

    //覆盖物
    private BitmapDescriptor mMarker;

    //infowindow
    private BitmapDescriptor mBitmapWindow;
    private InfoWindow infoWindow;

    //显示的动画
    private TranslateAnimation shwoAction;
    //隐藏时的动画
    private TranslateAnimation hideAction;

    MarkerOptions options;

    //当前选择的经纬度
    private LatLng currentLatLng = null;

    protected Publish mPublish;   //车位发布
    protected String userName;        //车主用户名
    protected String parking_price;  //车位价格
    protected String parking_address; //车位地址

    protected List<Publish> mPublishList = new ArrayList<>();  //车位发布列表

    protected double address2Latitude; // 通过地址得到的经纬度
    protected double address2Longitude;

    protected List<MapCar> infos;  //车位信息列表


    //相关权限
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;
    private String APP_FOLDER_NAME = "TMap";
    private String mSDCardPath = null;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static List<Activity> activityList = new LinkedList<Activity>();
    private boolean hasInitSuccess = false;//百度导航是否初始化成功
    private boolean hasRequestComAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        initView();

        initLocation();

        if (initDirs()) {
            initNavigatin();
        }

        //得到车位发布列表
        mPublishList = (List<Publish>) getIntent().getSerializableExtra("publishList");
       /* for (int i = 0; i < mPublishList.size(); i++) {
           userName = mPublishList.get(i).getUser().getUserName();
           parking_price = "" + mPublishList.get(i).getParkingMoney();
           parking_address = mPublishList.get(i).getLock().getAddress();

        }
        String[] address = CommonUtil.splitParkingAddress(parking_address);
        parking_address = address[0];*/

        mPublish = (Publish) getIntent().getSerializableExtra("positionPublish");
        userName = mPublish.getUser().getUserName();
        parking_price = "" + mPublish.getParkingMoney();
        parking_address = CommonUtil.splitParkingAddress(mPublish.getLock().getAddress())[0];

        initMarker();
        setMarkerInfo();
    }

    //添加覆盖物信息
    protected void setMarkerInfo() {
        infos = new ArrayList<>();
        for(int i = 0;i < mPublishList.size();i++) {
            MapCar mapCar = new MapCar();
            mapCar.setUserName(mPublishList.get(i).getUser().getUserName());
            mapCar.setParkingPrice("" + mPublishList.get(i).getParkingMoney());
            mapCar.setParkingAddress(mPublishList.get(i).getLock().getAddress());
            infos.add(mapCar);
        }
    }



    String authinfo = null;

    /***************************************
     * 导航部分
     ***************************************************/
    private void initNavigatin() {
        //申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }
        //初始化导航
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                MapActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void initStart() {
                Toast.makeText(MapActivity.this, "百度导航初始化开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void initSuccess() {
                Toast.makeText(MapActivity.this, "百度导航初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            @Override
            public void initFailed() {
                Toast.makeText(MapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        }, null, ttsHandler, ttsPlayStateListener);
    }


    /**
     * 内部TTS播报状态回传handler
     */
    @SuppressLint("HandlerLeak")
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    Log.d(TAG, "Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    Log.d(TAG, "Handler : TTS play end");
                    break;
                }
                default:
                    showToastMsg("TTS验证失败");
                    Log.d(TAG, "TTS验证失败");
                    break;
            }
        }
    };

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10468764");
        Log.d(TAG, "设置APPID");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            Log.d(TAG, "TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            Log.d(TAG, "TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        MapActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化文件路径
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    //判断是否有Sd卡
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }


    private boolean hasBasePhoneAuth() {
        if (Build.VERSION.SDK_INT >= 23) {
            PackageManager pm = this.getPackageManager();
            for (String auth : authComArr) {
                if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 发起搜索
     *
     * @param v
     */

    public void searchButtonProcess(View v) {
        if (v.getId() == R.id.reversegeocode) {
            LatLng ptCenter = new LatLng((Float.valueOf(lat.getText()
                    .toString())), (Float.valueOf(lon.getText().toString())));
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        } else if (v.getId() == R.id.geocode) {
            //editDetailAddress = (EditText) findViewById(R.id.detail_address);
           // EditText editGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
            // Geo搜索
            editDetailAddress.setText(parking_address);
            detail_address = editDetailAddress.getText().toString();
           // mOption = new GeoCodeOption().address(detail_address);
            mSearch.geocode(new GeoCodeOption().address(detail_address));
           // mSearch.geocode(new GeoCodeOption().city(
                    //editDetailAddress.getText().toString()).address(editGeoCodeKey.getText().toString()));
        }
    }



    private void routeplanToNavi(LatLng currentLatLng, boolean isSimulation) {
        BNRoutePlanNode.CoordinateType mCoordinateType = BNRoutePlanNode.CoordinateType.BD09LL;
        if (!hasInitSuccess) {
            Toast.makeText(MapActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(MapActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }

        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;


//        sNode = new BNRoutePlanNode(113.9512500000, 22.5489710000, "出发地", null, mCoordinateType);
//        eNode = new BNRoutePlanNode(113.9410840000, 22.5460020000, "终点站", null, mCoordinateType);

        sNode = new BNRoutePlanNode(mLongitude, mLatitude, "起始地", null, mCoordinateType);
        eNode = new BNRoutePlanNode(currentLatLng.longitude, currentLatLng.latitude, "目的地", null, mCoordinateType);

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            //开始导航
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, isSimulation, new DemoRoutePlanListener(sNode));
        }
    }

    /**
     * Geo搜索
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.placeholder)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 反Geo搜索
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.placeholder)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        Toast.makeText(MapActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }


    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {

            //设置途径点以及resetEndNode会回调该接口
            for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("BNGuideActivity")) {
                    return;
                }
            }
            Intent intent = new Intent(MapActivity.this, BNGuideActivity.class);
            Bundle bundle = new Bundle();
            //bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(MapActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /*****************************************************************************************/
    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.placeholder);
        /*List<MapCar> list = new ArrayList<>();
        for(int i = 0;i < mPublishList.size();i++) {
            MapCar mapCar = new MapCar();
            mapCar.setUserName(mPublishList.get(i).getUser().getUserName());
            mapCar.setParkingPrice("" + mPublishList.get(i).getParkingMoney());
            mapCar.setParkingAddress(mPublishList.get(i).getLock().getAddress());
            list.add(mapCar);
        }*/
        addOverLays();
    }

    private void initView() {

        shwoAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        shwoAction.setDuration(300);

        hideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        hideAction.setDuration(300);


        mBaiduMap = mMapView.getMap();
        //隐藏缩放控件
        mMapView.showZoomControls(true);
        //是否显示比例尺，默认true
        mMapView.showScaleControl(true);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        //隐藏百度logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls))
            child.setVisibility(View.INVISIBLE);

        //覆盖物的点击事件
        mBaiduMap.setOnMarkerClickListener(this);
        //地图的点击事件
        mBaiduMap.setOnMapClickListener(this);
        //地图状态事件
        mBaiduMap.setOnMapStatusChangeListener(this);
        mBaiduMap.setMyLocationEnabled(true);//开启定位图层
        MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(15.0F);
        mBaiduMap.setMapStatus(status);


    }

    //定位
    private void initLocation() {

        mLocationClient = new LocationClient(getApplicationContext());
        myLocationListener = new MyLoacationListener();
        mLocationClient.registerLocationListener(myLocationListener); //注册监听函数

        LocationClientOption options = new LocationClientOption();
        options.setCoorType("bd09ll");//坐标类型
        options.setIsNeedAddress(true);
        options.setAddrType("all");
        options.setOpenGps(true);
        options.setScanSpan(1000);

        mLocationClient.setLocOption(options);

        //模式
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;

        mLoacationBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);

        myOrentationListener = new MyOrentationListener(getApplicationContext());
        myOrentationListener.setmListener(new MyOrentationListener.OnOrientationListner() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1: //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.item_2://卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.item_3:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通ON");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通OFF");
                }
                break;
            case R.id.item_4:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.item_5://跟随模式
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.item_6://罗盘模式
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.item_7:
                addOverLays(MapCar.infos);
                break;
            case R.id.item_8:
                Toast.makeText(this, currentLatLng.latitude + "\n" + currentLatLng.longitude, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

   //通过地址得到经纬度
    public GeoPoint getGeoPointBystr(String str) {
        GeoPoint gpGeoPoint = null;
        if (str!=null) {
            Geocoder gc = new Geocoder(MapActivity.this, Locale.CHINA);
            List<Address> addressList = null;
            try {

                addressList = gc.getFromLocationName(str, 1);
                if (!addressList.isEmpty()) {
                    Address address_temp = addressList.get(0);
                    //计算经纬度
                    address2Latitude = address_temp.getLatitude() * 1E6;
                    address2Longitude = address_temp.getLongitude() * 1E6;
                    System.out.println("纬度：" + address2Latitude);
                    System.out.println("经度：" + address2Longitude);
                    //生产GeoPoint
                    gpGeoPoint = new GeoPoint((int)address2Latitude, (int)address2Longitude);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gpGeoPoint;
    }

   //添加图层
    private void addOverLays(List<MapCar> infos) {
        //首先清楚定位的浮层
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;

        /*List<MapCar> list = new ArrayList<>();
        for(int i = 0;i < mPublishList.size();i++) {
            MapCar mapCar = new MapCar();
            mapCar.setUserName(mPublishList.get(i).getUser().getUserName());
            mapCar.setParkingPrice("" + mPublishList.get(i).getParkingMoney());
            mapCar.setParkingAddress(mPublishList.get(i).getLock().getAddress());
            list.add(mapCar);

            //经纬度
            latLng = new LatLng(getGeoPointBystr(mPublishList.get(i).getLock().getAddress()).getLatitudeE6(),
                    getGeoPointBystr(mPublishList.get(i).getLock().getAddress()).getLongitudeE6());
            //添加自定义图标
            options = new MarkerOptions()
                    .position(latLng)      //设置marker的位置
                    .icon(mMarker)         //设置marker的图标
                    .zIndex(5);            //设置marker所在的层级
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("carInfo", mapCar);
            marker.setExtraInfo(bundle);
        }*/

        for (MapCar car : infos) {
            //经纬度
            latLng = new LatLng(car.getLatitude(), car.getLongitude());
            //添加自定义图标
            options = new MarkerOptions()
                    .position(latLng) // 设置marker的位置
                    .icon(mMarker)    // 设置marker的图标
                    .zIndex(5);       // 设置marker所在的层级
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("car", car);
            marker.setExtraInfo(bundle);
        }

        //定位到覆盖物的位置，应该是最后一个覆盖物的位置，如果不想定位到覆盖物的位置而是定位到人的位置，注释掉
        //MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        //mBaiduMap.setMapStatus(msu);
    }

    @OnClick({R.id.fab, R.id.dh, R.id.mn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                LatLng lng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(lng);
                mBaiduMap.animateMapStatus(status);
                break;
            case R.id.dh:
                routeplanToNavi(currentLatLng, true);
                break;
            case R.id.mn:
                confirmRent();
                break;
        }

    }

    //确定租用
    public void confirmRent() {

    }

    //覆盖物的点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        markerAddress = "";
        Bundle bundle = marker.getExtraInfo();
        MapCar car = (MapCar) bundle.getSerializable("car");

        //将marker所在的经纬度的信息转化成屏幕上的坐标
        final LatLng lng = marker.getPosition();
        //获取经纬度
        double latitude = lng.latitude;
        double longitude = lng.longitude;
        System.out.println("latitude = " + latitude + ",longitude = " + longitude);
        //依据当前给出的经纬度获取该地地址信息
       // LatLng point = new LatLng(latitude, longitude);
        // 构建MarkerOption，用于在地图上添加Marker
        // MarkerOptions options = new MarkerOptions().position(point).icon(mMarker);
        // 在地图上添加Marker，并显示
        // mBaiduMap.addOverlay(options);

        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
        op.location(lng);
        //发起反地理编码请求(经纬度->地址信息)
        geoCoder.reverseGeoCode(op);

        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            String  temporaryMarkerAddress;
            //经纬度->地址信息
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

                temporaryMarkerAddress = arg0.getAddressDetail().province
                        + arg0.getAddressDetail().city
                        + arg0.getAddressDetail().district
                        + arg0.getAddressDetail().street
                        + arg0.getAddressDetail().streetNumber;
                System.out.println("markerAddress = " + temporaryMarkerAddress);
                markerAddress = temporaryMarkerAddress;
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {
            }
        });

        //将infoWindow中的内容设置放在线程中，否则第一次点击覆盖物时为空值
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(markerAddress == ""){}
                //生成一个TextView用于用户在地图中显示InfoWindow
                TextView infoText = new TextView(getApplicationContext());
                infoText.setBackgroundResource(R.mipmap.location_tips);
                infoText.setPadding(30, 20, 30, 30);

                infoText.setText("车位主：" + userName + "\n车位价格：" + parking_price +
                        "\n地址：" + parking_address);
                System.out.println("地址: " + parking_address);
                // infoText.setText("经度：" + car.getLongitude() + '\n' + "纬度：" + car.getLatitude() + "\n地址：" + markerAddress);
                // infoText.setText(stringBuffer);
                infoText.setTextColor(Color.parseColor("#FFFFFF"));

                mBitmapWindow = BitmapDescriptorFactory.fromView(infoText);

                Point p = mBaiduMap.getProjection().toScreenLocation(lng);
                //让弹框在Y轴偏移47
                p.y -= 47;
                //定义用于显示该InfoWindow的坐标点
                currentLatLng = mBaiduMap.getProjection().fromScreenLocation(p);

                //为弹出的InfoWindow添加点击事件
                infoWindow = new InfoWindow(mBitmapWindow, currentLatLng, 0, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                        stopAnimation();
                    }
                });
                mBaiduMap.showInfoWindow(infoWindow);
            }

        }).start();
        startAnimation();
        return true;
    }

    private void startAnimation() {
        if (dh.getVisibility() == View.VISIBLE || mn.getVisibility() == View.VISIBLE)
            return;
        dh.startAnimation(shwoAction);
        mn.startAnimation(shwoAction);
        dh.setVisibility(View.VISIBLE);
        mn.setVisibility(View.VISIBLE);
    }

    private void stopAnimation() {
        if (dh.getVisibility() == View.GONE || mn.getVisibility() == View.GONE)
            return;
        dh.startAnimation(hideAction);
        mn.startAnimation(hideAction);
        dh.setVisibility(View.GONE);
        mn.setVisibility(View.GONE);
    }

    @Override
    public void onMapClick(LatLng latLng) {   //地图点击事件(获取当前地址)
        //获取经纬度
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        System.out.println("latitude = " + latitude + ",longitude = " + longitude);

        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);

        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
        op.location(latLng);
        //发起反地理编码请求(经纬度->地址信息)
        geoCoder.reverseGeoCode(op);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                //获取点击的坐标地址
                address = arg0.getAddressDetail().province
                        + arg0.getAddressDetail().city
                        + arg0.getAddressDetail().district
                        + arg0.getAddressDetail().street
                        + arg0.getAddressDetail().streetNumber;
                System.out.println("address = " + address);
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {
            }
        });
        mBaiduMap.hideInfoWindow();
        stopAnimation();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    /**
     * 手势操作地图
     *
     * @param mapStatus
     */
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    /**
     * 地图变化中
     *
     * @param mapStatus
     */
    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        mBaiduMap.hideInfoWindow();
        stopAnimation();
    }

    /**
     * 地图状态改变
     *
     * @param mapStatus
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {

    }

    private class MyLoacationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)//方向
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();

            mBaiduMap.setMyLocationData(data);

            //得到经纬度
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();

            /**
             * NORMAL:不会实时更新位置
             * COMPASS:实时更新位置,罗盘
             * FOLLOWING:跟随模式
             */
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, mLoacationBitmap);
            mBaiduMap.setMyLocationConfigeration(config);
//            mBaiduMap.setMyLocationEnabled(true);//当不需要定位图层时关闭定位图层

            if (isFirstIn) {
                LatLng latLbg = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());//经纬度
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLbg);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;

                Toast.makeText(MapActivity.this, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT > 19) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        //开启方向传感器
        myOrentationListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //关闭方向传感器
        myOrentationListener.stop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(MapActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavigatin();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            routeplanToNavi(currentLatLng, true);
        }

    }
}

