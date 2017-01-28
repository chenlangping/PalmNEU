package com.example.palmneu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginIn extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText checkNumberEdit;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button getPicture;
    private Button getGrade;
    private ImageView imageView;
    private URL url;
    private HttpURLConnection connection;
    private BufferedReader reader;
    private final String NEU = "http://202.118.31.197";
    private String cookie = null;
    private String picturesrc = null;
    private String htmlcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        checkNumberEdit = (EditText) findViewById(R.id.check_number);
        getPicture = (Button) findViewById(R.id.get_picture);
        getGrade = (Button) findViewById(R.id.get_grade);
        imageView = (ImageView) findViewById(R.id.check_picture);
        accountEdit.setText(preferences.getString("account", ""));
        passwordEdit.setText(preferences.getString("password", ""));
        getCookieAndPictureSrc();//获取cookie和验证码图片的地址
        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        getGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String WebUserNO = accountEdit.getText().toString();
                String Password = passwordEdit.getText().toString();
                String Agnomen = checkNumberEdit.getText().toString();
                Intent intent = new Intent(LoginIn.this, ShowGrade.class);
                intent.putExtra("cookie", cookie);
                intent.putExtra("WebUserNO", WebUserNO);
                intent.putExtra("Password", Password);
                intent.putExtra("Agnomen", Agnomen);
                startActivity(intent);
            }
        });

    }

    private void getCookieAndPictureSrc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(NEU);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", cookie);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    htmlcode = response.toString();
                    StringBuilder responsehead = new StringBuilder();
                    String key;
                    for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                        responsehead.append(key + ":" + connection.getHeaderField(key));
                        if (key.equals("Set-Cookie")) {
                            cookie = connection.getHeaderField(key);
                        }
                    }
                    cookie = cookie.substring(0, cookie.length() - 8);
                    //TODO 存在隐患！
                    //responsehead中存的是响应头的数据
                    //showResponse(response.toString());
                    picturesrc = htmlcode.substring(htmlcode.indexOf("ACTIONVALIDATERANDOMPICTURE"), htmlcode.indexOf("ACTIONVALIDATERANDOMPICTURE") + 64);
                    picturesrc = picturesrc.substring(0, picturesrc.indexOf("\""));
                    //showResponse(picturesrc);
                    Log.d("LoginIn", "clp图片的地址" + picturesrc);
                    Log.d("LoginIn", "clpcookie值为 " + cookie);

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

    private void getPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Bitmap bitmap = null;
                try {
                    URL url = new URL(NEU + "/" + picturesrc);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", cookie);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    showPicture(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showPicture(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
                //处理
            }
        });
    }



}