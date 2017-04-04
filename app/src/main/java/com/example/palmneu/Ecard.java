package com.example.palmneu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Ecard extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private EditText checkNumberEdit;//验证码编辑框

    private SharedPreferences preferences;//读取文件
    private SharedPreferences.Editor editor;

    private Button getPicture;//获取验证码按钮
    private Button getEcardInfo;//获取成绩按钮

    private ProgressBar progressBar;//小圈圈

    private ImageView imageView;//图片控件
    private String cookie=null;//保存cookie
    private int TIME_OUT=5; //超时时间设置
    private Bitmap bitmap=null; //验证码图片

    private String txtUserName=null;
    private String txtPassword=null;
    private String txtVaildateCode=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard);
        initView();
        getCookieAndPicture();



        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCookieAndPicture();
            }
        });

        getEcardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取记录啦

                txtUserName=accountEdit.getText().toString();
                txtPassword=passwordEdit.getText().toString();
                txtVaildateCode=checkNumberEdit.getText().toString();
                Log.d("clp","用户名:"+txtUserName);
                Log.d("clp","密码:"+txtPassword);
                Log.d("clp","验证码:"+txtVaildateCode);

                loginEcardWeb();
            }
        });



    }

    private void initView(){//初始化所有的组件

        editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        checkNumberEdit = (EditText) findViewById(R.id.check_number);
        getPicture = (Button) findViewById(R.id.get_picture);
        getEcardInfo = (Button) findViewById(R.id.get_ecardinfo);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        imageView = (ImageView) findViewById(R.id.check_picture);
        accountEdit.setText(preferences.getString("account", ""));
        passwordEdit.setText(preferences.getString("password", ""));

    }

    private void getCookieAndPicture() {//获取cookie
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();

                    Request request =new Request.Builder()
                            .url(new DataClass().ecardUrl+"SelfSearch/Login.aspx")
                            .addHeader("Connection","keep-alive")
                            .build();

                    Response response=client.newCall(request).execute();
                    String responseData= response.body().string();
                    //Log.d("clp",responseData);

                    String cookieAll = response.headers("Set-Cookie").get(0);
                    //Log.d("clp","\n cookie字段="+cookieAll);
                    //在这一步获取到形如  ASP.NET_SessionId=jxlbbbmkih40pzd40htwvu5r; path=/; HttpOnly 这样的cookie，但是我们只需要分号之前的

                    String[] cookieList = cookieAll.split(";");
                    //用分号分割
                    cookie = cookieList[0];
                    Log.d("clp","\n"+cookie);


                    //下一步就是利用此cookie去获取图片啦

                    getPicture();

                } catch (Exception e) {

                    ToastShow("无法连接到校园卡查询中心");
                    //超时处理
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void ToastShow(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPicture() {//利用现有的cookie去获取图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();

                    Request request =new Request.Builder()
                            .url(new DataClass().ecardUrl+"SelfSearch/validateimage.ashx?0.6119085425159312")
                            //这里的0.0000000000000000数字随意取，但是要确保长度一样
                            .addHeader("Cookie",cookie+"; .NECEID=1; .NEWCAPEC1=$newcapec$:zh-CN_CAMPUS;")
                            .addHeader("Connection","keep-alive")
                            .addHeader("Referer","http://ecard.neu.edu.cn/SelfSearch/Login.aspx")
                            .build();

                    ResponseBody body = client.newCall(request).execute().body();
                    InputStream in = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    showPicture(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastShow("无法连接到校卡查询中心");
                }
            }
        }).start();
    }

    private void showPicture(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    private void loginEcardWeb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .followRedirects(false)
                            //重定向功能关闭
                            //这个功能做了我一下午 艹艹艹
                            .build();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("__LASTFOCUS", "")
                            .add("__EVENTTARGET", "btnLogin")
                            .add("__EVENTARGUMENT", "")
                            .add("__VIEWSTATE", "/wEPDwUKMTM4OTU1Nzc4NA8WAh4Hc3lzSW5mbzKSBQABAAAA/////wEAAAAAAAAADAIAAABPTmV3Y2FwZWMuVW5pdmVyc2FsU1MuRFRPLCBWZXJzaW9uPTEuMC4wLjAsIEN1bHR1cmU9bmV1dHJhbCwgUHVibGljS2V5VG9rZW49bnVsbAwDAAAAUk5ld2NhcGVjLlVuaXZlcnNhbFNTLkVudGl0eSwgVmVyc2lvbj0xLjAuMC4wLCBDdWx0dXJlPW5ldXRyYWwsIFB1YmxpY0tleVRva2VuPW51bGwFAQAAAChOZXdjYXBlYy5Vbml2ZXJzYWxTUy5EVE8uRFRPX09VVF9TWVNJTkZPBAAAAB48T1BFUkFUSU9OTU9ERT5rX19CYWNraW5nRmllbGQbRFRPX1dTUnVuUmVzdWx0K19yZXN1bHRDb2RlGkRUT19XU1J1blJlc3VsdCtfcmVzdWx0TXNnHURUT19XU1J1blJlc3VsdCtfRWNhcmRWZXJzaW9uBAEBBDNOZXdjYXBlYy5Vbml2ZXJzYWxTUy5FbnRpdHkuRW51bS5FbnVtX09QRVJBVElPTk1PREUDAAAAMk5ld2NhcGVjLlVuaXZlcnNhbFNTLkVudGl0eS5FbnVtLkVudW1fRWNhcmRWZXJzaW9uAwAAAAIAAAAF/P///zNOZXdjYXBlYy5Vbml2ZXJzYWxTUy5FbnRpdHkuRW51bS5FbnVtX09QRVJBVElPTk1PREUBAAAAB3ZhbHVlX18ACAMAAAABAAAABgUAAAABMQYGAAAADOaJp+ihjOaIkOWKnwX5////Mk5ld2NhcGVjLlVuaXZlcnNhbFNTLkVudGl0eS5FbnVtLkVudW1fRWNhcmRWZXJzaW9uAQAAAAd2YWx1ZV9fAAgDAAAAQAAAAAsWAgIDD2QWBAIDDw8WAh4HVmlzaWJsZWhkFgJmDw8WAh4EVGV4dAUIMDAwMDAwMDBkZAILDw8WBB4LTmF2aWdhdGVVcmwFI2h0dHBzOi8vZWNhcmQubmV1LmVkdS5jbi9zZWxmc2VhcmNoHwFnZGRkAweWqu57mQFP8B4nVfROEI6Ir0096fOwGLk/ZRwAv/4=")
                            .add("__EVENTVALIDATION","/wEWBgKC+bThDQKl1bKzCQK1qbSRCwLTtPqEDQLkysKABAKC3IeGDGQe/atYgDWYZbK8ZRWJzBl5RKssahKr5OIP4orc4x36")
                            .add("txtUserName",txtUserName)
                            .add("txtPassword",txtPassword)
                            .add("txtVaildateCode",txtVaildateCode)
                            .add("hfIsManager","0")
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().ecardUrl+"SelfSearch/Login.aspx")
                            .addHeader("Cookie", cookie)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/Login.aspx")
                            .addHeader("Connection","keep-alive")
                            .addHeader("Upgrade-Insecure-Requests","1")
                            .addHeader("Content-Type","application/x-www-form-urlencoded")
                            .post(requestBody)
                            .build();




                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    String[] cookieAll=response.header("Set-Cookie").split(";");
                    String cookie2=cookieAll[0];
                    Log.d("clp","经过千辛万苦得到的第二个cookie："+cookie2);

                    //接下来就是把信息传过去了。。
                    Intent intent=new Intent(Ecard.this,EcardInfo.class);
                    intent.putExtra("cookie",cookie);
                    intent.putExtra("cookie2",cookie2);
                    //把cookie2的值放入到cookie2中传给下一个活动
                    startActivity(intent);




                }catch (Exception e){
                    ToastShow("超时");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
