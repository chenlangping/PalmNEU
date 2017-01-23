package com.example.palmneu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginIn extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button login;
    private URL url;
    private HttpURLConnection connection;
    private BufferedReader reader;
    private TextView responseText;
    private final String NEU = "http://202.118.31.197";
    private final String BAIDU = "http://www.baidu.com";
    private String cookie=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_in);
        responseText = (TextView) findViewById(R.id.response_text);
        accountEdit.setText(preferences.getString("account", ""));
        passwordEdit.setText(preferences.getString("password", ""));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpURLConnection();
            }
        });

    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(NEU);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    //response中存的就是返回的html页面的代码
                    StringBuilder responsehead = new StringBuilder();
                    String key;
                    for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                        responsehead.append(key+":"+connection.getHeaderField(key));
                        if(key.equals("Set-Cookie")){
                            cookie=connection.getHeaderField(key);
                        }
                    }
                    cookie=cookie.substring(0,cookie.length()-8);
                    //responsehead中存的是响应头的数据
                    //showResponse(response.toString());
                    showResponse(cookie);


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
                responseText.setText(response);
                //处理
            }
        });
    }


}
