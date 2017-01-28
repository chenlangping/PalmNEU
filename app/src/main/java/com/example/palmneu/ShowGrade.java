package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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
    private String Agnomen = null;
    private String htmlcode = null;
    private TextView gradeText = null;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private URL url = null;
    private InputStream in=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grade);
        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        WebUserNO = intent.getStringExtra("WebUserNO");
        Password = intent.getStringExtra("Password");
        Agnomen = intent.getStringExtra("Agnomen");
        gradeText = (TextView) findViewById(R.id.show_grade_text);
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
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    htmlcode = response.toString();
                    showResponse(htmlcode);
                    Log.d("LoginIn", "clp code=" + htmlcode);

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
}
