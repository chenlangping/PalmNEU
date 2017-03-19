package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserLogin extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private Button login;//登录按钮
    private Button register;//注册按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        initView();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行登录操作
                //TODO 陈瑜峰
                //1.获取用户输入
                //2.check(.....)懂吧？
                //3.如果check返回true就发送给服务器，否则告诉用户错了


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行注册操作
                Intent intent=new Intent(UserLogin.this,UserRegister.class);
                startActivity(intent);
            }
        });
    }


    private void initView(){

        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);

    }

    private boolean check(String username){

        return true;
    }
}
