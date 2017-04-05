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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginIn extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private EditText checkNumberEdit;//验证码编辑框

    private SharedPreferences preferences;//读取文件
    private SharedPreferences.Editor editor;

    private Button getPicture;//获取验证码按钮
    private Button getGrade;//获取成绩按钮

    private ProgressBar progressBar;//小圈圈

    private ImageView imageView;//图片控件
    private String cookie=null;//保存cookie
    private String picturesrc;//保存图片地址
    private int TIME_OUT=5; //超时时间设置
    private Bitmap bitmap=null; //验证码图片

    String WebUserNO;
    String Password;
    String Agnomen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //添加返回按钮到导航栏

        initView();//初始化控件

        getCookieAndPictureSrc();//获取cookie和验证码图片的地址

        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCookieAndPictureSrc();
                //getPicture();
                //此处不要使用getPicture()，否则在先断网的情况下进入该页面，之后在恢复网络并点击按钮，仍然会提示用户没有网络，这是因为根本没有图片地址获取到
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCookieAndPictureSrc();
            }
        });

        getGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUserNO = accountEdit.getText().toString();
                Password = passwordEdit.getText().toString();
                Agnomen = checkNumberEdit.getText().toString();

                judgeLoginInfomation();


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

        editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        checkNumberEdit = (EditText) findViewById(R.id.check_number);
        getPicture = (Button) findViewById(R.id.get_picture);
        getGrade = (Button) findViewById(R.id.get_grade);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        imageView = (ImageView) findViewById(R.id.check_picture);
        accountEdit.setText(preferences.getString("account", ""));
        passwordEdit.setText(preferences.getString("password", ""));

    }

    private void getCookieAndPictureSrc() {//获取cookie和图片地址
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();

                    Request request =new Request.Builder()
                            .url(new DataClass().aaoUrl)
                            .build();

                    Response response=client.newCall(request).execute();
                    String responseData= response.body().string();
                    String cookieString = response.header("Set-Cookie");
                    //获取set-cookie段的所有值，保存在cookieString中

                    Log.d("clp","用okhttp：\n"+responseData);
                    Log.d("clp",cookieString);

                    String[] cookieList = cookieString.split(";");
                    //用分号分割

                    cookie = cookieList[0];
                    //第一个就是cookie
                    Log.d("clp","cookie="+cookie);

                    picturesrc = responseData.substring(responseData.indexOf("ACTIONVALIDATERANDOMPICTURE"), responseData.indexOf("ACTIONVALIDATERANDOMPICTURE") + 64);
                    picturesrc = picturesrc.substring(0, picturesrc.indexOf("\""));
                    //从整个html的代码中获取到图片的地址

                    Log.d("clp","图片地址:"+picturesrc);

                    getPicture();
                    //把图片加载出来

                } catch (Exception e) {

                    ToastShow("无法连接到教务处");
                    //超时处理
                    e.printStackTrace();
                }
            }
        }).start();

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
                            .url(new DataClass().aaoUrl + "/" + picturesrc)
                            .addHeader("Cookie",cookie)
                            .build();

                    ResponseBody body = client.newCall(request).execute().body();
                    InputStream in = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    showPicture(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastShow("无法连接到教务处");
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


    private void judgeLoginInfomation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("WebUserNO", WebUserNO)
                            .add("Password", Password)
                            .add("Agnomen", Agnomen)
                            .add("submit7", "%B5%C7%C2%BC")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=")
                            .addHeader("Cookie", cookie)
                            .addHeader("Referer", "http://202.118.31.197")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("clp", "data1=\n" + responseData);

                    if (responseData.indexOf("请输入正确的附加码") != -1) {
                        ToastShow("请输入正确的附加码");
                    } else if (responseData.indexOf("您的密码错误了1次，共3次错误机会！") != -1) {
                        ToastShow("您的密码错误了1次，共3次错误机会！");
                    } else if (responseData.indexOf("您的密码错误了2次，共3次错误机会！") != -1) {
                        ToastShow("您的密码错误了2次，共3次错误机会！");
                    } else if (responseData.indexOf("您的密码错误了3次，共3次错误机会！") != -1) {
                        ToastShow("您的密码错误了3次，共3次错误机会！");
                    } else if (responseData.indexOf("密码错误次数超限，锁定登录5分钟！") != -1) {
                        ToastShow("密码错误次数超限，锁定登录5分钟！");
                    }else {
                        Intent intent = new Intent(LoginIn.this, ShowGrade.class);
                        intent.putExtra("cookie", cookie);
                        startActivity(intent);
                        //把验证成功的cookie传给下一个活动。
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ToastShow(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginIn.this,string,Toast.LENGTH_SHORT).show();
            }
        });
    }

}