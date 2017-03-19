package com.example.palmneu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
            String account;//用户名
            String password;//密码
            boolean result = false;//登录状态

            @Override
            public void onClick(View v) {
                //获取输入内容
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if (check(account, password)) {
                    //暂定向服务器发送的消息格式为   LOGIN%username%password
                    sendMessage("LOGIN%" + account + "%" + password);
                } else {
                    //不合法输入则弹出提示
                    new AlertDialog.Builder(MainActivity.mainactivity)
                            .setTitle("输入错误提示框")
                            .setMessage("用户名或密码输入不合法")
                            .setPositiveButton("确定", null)
                            .show();


                }


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行注册操作
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                startActivity(intent);
            }
        });

    }


    private void initView() {

        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

    }


    /**
     * 检测是否是有效输入
     *
     * @param username
     * @param password
     * @return flag
     */
    private boolean check(String username, String password) {
        boolean flag = false;
        if (username != null && password != null) {
            flag = true;
        }
        return flag;
    }

    /**
     * 向服务器发送信息方法
     * @param message
     */
    private void sendMessage(String message){

    }
}

