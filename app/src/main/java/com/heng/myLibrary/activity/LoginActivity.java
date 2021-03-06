package com.heng.myLibrary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.heng.myLibrary.R;
import com.heng.myLibrary.database.DB.DBDefinitionManipulation;
import com.heng.myLibrary.database.entity.User;
import com.heng.myLibrary.util.MyLogging;

/**
 * @author : HengZhang
 * @date : 2021/11/25 15:20
 * <p>
 * 用户登陆
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    RadioButton userBtn, adminBtn;
    EditText accountTv, passwordTv;
    Button loginBtn, registerBtn;
    DBDefinitionManipulation db;
    ImageView helperImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyLogging.myLog(TAG,"onCreate()");

        //todo: 初始化界面
        initView();
    }

    private void initView() {
        MyLogging.myLog(TAG,"initView()");
        accountTv = findViewById(R.id.login_account);
        passwordTv = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.register_btn);
        userBtn = findViewById(R.id.login_user_btn);
        adminBtn = findViewById(R.id.login_admin_btn);
        helperImg = findViewById(R.id.login_helper_image);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        helperImg.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        db = new DBDefinitionManipulation(this);
        db.open();
        switch (view.getId()) {
            case R.id.login_btn:
                if (checkUser() == 1) {
                    MyLogging.myLog("login_btn"," admin 登陆成功");

                    Intent intent = new Intent(this, AdminActivity.class);
                    accountTv.setText("");
                    passwordTv.setText("");
                    startActivity(intent);
                } else if (checkUser() == 2) {
                    MyLogging.myLog("login_btn","user 登陆成功");

                    Intent intent = new Intent(this, MainActivity.class);
                    //todo: 添加用户数据
                    intent.putExtra("user", getUser(accountTv.getText().toString()));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    MyLogging.myLog("login_btn","用户名或密码错误");
                }
                break;
            case R.id.register_btn:
                MyLogging.myLog("register_btn","进入 register activity");

                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.login_helper_image:
                MyLogging.myLog("login_helper_image","进入 helper activity");

                startActivity(new Intent(this,HelperActivity.class));
                break;
        }
        db.close();
    }

    //todo: 获取用户信息
    private User getUser(String account) {
        User user = db.queryUser(account);
        return user;
    }

    //管理员1，用户2，失败0
    //todo: 验证用户身份
    private Integer checkUser() {
        Integer checkUserFlag = db.checkUser(accountTv.getText().toString(), passwordTv.getText().toString(), userIdentity());
        MyLogging.myLog("checkUserFlag",checkUserFlag.toString());
        return checkUserFlag;
    }

    //todo: 用户身份按钮识别
    private Integer userIdentity() {
        if (userBtn.isChecked()) {
            return 2;
        } else if (adminBtn.isChecked()) {
            return 1;
        }
        return 0;
    }
}