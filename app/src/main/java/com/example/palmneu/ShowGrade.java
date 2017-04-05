package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowGrade extends AppCompatActivity {

    private String cookie = null;

    private String htmlcode = null;//返回的HTML代码

    private ListView listView = null;

    private ArrayAdapter<String> adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grade);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //添加返回按钮到导航栏
        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        listView = (ListView) findViewById(R.id.list_view);
        getGrade();


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

    private void getGrade() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("clp","获取成绩\n");
                    Log.d("clp","cookie="+cookie);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS")
                            .addHeader("Cookie",cookie)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("clp", "data=2\n" + responseData);
                    htmlcode=responseData;
                    showGradeInListView();
                } catch (Exception e) {
                    ToastShow("无法连接查成绩页面！");
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private String parseCodeWithJsoup(final String htmlcode) {
        String parsedCode = "";
        String[] course = null;
        try {
            Document doc = Jsoup.parse(htmlcode);
            //String class1=doc.select("tr.color-rowNext > td").text();
            //parsedCode=parsedCode+class1;
            //String class2=doc.getElementsByClass("color-row").text();
            //parsedCode=parsedCode+class2;
            Elements elements = doc.select("tr.color-rowNext > td");
            for (Element element : elements) {
                String name = element.text();
                parsedCode = parsedCode + name + ";";
            }
            elements = doc.select("tr.color-row > td");
            for (Element element : elements) {
                String name = element.text();
                parsedCode = parsedCode + name + ";";
            }

            course = parsedCode.split(";");
            Log.d("clp", "clp" + course.length);
            for (int i = 0; i < course.length; i++) {
                Log.d("clp", "clp" + course[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return parsedCode;
    }

    private void showGradeInListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] parsedCode = parseCodeWithJsoup(htmlcode).split(";");
                String[] course = new String[2 * parsedCode.length / 11];
                int j = 0;
                for (int i = 0; i < parsedCode.length; i++) {
                    if (i % 11 == 2) {
                        course[j] = parsedCode[i];
                        j++;
                    }
                    if (i % 11 == 10) {
                        course[j] = parsedCode[i];
                        j++;
                    }
                }
                adapter = new ArrayAdapter<String>(ShowGrade.this, android.R.layout.simple_list_item_1, course);
                listView.setAdapter(adapter);
            }
        });
    }

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShowGrade.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
