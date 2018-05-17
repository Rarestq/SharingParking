package com.example.sharingparking.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.adapter.GirdDropDownAdapter;
import com.example.sharingparking.adapter.ListDropDownAdapter;
import com.example.sharingparking.adapter.OrderingAdapter;
import com.example.sharingparking.baidumap.MapActivity;
import com.example.sharingparking.entity.Publish;
import com.example.sharingparking.widget.DropDownMenu;
import com.zhl.cbdialog.CBDialogBuilder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.LOCK_ORDERING_REQUEST_ERROR;
import static com.example.sharingparking.common.Common.LOCK_ORDERING_REQUEST_FAIL;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.CommonUtil.cancelSecondDialog;
import static com.example.sharingparking.utils.CommonUtil.initTitle;
import static com.example.sharingparking.utils.CommonUtil.toast;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;
import static com.example.sharingparking.utils.Utility.handlePublishResponse;
import static com.zyao89.view.zloading.Z_TYPE.DOUBLE_CIRCLE;

/**
 * 活动：显示附近已发布的车位，预订车位
 * Created by Lizhiguo on 2017/11/29.
 */

public class OrderActivity extends AppCompatActivity implements OrderingAdapter.OrderingInterface{

    private String TAG = "PublishedActivity";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txtTitle;

    private List<Publish> mPublishes = new ArrayList<>();
    private int userId;     //用户ID

    private OrderingAdapter mOrderingAdapter;

    /**
     * 筛选框
     */
    @BindView(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    //筛选框
    private List<View> popupViews = new ArrayList<>();
    //筛选框头部内容
    private String headers[] = {"距离最近","最近预定"};
    //筛选框数据
    private String [] selectConditions = {"距离最近","时间优先","评价优先"};
    private String [] recentOrdering = {"南昌","上海"};
    public static int ORDERING_CONDITION = 0;

    //筛选框适配器
    private ListDropDownAdapter selectConditionAdapter;
    private GirdDropDownAdapter mGirdDropDownAdapter;

    //加载Dialog第三方类
    private ZLoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_common);

        //添加活动到ActivityList中(安全退出)
        SysApplication.getInstance().addActivity(this);

        //从intent中获取数据
        userId = getIntent().getIntExtra("userId",0);

        ButterKnife.bind(this);
        init();


    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * 设置刷新
         * activity重新显示或首次进入后，请求车位信息
         */
        mSwipeRefreshLayout.measure(0,0);
        mSwipeRefreshLayout.setRefreshing(true);

