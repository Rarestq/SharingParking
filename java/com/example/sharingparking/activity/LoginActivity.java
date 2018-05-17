package com.example.sharingparking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharingparking.R;
import com.example.sharingparking.SysApplication;
import com.example.sharingparking.entity.User;
import com.example.sharingparking.utils.Utility;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.sharingparking.common.Common.LOGIN_ERROR;
import static com.example.sharingparking.common.Common.LOGIN_FAIL;
import static com.example.sharingparking.common.Common.NET_URL_HEADER;
import static com.example.sharingparking.utils.Utility.handleMessageResponse;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences pre;
    private SharedPreferences.Editor mEditor;

    private String TAG = "LoginActivity";

    private EditText etLoginUserPhoneNumber;
    private EditText etLoginPassword;
    private Button btnRegister;
    private Button btnLogin;
    private TextView txtForgetPsw;

    //用户名
    private String phoneNumber;
    //密码
    private String password;

    private CardView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //添加活动到ActivityList中(安全退出)
        SysApplication.getInstance().addActivity(this);
        //获取存储登录信息的SharedPreferences对象
        this.pre = getSharedPreferences("loginMessage",MODE_PRIVATE);
        //loginMessage: 指定存储的文件名
        //MODE_PRIVETE:指定该SharedPreferences数据只能被本应用程序读写。
        mEditor = getSharedPreferences("loginMessage",MODE_PRIVATE).edit();

        if(!pre.getBoolean("isLogin",false)){
            //如果没有登陆，无处理
        }else{
            //如果已经登录
            //获取手机号，密码
            this.phoneNumber = pre.getString("phoneNumber","");
            this.password = pre.getString("password","");

            loginRequest();
        }

        //初始化控件
        initView();


        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    //登陆请求
    private void loginRequest() {
        OkHttpUtils
                .postString()
                .url(NET_URL_HEADER + "user/login")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(new User(phoneNumber,password)))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,LOGIN_ERROR,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG,response);
                        User user = Utility.handleUserResponse(response);
                        if(user != null){
                            //如果服务器验证成功，保存信息到SharedPreferences
                            if(!pre.getBoolean("isLogin",false)){
                                //如果未登录
                                mEditor.putBoolean("isLogin",true);
                                mEditor.putString("phoneNumber",phoneNumber);
                                mEditor.putString("password",password);
                            }

                            // 跳转到主界面
                            Log.d(TAG,"登陆成功！");
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            //传入用户名,id
                            intent.putExtra("userName",user.getUserName());
                            intent.putExtra("userId",user.getUserId());
                            startActivity(intent);
                            finish();
                        }else if(handleMessageResponse(response) != null){
                            //提示错误信息
                            Toast.makeText(LoginActivity.this,handleMessageResponse(response),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this,LOGIN_FAIL,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_register:
                startActivity(new Intent(this,RegisterActivity.class));

                break;
            case R.id.btn_login:
                //explode: 转场特效
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(this);

                //临时加入
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                startActivity(intent);

                //获取输入框中的手机号和密码
                this.phoneNumber = etLoginUserPhoneNumber.getText().toString();
                this.password = etLoginPassword.getText().toString();

                loginRequest();

                break;
        }
    }

    private void initView(){

        etLoginUserPhoneNumber = (EditText) findViewById(R.id.et_login_username);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        btnRegister = (Button) findViewById(R.id.btn_login_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        txtForgetPsw = (TextView) findViewById(R.id.txt_forget_psw);
        cv = (CardView) findViewById(R.id.login_card);

    }


}












