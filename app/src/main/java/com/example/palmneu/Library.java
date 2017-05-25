package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chenyufeng on 17/5/24.
 */

public class Library extends AppCompatActivity {
    private boolean canLogin = true;
    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private Button getLibrary;//获取借阅记录按钮
    private String tempResponse = null;//保存response
    private String userInfo = null;
    private String bookNow = null;//保存未还图书
    private String bookBefore = null;//保存借阅记录

    private int TIME_OUT = 5; //超时时间设置
    private String txtUserName = null;
    private String txtPassword = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //添加返回按钮到导航栏
        initView();


        accountEdit.setText("20144837");
        passwordEdit.setText("144837");

        getLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUserName = accountEdit.getText().toString();
                txtPassword = passwordEdit.getText().toString();
                Log.d("clp", "用户名:" + txtUserName);
                Log.d("clp", "密码:" + txtPassword);
                loginLibraryWeb();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮的实现
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }

    }

    private void initView() {//初始化所有的组件
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        getLibrary = (Button) findViewById(R.id.get_libraryinfo);

    }

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loginLibraryWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://202.118.8.7:8991/F/-?func=bor-info")
                            .build();

                    Response response = client.newCall(request).execute();


                    InputStream in = response.body().byteStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line = "";
                    String url = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("action") != -1) {
                            url = line;
                        }
                    }
                    url = url.substring(url.indexOf("action") + 8, url.length() - 3);


                    OkHttpClient client1 = new OkHttpClient();

                    //        bor_id  20154863
                    //        bor_library NEU50
                    //        bor_verification 154863
                    //        func login-session
                    //        login_source bor-info
                    RequestBody requestBody1 = new FormBody.Builder()
                            .add("bor_id", txtUserName)
                            .add("bor_library", "NEU50")
                            .add("bor_verification", txtPassword)
                            .add("func", "login-session")
                            .add("login_source", "bor-info")
                            .build();

                    Request request1 = new Request.Builder()
                            .url(url)
                            .addHeader("Referer", "http://202.118.8.7:8991/F/-?func=bor-info")
                            .post(requestBody1)
                            .build();
                    Response response1 = client1.newCall(request1).execute();
                    tempResponse = response1.body().string();
                    if (tempResponse.indexOf("证号或密码错误") != -1) {
                        canLogin = false;
                        ToastShow("证号或密码错误");
                    } else {
                        OkHttpClient client2 = new OkHttpClient();
                        Request request2 = new Request.Builder()
                                .url(url + "?func=bor-loan&adm_library=NEU50")
                                .addHeader("Referer", url)
                                .build();
                        Response response2 = client2.newCall(request2).execute();
                        bookNow = response2.body().string();

                        OkHttpClient client3 = new OkHttpClient();
                        Request request3 = new Request.Builder()
                                .url(url + "?func=bor-history-loan&adm_library=NEU50")
                                .addHeader("Referer", url)
                                .build();
                        Response response3 = client3.newCall(request3).execute();

                        bookBefore = response3.body().string();
                        if (canLogin) {
                            Intent intent = new Intent(Library.this, LibraryInfo.class);
                            userInfo = tempResponse;
                            intent.putExtra("userInfo", userInfo);
                            intent.putExtra("bookNow", bookNow);
                            intent.putExtra("bookBefore", bookBefore);
                            //获得的三个html传给LibraryInfo做处理
                            startActivity(intent);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