        //发起请求(仅查询)
        requestOrdering();

    }

    //请求订单信息
    private void requestOrdering() {
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "publish/querypublishnotmy")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"userId\":" + userId + "}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(OrderActivity.this,LOCK_ORDERING_REQUEST_ERROR);
                        //取消刷新效果
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        List<Publish> publishes = handlePublishResponse(response);
                        if(publishes != null){
                            //车位发布信息请求成功，更新UI
                            mPublishes.clear();
                            mPublishes.addAll(publishes);

                        }else if(handleMessageResponse(response) != null){
                            if("empty".equals(handleMessageResponse(response))){
                                mPublishes.clear();
                            }
                            //提示错误信息
                            Toast.makeText(OrderActivity.this,handleMessageResponse(response),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            //车位信息请求失败
                            Toast.makeText(OrderActivity.this,LOCK_ORDERING_REQUEST_FAIL,
                                    Toast.LENGTH_SHORT).show();
                        }

                        //刷新UI界面
                        //放到外面由于多线程无法及时接收
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"请求完毕");
                                mOrderingAdapter.notifyDataSetChanged();
                                //取消刷新效果
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });

    }

    private void init() {

        //初始化筛选条件筛选框
        final ListView selectConditionsView = new ListView(this);
        selectConditionsView.setDividerHeight(0);
        selectConditionAdapter = new ListDropDownAdapter(this, Arrays.asList(selectConditions));
        selectConditionsView.setAdapter(selectConditionAdapter);

        //初始化最近预定筛选框
        final ListView recentOrderingView = new ListView(this);
        mGirdDropDownAdapter = new GirdDropDownAdapter(this, Arrays.asList(recentOrdering));
        recentOrderingView.setDividerHeight(0);
        recentOrderingView.setAdapter(mGirdDropDownAdapter);


        //添加筛选框
        popupViews.add(selectConditionsView);
        popupViews.add(recentOrderingView);

        //添加点击事件
        selectConditionsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectConditionAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : selectConditions[position]);
                mDropDownMenu.closeMenu();
            }
        });

        //初始化布局
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews,
                LayoutInflater.from(OrderActivity.this).inflate(R.layout.activity_ordering, null));

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_my_ordering);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_ordering);
        txtTitle = (TextView) findViewById(R.id.txt_title_common);
        initTitle(txtTitle,getIntent().getStringExtra("title_text"));

        //适配发布信息到RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mOrderingAdapter = new OrderingAdapter(mPublishes);
        mRecyclerView.setAdapter(mOrderingAdapter);

        //设置按钮点击事件接口监听
        mOrderingAdapter.setOrderingInterface(this);

        //配置刷新列表
        //设置颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //配置监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestOrdering();
            }
        });

    }

    /**
     * 预订车位
     * @param publishId
     */
    @Override
    public void orderingParking(Integer publishId) {

        requestOrdered(publishId);

    }

    /**
     * 预订车位
     * @param publishId
     */
    private void requestOrdered(Integer publishId) {

        duringDialog("正在预定...");

        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "publish/querypublishnotmy")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"userId\":" + userId + ",\"publishId\"" + publishId + "}")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast(OrderActivity.this,LOCK_ORDERING_REQUEST_ERROR);
                        dialog.cancel();
                        duringDialog("预定失败");
                        cancelSecondDialog(dialog);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        String msg = handleMessageResponse(response);
                        if(msg != null){
                            if("succeed".equals(msg)){
                                //预订成功
                                sessionDialog(OrderActivity.this,"预订成功","查看订单","取消");
                            }else {
                                dialog.cancel();
                                duringDialog("预定失败");
                                cancelSecondDialog(dialog);
                            }
                        }else{
                            //车位信息请求失败
                            dialog.cancel();
                            duringDialog("预定失败");
                            cancelSecondDialog(dialog);
                        }
                    }
                });

    }

    /**
     * 创建预订成功对话框
     * @param context
     * @param title
     * @param btnConfirmText
     * @param btnCancelText
     */
    private void sessionDialog(Context context,String title,String btnConfirmText,String btnCancelText){

        //不同样式
//        CBDialogBuilder.DIALOG_STYLE_NORMAL
//        CBDialogBuilder.DIALOG_STYLE_PROGRESS
//        CBDialogBuilder.DIALOG_STYLE_PROGRESS_TITANIC
//        CBDialogBuilder.DIALOG_STYLE_PROGRESS_AVLOADING

        new CBDialogBuilder(context)
                .setTouchOutSideCancelable(false)// 设置是否点击对话框以外的区域dismiss对话框
                .showCancelButton(true) //是否显示取消按钮
                .setTitle(title)
                .setConfirmButtonText(btnConfirmText)
                .setCancelButtonText(btnCancelText)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM) //设置对话框的动画样式
                .setDialoglocation(CBDialogBuilder.MSG_LAYOUT_CENTER)  //设置对话框位于屏幕的位置
                .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() { //添加按钮回调监听
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case BUTTON_CONFIRM:
                                //去支付
                                Intent intent = new Intent(OrderActivity.this,RentMessageActivity.class);
                                intent.putExtra("title_text",getResources().getText(R.string.ordering_detail));
                                intent.putExtra("userId",userId);
                                startActivity(intent);
                                break;
                            case BUTTON_CANCEL:
                                //取消

                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
    }

    /**
     * 显示地图
     * @param publish
     */
    @Override
    public void showMap(Publish publish) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("positionPublish",publish);
        intent.putExtra("publishList", (Serializable) mPublishes);
        startActivity(intent);
    }


    //dialog动画
    private void duringDialog(String dialogText){
        dialog = new ZLoadingDialog(OrderActivity.this);
        dialog.setLoadingBuilder(DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLACK)//颜色
                .setHintText(dialogText)
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setCanceledOnTouchOutside(false)
                .show();
    }

}
