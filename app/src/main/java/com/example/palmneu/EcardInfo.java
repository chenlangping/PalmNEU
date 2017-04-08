package com.example.palmneu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EcardInfo extends AppCompatActivity {

    private String cookie = "";
    private String cookie2 = "";
    private String cookie3 = "";

    private TextView textView = null;//用来显示卡主的信息
    private TextView textViewStartTime = null;//用来显示用户选择的开始世界
    private TextView textViewEndTime = null;//用来显示用户选择的结束时间


    //此处同时用来显示圈存记录
    private TextView finalitem = null;//用来显示用户的消费记录

    private Button button = null;
    private Button button2 = null;//获取圈存记录的按钮
    private Button setStartTime = null;
    private Button setEndTime = null;
    private ImageView imageView;//图片控件
    private Bitmap bitmap = null; //头像

    private int TIME_OUT = 5;
    private int maxPage = 0;

    private String startTime = null;
    private String endTime = null;

    private int currentYear = 0;
    private int currentMonth = 0;
    private int currentDay = 0;

    private int startYear = 0;
    private int startMonth = 0;
    private int startDay = 0;

    private int endYear = 0;
    private int endMonth = 0;
    private int endDay = 0;

    private String __VIEWSTATE = "";
    private String __EVENTVALIDATION = "";

    private String msg = "";//用来存得到的消费记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initData();

        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        cookie2 = intent.getStringExtra("cookie2");
        cookie3 = cookie + "; .NECEID=1; .NEWCAPEC1=$newcapec$:zh-CN_CAMPUS; " + cookie2;

        Log.d("clp", "获取到的cookie:" + cookie);
        Log.d("clp", "获取到的cookie2:" + cookie2);
        Log.d("clp", "拼接得到的最后的cookie=" + cookie3);

        showEcardInfo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConsumeInfo();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBankInfo();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置开始日期
                DatePickerDialog dpd = new DatePickerDialog(EcardInfo.this, Datelistener1, startYear, --startMonth, startDay);
                //注意这里要 --月份！
                dpd.show();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置结束日期
                DatePickerDialog dpd = new DatePickerDialog(EcardInfo.this, Datelistener2, endYear, --endMonth, endDay);
                dpd.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }

    }

    private void initView() {
        textView = (TextView) findViewById(R.id.textview);
        button = (Button) findViewById(R.id.get_consumeinfo);
        //新增，获取圈存记录按钮
        button2 = (Button) findViewById(R.id.get_transferinfo);
        setStartTime = (Button) findViewById(R.id.setstarttime);
        setEndTime = (Button) findViewById(R.id.setendtime);
        textViewStartTime = (TextView) findViewById(R.id.textviewstarttime);
        textViewEndTime = (TextView) findViewById(R.id.textviewendtime);
        finalitem = (TextView) findViewById(R.id.final_item);
        imageView = (ImageView) findViewById(R.id.photo);
    }

    private void initData() {
        Date date = new Date();
        currentYear = date.getYear() + 1900;
        currentMonth = date.getMonth() + 1;
        currentDay = date.getDate();
        //虽然安卓不推荐这么做 但是我懒啊

        startYear = currentYear;
        startMonth = currentMonth;
        startDay = currentDay;

        endYear = currentYear;
        endMonth = currentMonth;
        endDay = currentDay;

        textViewStartTime.setText(String.valueOf(startYear) + "-" + String.valueOf(startMonth) + "-" + String.valueOf(startDay));
        textViewEndTime.setText(String.valueOf(endYear) + "-" + String.valueOf(endMonth) + "-" + String.valueOf(endDay));

    }

    private void showEcardInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                BufferedReader reader = null;
                String line = "";
                try {

                    /***********************首先需要登录到index.aspx************************/
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/login.aspx")
                            .addHeader("Connection", "keep-alive")
                            .build();

                    Response response = client.newCall(request).execute();

                    /*************************下面开始获取登录信息啦**********************/

                    OkHttpClient client2 = new OkHttpClient();

                    Request request2 = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/Home.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Connection", "keep-alive")
                            .build();

                    Response response2 = client2.newCall(request2).execute();

                    in = response2.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("正常卡") != -1) {
                            Log.d("clp", line);
                            String msg = "";
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("span");
                            for (Element element : elements) {
                                String name = element.text();
                                msg = msg + name + '\n';
                            }
                            showResult(msg);
                        }
                    }

                    /*************************下面开始获取你的照片***************************/

                    OkHttpClient client3 = new OkHttpClient();

                    Request request3 = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/Photo.ashx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/User/Home.aspx")
                            .addHeader("Connection", "keep-alive")
                            .build();

                    ResponseBody body = client3.newCall(request3).execute().body();
                    in = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    showPicture(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(msg);
            }
        });
    }

    private void showFinalResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  传入的信息形如  2017/3/5 11:45:01;餐费支出;9.00;110.30;虚拟职员;餐饮采集工作站;浑南三楼125#
                //  分别是 时间，支出种类，支出钱数，支出后的余额，谁把你钱扣走的，在哪里扣走的，具体在哪里扣走的
                String[] item = msg.split(";");
                String finalresult = "";
                for (int i = 0; i < item.length; i++) {
                    if (i % 7 == 0) {
                        finalresult = finalresult + "\n";
                    }
                    if (i % 7 == 0 || i % 7 == 1 || i % 7 == 2 || i % 7 == 3 || i % 7 == 6) {
                        finalresult = finalresult + item[i] + " ";
                    }

                }
                finalitem.setText(finalresult);
            }
        });
    }


    private void getBankInfo() {//用户点击查询圈存信息

        timeCheck();
        maxPage = 1;
        //用来显示用户查询数据的页数

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;
                String line = null;
                BufferedReader reader = null;
                try {
                    /*************************下面开始预读取数据*************************/
                    //注意 这里需要获取以下两个值，是为了接下来的使用
                    //__VIEWSTATE
                    //__EVENTVALIDATION
                    OkHttpClient client5 = new OkHttpClient();

                    Request request5 = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer",  "http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .build();

                    Response response5 = client5.newCall(request5).execute();
                    in = response5.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("__VIEWSTATE") != -1) {
                            __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 预读取获得的__VIEWSTATE1=" + __VIEWSTATE);
                        }
                        if (line.indexOf("__EVENTVALIDATION") != -1) {
                            __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 预读取获得的__EVENTVALIDATION1=" + __EVENTVALIDATION);
                        }
                    }

                    /****************************正式开始查找**************************************/
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();

                    Log.d("clp", "第一次查找用的：" + __VIEWSTATE);
                    Log.d("clp", "第一次查找用的：" + __EVENTVALIDATION);


                    RequestBody requestBody = new FormBody.Builder()
                            .add("__EVENTTARGET", "")
                            .add("__EVENTARGUMENT", "")
                            .add("__VIEWSTATE", __VIEWSTATE)
                            .add("__EVENTVALIDATION", __EVENTVALIDATION)
                            .add("ctl00$ContentPlaceHolder1$rbtnType", "0")
                            .add("ctl00$ContentPlaceHolder1$txtStartDate", startTime)
                            .add("ctl00$ContentPlaceHolder1$txtEndDate", endTime)
                            .addEncoded("ctl00$ContentPlaceHolder1$btnSearch", "查++询")
                            //注意最后一句话的addEncoded 因为有中文参数
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    in = response.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    msg = "";
                    while ((line = reader.readLine()) != null) {
                        //Log.d("clp",line);
                        if (line.indexOf("未查询到记录！") != -1) {
                            ToastShow("未查询到记录");
                            maxPage = 0;
                            //没有记录的时候当然是没有页面数啦
                            break;
                        }
                        if (line.indexOf("ContentPlaceHolder1_gridView_Label") != -1) {
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("span");
                            for (Element element : elements) {
                                String name = element.text();
                                msg = msg + name + ';';
                            }
                        }
                        if (line.indexOf("圈存") != -1) {
                            line = "<table>" + line.substring(line.indexOf("</td>") + 5, line.length()) + "</table>";
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("td");
                            for (Element element : elements) {
                                String name = element.text();
                                msg = msg + name + ';';
                            }
                            //这是因为没有table标签的td tr标签jsoup不解析。。所以曲线救国 我给它加了table。。
                        }

                        if (line.indexOf("<a disabled=\"true\">&lt;&lt;</a><a disabled=\"true\">") != -1) {
                            Log.d("clp", line);
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("a");
                            for (Element element : elements) {
                                String name = element.text();
                                if ((name.indexOf("<") == -1) && (name.indexOf(">") == -1) && (name.indexOf("...") == -1)) {
                                    maxPage = Integer.parseInt(name);
                                }
                                if (name.indexOf("...") != -1) {
                                    ToastShow("信息量有点大，只显示一部分");
                                }
                                //Log.d("clp",name);
                            }
                        }
                        if (line.indexOf("__VIEWSTATE") != -1) {
                            __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 第一次查找得到的__VIEWSTATE=" + __VIEWSTATE);
                        }
                        if (line.indexOf("__EVENTVALIDATION") != -1) {
                            __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", "第一次查找得到的__EVENTVALIDATION=" + __EVENTVALIDATION);
                        }
                    }
                    Log.d("clp", "得到的最大页数=" + String.valueOf(maxPage));
                    Log.d("clp", "第一页=" + msg);

                    /**********************接下来获取除第一页的信息*********************************/
                    for (int i = 1; i < maxPage; i++) {
                        client = new OkHttpClient.Builder()
                                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                .build();
                        Log.d("clp", "现在开始查询的页码：" + String.valueOf(i + 1));


                        requestBody = new FormBody.Builder()
                                .add("__EVENTTARGET", "ctl00$ContentPlaceHolder1$AspNetPager1")
                                .add("__EVENTARGUMENT", String.valueOf(i + 1))
                                .add("__VIEWSTATE", __VIEWSTATE)
                                .add("__EVENTVALIDATION", __EVENTVALIDATION)
                                .add("ctl00$ContentPlaceHolder1$rbtnType", "0")
                                .add("ctl00$ContentPlaceHolder1$txtStartDate", startTime)
                                .add("ctl00$ContentPlaceHolder1$txtEndDate", endTime)
                                //.addEncoded("ctl00$ContentPlaceHolder1$btnSearch", "查++询")
                                //注意最后一句话的addEncoded 因为有中文参数
                                .build();

                        request = new Request.Builder()
                                .url("http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                                .addHeader("Cookie", cookie3)
                                .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/User/BankInfo.aspx")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Upgrade-Insecure-Requests", "1")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                                .post(requestBody)
                                .build();

                        response = client.newCall(request).execute();
                        in = response.body().byteStream();
                        reader = new BufferedReader(new InputStreamReader(in));

                        while ((line = reader.readLine()) != null) {
                            //Log.d("clp",line);

                            if (line.indexOf("ContentPlaceHolder1_gridView_Label") != -1) {
                                Document doc = Jsoup.parse(line);
                                Elements elements = doc.select("span");
                                for (Element element : elements) {
                                    String name = element.text();
                                    msg = msg + name + ';';
                                }
                            }
                            if (line.indexOf("圈存") != -1) {
                                line = "<table>" + line.substring(line.indexOf("</td>") + 5, line.length()) + "</table>";
                                Document doc = Jsoup.parse(line);
                                Elements elements = doc.select("td");
                                for (Element element : elements) {
                                    String name = element.text();
                                    msg = msg + name + ';';
                                }
                                //这是因为没有table标签的td tr标签jsoup不解析。。所以曲线救国 我给它加了table。。
                            }

                            if (line.indexOf("__VIEWSTATE") != -1) {
                                __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                                //Log.d("clp", " __VIEWSTATE=" + __VIEWSTATE);
                            }
                            if (line.indexOf("__EVENTVALIDATION") != -1) {
                                __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                                //Log.d("clp", " __EVENTVALIDATION=" + __EVENTVALIDATION);
                            }
                        }
                    }

                    showFinalResult(msg);
                } catch (Exception e) {
                    ToastShow("发生错误");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getConsumeInfo() {//用户点击查询按钮

        timeCheck();

        msg = "";
        //msg是用来存放用户的所有的消费记录，所以在点击查询的时候需要先清空
        maxPage = 1;
        //用来显示用户查询数据的页数

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;
                String line = null;
                BufferedReader reader = null;
                try {
                    /*************************下面开始预读取数据*************************/
                    //注意 这里需要获取以下两个值，是为了接下来的使用
                    //__VIEWSTATE
                    //__EVENTVALIDATION
                    OkHttpClient client4 = new OkHttpClient();

                    Request request4 = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/ConsumeInfo.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/Index.aspx")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .build();

                    Response response4 = client4.newCall(request4).execute();
                    in = response4.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("__VIEWSTATE") != -1) {
                            __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 预读取获得的__VIEWSTATE1=" + __VIEWSTATE);
                        }
                        if (line.indexOf("__EVENTVALIDATION") != -1) {
                            __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 预读取获得的__EVENTVALIDATION1=" + __EVENTVALIDATION);
                        }
                    }

                    /****************************正式开始查找**************************************/
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();

                    Log.d("clp", "第一次查找用的：" + __VIEWSTATE);
                    Log.d("clp", "第一次查找用的：" + __EVENTVALIDATION);


                    RequestBody requestBody = new FormBody.Builder()
                            .add("__EVENTTARGET", "")
                            .add("__EVENTARGUMENT", "")
                            .add("__VIEWSTATE", __VIEWSTATE)
                            .add("__EVENTVALIDATION", __EVENTVALIDATION)
                            .add("ctl00$ContentPlaceHolder1$rbtnType", "0")
                            .add("ctl00$ContentPlaceHolder1$txtStartDate", startTime)
                            .add("ctl00$ContentPlaceHolder1$txtEndDate", endTime)
                            .addEncoded("ctl00$ContentPlaceHolder1$btnSearch", "查++询")
                            //注意最后一句话的addEncoded 因为有中文参数
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ecard.neu.edu.cn/SelfSearch/User/ConsumeInfo.aspx")
                            .addHeader("Cookie", cookie3)
                            .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/User/ConsumeInfo.aspx")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    in = response.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    msg = "";
                    while ((line = reader.readLine()) != null) {
                        //Log.d("clp",line);
                        if (line.indexOf("未查询到记录！") != -1) {
                            ToastShow("未查询到记录");
                            maxPage = 0;
                            //没有记录的时候当然是没有页面数啦
                            break;
                        }
                        if (line.indexOf("ContentPlaceHolder1_gridView_Label") != -1) {
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("span");
                            for (Element element : elements) {
                                String name = element.text();
                                msg = msg + name + ';';
                            }
                        }
                        if (line.indexOf("支出") != -1) {
                            line = "<table>" + line.substring(line.indexOf("</td>") + 5, line.length()) + "</table>";
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("td");
                            for (Element element : elements) {
                                String name = element.text();
                                msg = msg + name + ';';
                            }
                            //这是因为没有table标签的td tr标签jsoup不解析。。所以曲线救国 我给它加了table。。
                        }

                        if (line.indexOf("<a disabled=\"true\">&lt;&lt;</a><a disabled=\"true\">") != -1) {
                            Log.d("clp", line);
                            Document doc = Jsoup.parse(line);
                            Elements elements = doc.select("a");
                            for (Element element : elements) {
                                String name = element.text();
                                if ((name.indexOf("<") == -1) && (name.indexOf(">") == -1) && (name.indexOf("...") == -1)) {
                                    maxPage = Integer.parseInt(name);
                                }
                                if (name.indexOf("...") != -1) {
                                    ToastShow("信息量有点大，只显示一部分");
                                }
                                //Log.d("clp",name);
                            }
                        }
                        if (line.indexOf("__VIEWSTATE") != -1) {
                            __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", " 第一次查找得到的__VIEWSTATE=" + __VIEWSTATE);
                        }
                        if (line.indexOf("__EVENTVALIDATION") != -1) {
                            __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                            Log.d("clp", "第一次查找得到的__EVENTVALIDATION=" + __EVENTVALIDATION);
                        }
                    }
                    Log.d("clp", "得到的最大页数=" + String.valueOf(maxPage));
                    Log.d("clp", "第一页=" + msg);

                    /**********************接下来获取除第一页的信息*********************************/
                    for (int i = 1; i < maxPage; i++) {
                        client = new OkHttpClient.Builder()
                                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                .build();
                        Log.d("clp", "现在开始查询的页码：" + String.valueOf(i + 1));


                        requestBody = new FormBody.Builder()
                                .add("__EVENTTARGET", "ctl00$ContentPlaceHolder1$AspNetPager1")
                                .add("__EVENTARGUMENT", String.valueOf(i + 1))
                                .add("__VIEWSTATE", __VIEWSTATE)
                                .add("__EVENTVALIDATION", __EVENTVALIDATION)
                                .add("ctl00$ContentPlaceHolder1$rbtnType", "0")
                                .add("ctl00$ContentPlaceHolder1$txtStartDate", startTime)
                                .add("ctl00$ContentPlaceHolder1$txtEndDate", endTime)
                                //.addEncoded("ctl00$ContentPlaceHolder1$btnSearch", "查++询")
                                //注意最后一句话的addEncoded 因为有中文参数
                                .build();

                        request = new Request.Builder()
                                .url("http://ecard.neu.edu.cn/SelfSearch/User/ConsumeInfo.aspx")
                                .addHeader("Cookie", cookie3)
                                .addHeader("Referer", "http://ecard.neu.edu.cn/SelfSearch/User/ConsumeInfo.aspx")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Upgrade-Insecure-Requests", "1")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                                .post(requestBody)
                                .build();

                        response = client.newCall(request).execute();
                        in = response.body().byteStream();
                        reader = new BufferedReader(new InputStreamReader(in));

                        while ((line = reader.readLine()) != null) {
                            //Log.d("clp",line);

                            if (line.indexOf("ContentPlaceHolder1_gridView_Label") != -1) {
                                Document doc = Jsoup.parse(line);
                                Elements elements = doc.select("span");
                                for (Element element : elements) {
                                    String name = element.text();
                                    msg = msg + name + ';';
                                }
                            }
                            if (line.indexOf("支出") != -1) {
                                line = "<table>" + line.substring(line.indexOf("</td>") + 5, line.length()) + "</table>";
                                Document doc = Jsoup.parse(line);
                                Elements elements = doc.select("td");
                                for (Element element : elements) {
                                    String name = element.text();
                                    msg = msg + name + ';';
                                }
                                //这是因为没有table标签的td tr标签jsoup不解析。。所以曲线救国 我给它加了table。。
                            }

                            if (line.indexOf("__VIEWSTATE") != -1) {
                                __VIEWSTATE = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                                //Log.d("clp", " __VIEWSTATE=" + __VIEWSTATE);
                            }
                            if (line.indexOf("__EVENTVALIDATION") != -1) {
                                __EVENTVALIDATION = line.substring(line.indexOf("value=") + 7, line.length() - 4);
                                //Log.d("clp", " __EVENTVALIDATION=" + __EVENTVALIDATION);
                            }
                        }
                    }

                    showFinalResult(msg);
                } catch (Exception e) {
                    ToastShow("发生错误");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void timeCheck() {//该函数的作用是确保生成 2017-01-01这样的时间
        startTime = "";
        startTime = String.valueOf(startYear);

        if (startMonth < 10) {
            startTime = startTime + '-' + '0' + String.valueOf(startMonth);
        } else {
            startTime = startTime + '-' + String.valueOf(startMonth);
        }

        if (startDay < 10) {
            startTime = startTime + '-' + '0' + String.valueOf(startDay);
        } else {
            startTime = startTime + '-' + String.valueOf(startDay);
        }

        Log.d("clp", "开始时间=" + startTime);


        endTime = "";

        endTime = String.valueOf(endYear);

        if (endMonth < 10) {
            endTime = endTime + '-' + '0' + String.valueOf(endMonth);
        } else {
            endTime = endTime + '-' + String.valueOf(endMonth);
        }

        if (endDay < 10) {
            endTime = endTime + '-' + '0' + String.valueOf(endDay);
        } else {
            endTime = endTime + '-' + String.valueOf(endDay);
        }

        Log.d("clp", "结束时间=" + endTime);
    }

    private DatePickerDialog.OnDateSetListener Datelistener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //在这里获取日期和设置日期
            startYear = year;
            startMonth = monthOfYear + 1;
            startDay = dayOfMonth;
            textViewStartTime.setText(String.valueOf(startYear) + "-" + String.valueOf(startMonth) + "-" + String.valueOf(startDay));

        }


    };

    private DatePickerDialog.OnDateSetListener Datelistener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //在这里获取日期和设置日期
            endYear = year;
            endMonth = monthOfYear + 1;
            endDay = dayOfMonth;
            textViewEndTime.setText(String.valueOf(endYear) + "-" + String.valueOf(endMonth) + "-" + String.valueOf(endDay));

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

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
