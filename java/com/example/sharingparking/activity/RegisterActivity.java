package com.example.sharingparking.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.entity.User;
import com.example.sharingparking.utils.Utility;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.common.Common.REGISTER_USER_ERROR;
import static com.example.sharingparking.common.Common.REGISTER_USER_FAIL;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;


/**
 * Created by Lizhiguo on 2017/10/20.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private String mPhoneString;        //暂时保存手机号码

    private EditText etRegisterUserName;        //注册用户名
    private EditText etRegisterPassword;        //注册密码
    private Button btnRegister;                 //注册按钮
    private CardView cvRegister;                //注册卡片视图
    private Button btnGetIdentifyCode;         //获取验证码按钮
    private EditText etIdentifyCode;          //验证码输入框

    private int messageSendTime = 60;       //设置信息冲新发送时间为60s

    //MOB监听后，进行相应的UI操作的msg.what标记
    private static final int SMSSDK_TEST_RESPONSE = -2;
    //更新短信重新发送的时间的msg.what标记
    private static final int RESEND_MESSAGE_TIME_CHANGE_HANDLE = -3;
    //更新为可重新发送按钮的msg.what标记
    private static final int RESEND_MESSAGE_HANDLE = -4;

    private EventHandler eventHandler;      //注册短信验证成功后的回调Handle

    private String TAG = "RegisterActivity";

    //用于更新UI的句柄
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RESEND_MESSAGE_TIME_CHANGE_HANDLE:
                    //更新短信多少秒后可以发送
                    btnGetIdentifyCode.setText(messageSendTime + "s后可以重新发送");
                    btnGetIdentifyCode.setBackground(getResources().getDrawable(R.color.darkgray));
                    break;
                case RESEND_MESSAGE_HANDLE:
                    //按钮变为可点击
                    btnGetIdentifyCode.setText("获取验证码");
                    btnGetIdentifyCode.setClickable(true);
                    btnGetIdentifyCode.setBackground(getResources().getDrawable(R.drawable.bt_register));
                    messageSendTime = 60;
                    break;
                case SMSSDK_TEST_RESPONSE:
                    int event = msg.arg1;
                    int result = msg.arg2;

                    //如果发送成功
                    if(result == SMSSDK.RESULT_COMPLETE){
                        Log.d("test_message","3");
                        //验证成功，向服务器发送请求验证是否已注册
                        if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                            Log.d("test_message","4");
                            OkHttpUtils
                                    .postString()
                                    .url(NET_URL_HEADER + "user/register")
                                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                    .content(new Gson().toJson(new User(mPhoneString,mPhoneString,etRegisterPassword.getText().toString(),0)))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            e.printStackTrace();
                                            Log.d(TAG,"连接异常！");
                                            Toast.makeText(RegisterActivity.this,REGISTER_USER_ERROR,Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            User user = Utility.handleUserResponse(response);
                                            if(user != null){
                                                //如果服务器验证成功，跳转到主界面
                                                Toast.makeText(RegisterActivity.this,"注册成功",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                                //传入用户名,id
                                                intent.putExtra("userName",user.getUserName());
                                                intent.putExtra("userId",user.getUserId());
                                                startActivity(intent);
                                                finish();
                                            }else if(handleMessageResponse(response) != null){
                                                //提示错误信息
                                                Toast.makeText(RegisterActivity.this,handleMessageResponse(response),
                                                        Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(RegisterActivity.this,REGISTER_USER_FAIL,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                            //验证码已经从服务器发出
                            Toast.makeText(RegisterActivity.this, "验证码已发出,请注意查收", Toast.LENGTH_SHORT).show();
                        }else{
                            //验证码输入错误
                            Toast.makeText(RegisterActivity.this, "验证码输入错误！", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //发送失败
                        Log.d("send_message","3");
                        Toast.makeText(RegisterActivity.this,"短信发送失败！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //添加活动到ActivityList中
        SysApplication.getInstance().addActivity(this);

        /**
         * 判断SDK版本，若果版本低于设定值则不显示动画
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }

        /**
         * 初始化控件
         */
        initView();

        //注册短信验证成功后回调afterEvent的方法
        getEventHandler();

        SMSSDK.registerEventHandler(eventHandler);

    }

    private void getEventHandler() {
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                /**
                 * 短信验证后，返回主线程进行UI操作
                 */
                Message message = new Message();
                message.what = SMSSDK_TEST_RESPONSE;
                message.arg1 = event;
                message.arg2 = result;
                message.obj = data;
                handle.sendMessage(message);

            }
        };
    }

    /**
     * 初始化控件
     */
    private void initView() {
        etRegisterUserName = (EditText) findViewById(R.id.et_register_username);
        etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        cvRegister = (CardView) findViewById(R.id.register_card );
        etIdentifyCode = (EditText) findViewById(R.id.edit_identifying_code);
        btnGetIdentifyCode = (Button) findViewById(R.id.btn_get_identifying_code);

        btnGetIdentifyCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //注册点击事件
            case R.id.btn_register:
                /**
                 * 1.先通过SMSSDK验证 验证码是否正确
                 * 2.若错误这提示验证码输入错误
                 * 3.正确以后再向服务器发送请求验证是否已注册
                 * 4.若没注册，则返回注册成功，否则注册失败
                 */
                if (!TextUtils.isEmpty(etIdentifyCode.getText().toString())) {
                    Log.d("test_message","1");
                    SMSSDK.submitVerificationCode("86", mPhoneString,etIdentifyCode.getText().toString());
                } else {
                    Log.d("test_message","2");
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }


                break;
            //获取验证码点击事件
            case R.id.btn_get_identifying_code:
                /**
                 * 当输入手机号符合格式时，发送短信
                 * 否则，提示输入错误
                 */
                if(isMobile(etRegisterUserName.getText().toString())){
                    //符合，发送短信
                    Log.d("send_message","1");
                    SMSSDK.getVerificationCode("86", etRegisterUserName
                            .getText().toString());
                    mPhoneString = etRegisterUserName.getText().toString();
                    btnGetIdentifyCode.setClickable(false);         //设置按钮不可点击
                    btnGetIdentifyCode.setText(messageSendTime + "s后可以重新发送");
                    //显示倒计时
                    showMessageSendTime();
                }else{
                    //不符合
                    Log.d("send_message","2");
                    Toast.makeText(RegisterActivity.this,"手机号码格式输入错误",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void showMessageSendTime() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                for(int tempTime = messageSendTime; tempTime > 0;tempTime--,messageSendTime--){
                    //返回主线程
                    handle.sendEmptyMessage(RESEND_MESSAGE_TIME_CHANGE_HANDLE);
                    if(tempTime <= 0){
                        break;
                    }
                    try {
                        Thread.sleep(1000);     //休眠读秒
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //按钮恢复为可发送短信状态
                handle.sendEmptyMessage(RESEND_MESSAGE_HANDLE);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册handler监听
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 点击退出键，显示退出动画
     */
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    //显示进入动画
    private void ShowEnterAnimation() {
        //获取过渡对象,并且加载前一个活动的过渡动画
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.register_transition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                //将当前活动布局去掉，以动画形式加载
                cvRegister.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                //取消监听器
                transition.removeListener(this);
                //动画加载布局
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    //进入动画
    private void animateRevealShow(){
        //设置动画
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvRegister,cvRegister.getWidth()/2,0,cvRegister.getWidth()/8,cvRegister.getHeight());
        //设置动画时间
        mAnimator.setDuration(500);
        //设置动画变化速率(越来越快)
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                cvRegister.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        mAnimator.start();
    }

    //退出动画
    private void animateRevealClose(){
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvRegister,cvRegister.getWidth()/2,0,cvRegister.getHeight(),0);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cvRegister.setVisibility(View.INVISIBLE);
                RegisterActivity.super.onBackPressed();
            }
        });
        mAnimator.start();
    }

}
