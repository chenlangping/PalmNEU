package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowGrade extends AppCompatActivity {

    private String cookie = null;
    private String WebUserNO = null;
    private String Password = null;
    private String Agnomen = null;//验证码
    private String htmlcode = null;//返回的HTML代码
    private TextView gradeText = null;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private URL url = null;
    private InputStream in=null;
    private ListView listView=null;
    private Button button=null;
    private ArrayAdapter<String>adapter=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grade);
        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        WebUserNO = intent.getStringExtra("WebUserNO");
        Password = intent.getStringExtra("Password");
        Agnomen = intent.getStringExtra("Agnomen");
        listView=(ListView)findViewById(R.id.list_view);
        getGrade();


    }

    private void getGrade() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    url = new URL("http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Cookie", cookie);
                    connection.setRequestProperty("Referer", "http://202.118.31.197/");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("WebUserNO=" + WebUserNO + "&Password=" + Password + "&Agnomen=" + Agnomen + "&submit7=%B5%C7%C2%BC");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    in = connection.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                try {
                    url = new URL("http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", cookie);
                    connection.setRequestProperty("Referer", " http://202.118.31.197/Menu.jsp?UserType=BASE_STUDENT");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in,"GBK"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    htmlcode = response.toString();

                    //htmlcode=htmlcode.substring(htmlcode.indexOf("<td nowrap>&nbsp;"),htmlcode.length());
                    //showResponse(htmlcode);


                    showGradeInListView();

                    Log.d("ShowGrade", "clp code=" + htmlcode);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gradeText.setText(response);
            }
        });
    }


    private String parseCodeWithJsoup(final String htmlcode){
        String parsedCode="";
        String []course=null;
        try {
            Document doc = Jsoup.parse(htmlcode);

            //parsedCode=parsedCode+class2;
            Elements elements=doc.select("tr.color-rowNext > td");
            for(Element element:elements){
                String name=element.text();
                parsedCode=parsedCode+name+";";
            }
            elements=doc.select("tr.color-row > td");
            for(Element element:elements) {
                String name = element.text();
                parsedCode = parsedCode + name + ";";
            }

            course=parsedCode.split(";");
            Log.d("clp","clp"+course.length);
            for(int i=0;i<course.length;i++){
                Log.d("clp","clp"+course[i]);
            }


        }catch(Exception e) {
            e.printStackTrace();
        }
        return parsedCode;
    }

    private void showGradeInListView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String [] parsedCode=parseCodeWithJsoup(htmlcode).split(";");
                String [] course = new String[2*parsedCode.length/11];
                int j=0;
                for(int i=0;i<parsedCode.length;i++){
                    if(i%11==2){
                        course[j]=parsedCode[i];
                        j++;
                    }
                    if(i%11==10){
                        course[j]=parsedCode[i];
                        j++;
                    }
                }
                adapter=new ArrayAdapter<String>(ShowGrade.this,android.R.layout.simple_list_item_1,course);
                listView.setAdapter(adapter);
            }
        });
    }

}
