package com.example.palmneu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SaveUserData extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button clear;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_user_data);
        editor =getSharedPreferences("userdata",MODE_PRIVATE).edit();
        preferences=getSharedPreferences("userdata",MODE_PRIVATE);
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        clear=(Button)findViewById(R.id.clear);
        accountEdit.setText(preferences.getString("account",""));
        passwordEdit.setText(preferences.getString("password",""));
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String account=accountEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                editor.putString("account",account);
                editor.putString("password",password);
                editor.apply();
                finish();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("account","");
                editor.putString("password","");
                editor.apply();
                finish();
            }
        });


    }
}
