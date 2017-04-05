package com.example.palmneu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EcardInfo extends AppCompatActivity {

    private String cookie=null;
    private String cookie2=null;
    private String cookie3=null;

    private TextView textView=null;
    private TextView textViewStartTime=null;
    private TextView textViewEndTime=null;
    private Button button=null;
    private Button setStartTime=null;
    private Button setEndTime=null;
    private ImageView imageView;//图片控件
    private Bitmap bitmap=null; //头像

    private int currentYear=0;
    private int currentMonth=0;
    private int currentDay=0;

    private int startYear=0;
    private int startMonth=0;
    private int startDay=0;

    private int endYear=0;
    private int endMonth=0;
    private int endDay=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard_info);
        initView();
        initData();
        Intent intent= getIntent();
        cookie=intent.getStringExtra("cookie");
        cookie2=intent.getStringExtra("cookie2");
        cookie3=cookie+"; .NECEID=1; .NEWCAPEC1=$newcapec$:zh-CN_CAMPUS; "+cookie2;
        Log.d("clp","获取到的cookie:"+cookie);
        Log.d("clp","获取到的cookie2:"+cookie2);
        Log.d("clp","拼接得到的最后的cookie="+cookie3);

        showEcardInfo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConsumeInfo();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置开始日期
                DatePickerDialog dpd=new DatePickerDialog(EcardInfo.this,Datelistener1,startYear,--startMonth,startDay);
                //注意这里要 --月份！
                dpd.show();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置结束日期
                DatePickerDialog dpd=new DatePickerDialog(EcardInfo.this,Datelistener2,endYear,--endMonth,endDay);
                dpd.show();
            }
        });

    }

    private void initView(){
        textView=(TextView)findViewById(R.id.textview);
        button=(Button)findViewById(R.id.get_consumeinfo);
        setStartTime=(Button)findViewById(R.id.setstarttime);
        setEndTime=(Button)findViewById(R.id.setendtime);
        textViewStartTime=(TextView)findViewById(R.id.textviewstarttime);
        textViewEndTime=(TextView)findViewById(R.id.textviewendtime);
        imageView=(ImageView)findViewById(R.id.photo);
    }

    private void initData(){
        Date date=new Date();
        currentYear=date.getYear()+1900;
        currentMonth=date.getMonth()+1;
        currentDay=date.getDate();
        //虽然安卓不推荐这么做 但是我懒啊

        startYear=currentYear;
        startMonth=currentMonth;
        startDay=currentDay;

        endYear=currentYear;
        endMonth=currentMonth;
        endDay=currentDay;

        textViewStartTime.setText(String.valueOf(startYear)+"-"+String.valueOf(startMonth)+"-"+String.valueOf(startDay));
        textViewEndTime.setText(String.valueOf(endYear)+"-"+String.valueOf(endMonth)+"-"+String.valueOf(endDay));

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
                    InputStream in=response.body().byteStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line=reader.readLine())!=null){
                        //Log.d("clp",line);
                    }

                    /*************************下面开始获取登录信息啦**********************/

                    OkHttpClient client2 = new OkHttpClient();

                    Request request2 =new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/Home.aspx")
                            .addHeader("Cookie",cookie3)
                            .addHeader("Referer","http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Connection","keep-alive")
                            .build();

                    Response response2=client2.newCall(request2).execute();

                    in=response2.body().byteStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    while((line=reader.readLine())!=null){
                        if(line.indexOf("正常卡")!=-1){
                            Log.d("clp",line);
                            String msg="";
                            Document doc = Jsoup.parse(line);
                            Elements elements =doc.select("span");
                            for (Element element : elements) {
                                String name = element.text();
                                msg=msg+name+'\n';
                            }
                            showResult(msg);
                        }
                    }

                    /*************************下面开始获取你的照片***************************/

                    OkHttpClient client3 = new OkHttpClient();

                    Request request3 =new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/Photo.ashx")
                            .addHeader("Cookie",cookie3)
                            .addHeader("Referer","http://ecard.neu.edu.cn/SelfSearch/User/Home.aspx")
                            .addHeader("Connection","keep-alive")
                            .build();

                    ResponseBody body = client3.newCall(request3).execute().body();
                    in = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    showPicture(bitmap);


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

    private void getConsumeInfo(){

    }

    private DatePickerDialog.OnDateSetListener Datelistener1=new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //在这里获取日期和设置日期
            startYear=year;
            startMonth=monthOfYear+1;
            startDay=dayOfMonth;
            textViewStartTime.setText(String.valueOf(startYear)+"-"+String.valueOf(startMonth)+"-"+String.valueOf(startDay));

        }


    };

    private DatePickerDialog.OnDateSetListener Datelistener2=new DatePickerDialog.OnDateSetListener()
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //在这里获取日期和设置日期
            endYear=year;
            endMonth=monthOfYear+1;
            endDay=dayOfMonth;
            textViewEndTime.setText(String.valueOf(endYear)+"-"+String.valueOf(endMonth)+"-"+String.valueOf(endDay));

        }


    };

    private void showPicture(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
