package com.example.palmneu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 陈浪平 on 2017/3/7.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
