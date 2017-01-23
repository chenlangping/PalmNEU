package com.example.palmneu;

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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginIn extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText checknumberEdit;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button login;
    private Button getPicture;
    private ImageView imageView;
    private URL url;
    private HttpURLConnection connection;
    private BufferedReader reader;
    private TextView responseText;
    private final String NEU = "http://202.118.31.197";
    private final String BAIDU = "http://www.baidu.com";
    private String cookie = null;
    private String picturesrc = "";
    private String htmlcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        checknumberEdit=(EditText)findViewById(R.id.check_number);
        getPicture = (Button) findViewById(R.id.get_picture);
        login = (Button) findViewById(R.id.login_in);
        responseText = (TextView) findViewById(R.id.response_text);
        imageView = (ImageView) findViewById(R.id.check_picture);
        accountEdit.setText(preferences.getString("account", ""));
        passwordEdit.setText(preferences.getString("password", ""));
        getcookieandpicturesrc();//获取cookie和验证码图片的地址
        Log.d("LoginIn", "clp1" + picturesrc);
        Log.d("LoginIn", "clp2" + cookie);


        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIn();
            }
        });


    }

    private void getcookieandpicturesrc() {
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
                    Log.d("LoginIn", "clp1" + picturesrc);
                    Log.d("LoginIn", "clp2" + cookie);

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

    private void loginIn() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                String param=null;
                try {
                    URL url = new URL("http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Cookie", cookie);
                    connection.setRequestProperty("Referer","http://202.118.31.197/");
                    DataOutputStream out=new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("WebUserNO=20144837&Password=214365879&Agnomen="+checknumberEdit.getText().toString()+"&submit7=%B5%C7%C2%BC");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    InputStream in = connection.getInputStream();
                    Log.d("LoginIn","clp"+String.valueOf(connection.getResponseCode()));
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    htmlcode = response.toString();
                    showResponse(htmlcode);
                    Log.d("LoginIn","clp code="+htmlcode);

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
}
