package com.example.palmneu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserLogin extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private Button login;//登录按钮
    private Button register;//注册按钮

    private String account = null;//用户名
    private String nickname = null;//昵称
    private String password = null;//密码
    private String emailAddress = null;//邮箱


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        initView();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入内容
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if (check(account,password)) {
                    //手机端通过检查，发送信息给服务器
                    sendLoginMessageToServer(account, password);
                } else {
                    showToast("输入错误");
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行注册操作
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void initView() {

        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

    }


    private boolean check(String username, String password) {
        boolean flag = false;
        if (username.indexOf(" ") == -1 && password.indexOf(" ") == -1) {
            //若用户名和密码均不含空格
            flag = true;
        }

        return flag;
    }

    private void sendLoginMessageToServer(final String account, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName", account)
                            .add("passWord", password)
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "login.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("clp", "返回的信息:" + responseData);

                    if (responseData.indexOf("1") != -1) {
                        //返回1，表示成功登录
                        //获取昵称和邮箱
                        //返回格式为1,nickName,emailAddress
                        nickname = responseData.split(",")[1];
                        emailAddress = responseData.split(",")[2];
                        toastShow("登录成功，欢迎你 "+nickname);
                        //因为此时已经验证用户登录了，所以我们帮用户保存下账号和密码
                        SharedPreferences.Editor editor=getSharedPreferences("userdata",MODE_PRIVATE).edit();
                        editor.putString("palmneuaccount",account);
                        editor.putString("palmneupassword",password);
                        editor.putString("palmneunickname",nickname);
                        editor.putString("palmneunickname",emailAddress);
                        editor.apply();


                    } else if (responseData.indexOf("0") != -1) {
                        toastShow("该用户不存在");
                    } else if (responseData.indexOf("2") != -1) {
                        toastShow("密码错误");
                    } else {
                        toastShow("未知错误");
                    }


                } catch (Exception e) {
                    toastShow("连接到服务器超时");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void showToast(String msg) {
        Toast.makeText(UserLogin.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toastShow(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserLogin.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    //用户注册之后返回的
                    String account = data.getStringExtra("userName");
                    String password = data.getStringExtra("passWord");
                    accountEdit.setText(account);
                    passwordEdit.setText(password);
                } else if (resultCode == 2) {
                    //用户只是按了返回键返回，什么都不用做
                }

                break;
            default:
                //没有别的了
        }
    }


}

