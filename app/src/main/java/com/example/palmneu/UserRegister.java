package com.example.palmneu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class UserRegister extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private EditText nickName;//昵称编辑框
    private EditText emailAddress;//邮箱编辑框
    private Button register;//注册按钮
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);





    }

    /*private boolean check(String username ,String password ,String nickname,String mail ){

        if(username== null || password == null || nickname  == null || mail  == null||isLegitimateMail(mail))
            return false;

        else
            return true;

    }

    public boolean isLegitimateMail(String mail){
        char[] charArray = mail.toCharArray();
        for(char c : charArray){
            if(!('a' <= c && 'z' >= c || 'A' <= c && 'Z' >= c || c == '+' || c == '@' || c == '.')){
                return false;
            }
        }
        String[] strAtAndDot = mail.split("@|\\.|\\+");
        for(String str : strAtAndDot){
            if(str.equals("")){
                return false;	// 2, 3, 4 OK.
            }
        }
        String[] strAt = mail.split("@");
        if(strAt.length != 2){
            return false;	// 1 OK.
        }
        String[] strAtDot = strAt[1].split("\\.");
        if(strAtDot.length != 2){
            return false;	// It should be one "." after "@".
        }
        return true;
    }*/

}

