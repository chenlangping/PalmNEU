package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EcardInfo extends AppCompatActivity {

    private String cookie=null;
    private String cookie2=null;
    private String cookie3=null;

    private TextView textView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard_info);
        initView();
        Intent intent= getIntent();
        cookie=intent.getStringExtra("cookie");
        cookie2=intent.getStringExtra("cookie2");
        cookie3=cookie+"; .NECEID=1; .NEWCAPEC1=$newcapec$:zh-CN_CAMPUS; "+cookie2;
        Log.d("clp","获取到的cookie:"+cookie);
        Log.d("clp","获取到的cookie2:"+cookie2);
        Log.d("clp","拼接得到的最后的cookie="+cookie3);

        showEcardInfo();

    }

    private void initView(){
        textView=(TextView)findViewById(R.id.textview);
    }
    private void showEcardInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    OkHttpClient client = new OkHttpClient();

                    Request request =new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Cookie",cookie3)
                            .addHeader("Referer","http://ecard.neu.edu.cn/SelfSearch/login.aspx")
                            .addHeader("Connection","keep-alive")
                            .build();

                    Response response=client.newCall(request).execute();
                    String responseData= response.body().string();
                    Log.d("clp","用okhttp：\n"+responseData);

                    /***********************************************/

                    OkHttpClient client2 = new OkHttpClient();

                    Request request2 =new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/Home.aspx")
                            .addHeader("Cookie",cookie3)
                            .addHeader("Referer","http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Connection","keep-alive")
                            .build();

                    Response response2=client2.newCall(request2).execute();

                    InputStream in=response2.body().byteStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line=reader.readLine())!=null){
                        if(line.indexOf("正常卡")!=-1){
                            showResult(line);
                        }
                    }

                    //String responseData2= response2.body().string();
                    //Log.d("clp","用okhttp2：\n"+responseData2);



                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResult(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(msg);
            }
        });
    }
}
