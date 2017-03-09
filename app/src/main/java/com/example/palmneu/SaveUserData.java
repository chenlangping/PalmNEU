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
    private EditText ipgwAccountEdit;
    private EditText ipgwPasswordEdit;
    private Button save;
    private Button clear;
    private Button ipgwsave;
    private Button ipgwclear;
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

        ipgwAccountEdit=(EditText)findViewById(R.id.ipgw_account);
        ipgwPasswordEdit=(EditText)findViewById(R.id.ipgw_password);

        save=(Button)findViewById(R.id.save_data);
        clear=(Button)findViewById(R.id.clear_data);

        ipgwsave=(Button)findViewById(R.id.ipgw_save_data);
        ipgwclear=(Button)findViewById(R.id.ipgw_clear_data);


        accountEdit.setText(preferences.getString("account",""));
        passwordEdit.setText(preferences.getString("password",""));

        ipgwAccountEdit.setText(preferences.getString("ipgwaccount",""));
        ipgwPasswordEdit.setText(preferences.getString("ipgwpassword",""));

        save.setOnClickListener(new View.OnClickListener(){
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

        ipgwsave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String account=ipgwAccountEdit.getText().toString();
                String password=ipgwPasswordEdit.getText().toString();
                editor.putString("ipgwaccount",account);
                editor.putString("ipgwpassword",password);
                editor.apply();
                finish();
            }
        });

        ipgwclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ipgwaccount","");
                editor.putString("ipgwpassword","");
                editor.apply();
                finish();
            }
        });


    }
}
