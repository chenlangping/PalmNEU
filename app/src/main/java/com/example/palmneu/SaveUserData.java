package com.example.palmneu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SaveUserData extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_user_data);
        editor =getSharedPreferences("data",MODE_PRIVATE).edit();
        preferences=getSharedPreferences("data",MODE_PRIVATE);
        checkBox=(CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember =preferences.getBoolean("remember_password",false);
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        if(isRemember){
            accountEdit.setText(preferences.getString("account",""));
            passwordEdit.setText(preferences.getString("password",""));
            checkBox.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String account=accountEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                if(checkBox.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",account);
                    editor.putString("password",password);
                }else{
                    editor.clear();
                }
                editor.apply();
                finish();
            }
        });
    }
}
