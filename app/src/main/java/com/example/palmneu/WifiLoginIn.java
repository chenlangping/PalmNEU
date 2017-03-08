package com.example.palmneu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WifiLoginIn extends AppCompatActivity {

    TextView textView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_login_in);
        textView=(TextView)findViewById(R.id.wifi_login_in);
        connectWifi();
    }

    private void connectWifi(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody =new FormBody.Builder()
                            .add("action","login")
                            .add("ac_id","1")
                            .add("username","用户名")
                            .add("password","密码")
                            .add("save_me","0")
                            .build();
                    Request request =new Request.Builder()
                            .url("https://ipgw.neu.edu.cn/srun_portal_pc.php?url=&ac_id=1")
                            .post(requestBody)
                            .build();

                    Response response= client.newCall(request).execute();

                    //获取set-cookie字段的整个值
                    String cookieString =response.header("Set-Cookie");

                    //获取“;”之前的数据，即login=一大串值
                    String [] cookieList =cookieString.split(";");
                    String cookie=cookieList[0];

                    Log.d("clp",cookie);
                    //删除"login="
                    //cookie=cookie.substring(6);
                    //Log.d("clp",cookie);

                    //至此cookie获取结束，接着开始第二次连接，获取剩余流量等信息

                    OkHttpClient client2 = new OkHttpClient();
                    RequestBody requestBody2 =new FormBody.Builder()
                            .add("action","get_online_info")
                            .add("key","123")
                            .build();
                    Request request2 =new Request.Builder()
                            .url("https://ipgw.neu.edu.cn//include/auth_action.php?k=123")
                            .addHeader("cookie",cookie)
                            .addHeader("Referer","https://ipgw.neu.edu.cn/srun_portal_pc.php?url=&ac_id=1")
                            .post(requestBody2)
                            .build();

                    Response response2= client2.newCall(request2).execute();
                    String returnInformation =response2.body().string();

                    //至此已经获得一串数据，用逗号分隔
                    //9056917438,383059,34.54,,0,118.202.16.155
                    //第一个是流量，第二个是在线时长，第三个是账户余额，第四个是无，第五个是0，第六个是IP地址。
                    Log.d("clp","return:\n"+returnInformation);


                    String [] ipgwInfo =returnInformation.split(",");
                    Log.d("clp","length="+ipgwInfo.length);
                    //接下来就是数据处理
                    showResponse(ipgwInfo);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String [] ipgwInfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float liuliang=Float.parseFloat(ipgwInfo[0]);
                liuliang=liuliang/(1024*1024);
                Log.d("clp","流量："+liuliang+"M");

                float zaixianshichang=Float.parseFloat(ipgwInfo[1]);
                zaixianshichang=zaixianshichang/3600;
                Log.d("clp","在线时长："+zaixianshichang+"小时");

                float yue=Float.parseFloat(ipgwInfo[2]);
                Log.d("clp","余额："+yue+"元");

                String IPAddress=ipgwInfo[5];
                Log.d("clp","ip地址："+IPAddress);

                textView.setText("连接成功！\n"+"已用流量："+liuliang+"M"+"\n"+"在线时长："+zaixianshichang+"小时"+"\n"+"余额："+yue+"元"+"\n"+"ip地址："+IPAddress);
            }
        });
    }
}
