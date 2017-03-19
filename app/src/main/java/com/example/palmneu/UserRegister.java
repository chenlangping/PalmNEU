package com.example.palmneu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRegister extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private EditText nickNameEdit;//昵称编辑框
    private EditText emailAddressEdit;//邮箱编辑框
    private Button register;//注册按钮

    private String account;
    private String password;
    private String nickname;
    private String emailaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        initView();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=accountEdit.getText().toString();
                password=passwordEdit.getText().toString();
                nickname=nickNameEdit.getText().toString();
                emailaddress=emailAddressEdit.getText().toString();
                if(check(account,password,nickname,emailaddress)){
                    //手机端通过检查 发送信息给服务器
                    sendRegisterMessageToServer(account,password,nickname,emailaddress);
                }else{
                    //告知用户错误信息
                }
                //userName=chenlangping&nickName=chenlangping&emailAddress=328566090@qq.com&passWord=123456


            }
        });
    }

    private void initView(){
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        nickNameEdit=(EditText)findViewById(R.id.nickname);
        emailAddressEdit=(EditText)findViewById(R.id.emailaddress);
        register=(Button)findViewById(R.id.register);
    }

    private boolean check(String account,String password,String nickname,String emailaddress){
        return true;
    }

    private void sendRegisterMessageToServer(final String account,final String password,final String nickname,final String emailaddress){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    OkHttpClient client=new OkHttpClient();
                    RequestBody requestBody=new FormBody.Builder()
                            .add("userName",account)
                            .add("passWord",password)
                            .add("nickName",nickname)
                            .add("emailAddress",emailaddress)
                            .build();

                    Request request=new Request.Builder()
                            .url(new DataClass().serveraddress+"register.php")
                            .post(requestBody)
                            .build();

                    Response response= client.newCall(request).execute();
                    String responseData =response.body().string();
                    Log.d("register:","返回信息:"+responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }
}

