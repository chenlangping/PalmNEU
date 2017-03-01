package com.example.palmneu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.URL;

public class WifiLoginIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_login_in);

    }

    private void connectWifi(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String username="20144830";
                String password="214365879";
                try{
                    URL url=new URL("");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
