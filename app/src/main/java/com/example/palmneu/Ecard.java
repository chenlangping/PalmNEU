package com.example.palmneu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.io.InputStreamReader;
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
    //用户名 密码 验证码

    private String __VIEWSTATE=null;
    private String __EVENTVALIDATION=null;
    //这两个参数是网站每日生成的，在第一次返回的代码中有


    private boolean canLogin=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //添加返回按钮到导航栏
        initView();
        getCookieAndPicture();



        accountEdit.setText("20144830");
        passwordEdit.setText("032015");
        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCookieAndPicture();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCookieAndPicture();
            }
        });

        getEcardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取记录啦
                canLogin=true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮的实现
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:return  true;
        }

    }

    private void initView(){//初始化所有的组件

        //editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        //preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        checkNumberEdit = (EditText) findViewById(R.id.check_number);
        getPicture = (Button) findViewById(R.id.get_picture);
        getEcardInfo = (Button) findViewById(R.id.get_ecardinfo);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        imageView = (ImageView) findViewById(R.id.check_picture);
        //accountEdit.setText(preferences.getString("account", ""));
        //passwordEdit.setText(preferences.getString("password", ""));

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

                    InputStream in=response.body().byteStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String line="";
                    String __VIEWSTATEline=null;
                    String __EVENTVALIDATIONline=null;
                    while((line=reader.readLine())!=null){
                        //Log.d("clp",line);
                        if(line.indexOf("__VIEWSTATE")!=-1){
                            __VIEWSTATEline=line;
                        }
                        if(line.indexOf("__EVENTVALIDATION")!=-1){
                            __EVENTVALIDATIONline=line;
                        }

                    }
                    Log.d("clp",__VIEWSTATEline);
                    Log.d("clp",__EVENTVALIDATIONline);

                    __VIEWSTATE=__VIEWSTATEline.substring(__VIEWSTATEline.indexOf("value=")+7,__VIEWSTATEline.length()-4);
                    Log.d("clp","处理过后的="+__VIEWSTATE);

                    __EVENTVALIDATION=__EVENTVALIDATIONline.substring(__EVENTVALIDATIONline.indexOf("value=")+7,__EVENTVALIDATIONline.length()-4);
                    Log.d("clp","处理过后的="+__EVENTVALIDATION);

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
                            .add("__VIEWSTATE",__VIEWSTATE)
                            .add("__EVENTVALIDATION",__EVENTVALIDATION)
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

                    InputStream in=response.body().byteStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line=reader.readLine())!=null){
                        if(line.indexOf("验证码错误")!=-1){
                            ToastShow("验证码错误");
                            canLogin=false;
                            getCookieAndPicture();
                        }
                        if(line.indexOf("输入的密码有误")!=-1){
                            ToastShow("输入的密码有误！");
                            canLogin=false;
                            getCookieAndPicture();
                        }
                        if(line.indexOf("账户或密码错误")!=-1){
                            ToastShow("账户或密码错误");
                            canLogin=false;
                            getCookieAndPicture();
                        }
                        if(line.indexOf("验证码过期")!=-1){
                            ToastShow("验证码过期");
                            canLogin=false;
                            getCookieAndPicture();
                        }
                    }
                    String[] cookieAll=response.header("Set-Cookie").split(";");
                    String cookie2=cookieAll[0];
                    Log.d("clp","经过千辛万苦得到的第二个cookie："+cookie2);

                    //接下来就是把信息传过去了。。
                    if(canLogin){
                        Intent intent=new Intent(Ecard.this,EcardInfo.class);
                        intent.putExtra("cookie",cookie);
                        intent.putExtra("cookie2",cookie2);
                        //把cookie2的值放入到cookie2中传给下一个活动
                        startActivity(intent);
                    }





                }catch (Exception e){
                    //ToastShow("发生错误");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {//用户点击返回按钮返回的时候重新需要获取
        super.onStart();
        getCookieAndPicture();
        checkNumberEdit.setText("");
        //因为验证码肯定已经刷新了 ，所以帮用户去掉
    }
}
