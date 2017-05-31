package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by chenyufeng on 17/5/24.
 */

public class LibraryInfo extends AppCompatActivity {
    private String userInfo = null; //用户信息
    private String bookNow = null;  //未还图书
    private String bookBefore = null; //借阅记录

    private TextView viewUserInfo = null;//用来显示用户信息
    private Button buttonBookNow = null;//用来显示用户未还图书
    private Button buttonBookBefore = null;//用来显示用户的借阅记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        Intent intent = getIntent();
        userInfo = intent.getStringExtra("userInfo");
        bookNow = intent.getStringExtra("bookNow");
        bookBefore = intent.getStringExtra("bookBefore");

        try {
            showUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        buttonBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookNow();
            }
        });

        buttonBookBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookBefore();
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
        viewUserInfo = (TextView) findViewById(R.id.viewUserInfo);
        buttonBookNow = (Button) findViewById(R.id.buttonBookNow);
        buttonBookBefore = (Button) findViewById(R.id.buttonBookBefore);
        viewUserInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    private void showUserInfo() throws IOException {
        Document doc = Jsoup.parse(userInfo);
        Elements table = doc.select("table");
        Elements user = table.select("td");
        Elements table1 = doc.select("table[class=indent1]");
        Elements tds1 = table1.get(0).select("td");
        userInfo = "";
        for (int a = 0; a < 8; a++) {
            if (a % 2 == 0) {
                userInfo += user.get(a).text() + "   ";
            } else {
                userInfo += user.get(a).text() + "\n";
            }
        }
        for (int k = 0; k < 12; k++) {
            if (k % 2 == 0) {
                userInfo += tds1.get(k).text() + "   ";
            } else {
                userInfo += tds1.get(k).text() + "\n";
            }
        }
        viewUserInfo.setText(userInfo);
    }

    private void getBookNow() {//用户点击查询未还图书
        userInfo = "";
        if (bookNow.indexOf("没有任何") == -1) {
            Document doc = Jsoup.parse(bookNow);
            Elements tables = doc.select("table");
            Elements ths = tables.get(2).select("th");
            Elements tds = tables.get(2).select("td");
            Iterator<Element> it2 = tds.iterator();
            while (it2.hasNext()) {
                Iterator<Element> it1 = ths.iterator();
                while (it1.hasNext()) {
                    userInfo += it1.next().text() + "    ";
                    userInfo += it2.next().text() + "\n";
                }
            }
        } else {
            userInfo += "您没有未还图书";
        }
        viewUserInfo.setText(userInfo);
    }

    private void getBookBefore() {//用户点击查询借阅记录
        //借书总记录
        userInfo = "";
        Document doc = Jsoup.parse(bookBefore);
        Elements tables = doc.select("table");
        Elements ths = tables.get(2).select("th");
        Elements tds = tables.get(2).select("td");
        Iterator<Element> it2 = tds.iterator();
        while (it2.hasNext()) {
            Iterator<Element> it1 = ths.iterator();
            while (it1.hasNext()) {
                userInfo += it1.next().text() + "    ";
                userInfo += it2.next().text() + "\n";
            }
        }
        viewUserInfo.setText(userInfo);
    }

}
