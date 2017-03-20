package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

                //userName=chen&passWord=1234&nickName=123jeo&emailAddress=123@163.com
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
                    Log.d("clp","返回信息:"+responseData);


                    if (responseData.indexOf("0") != -1) {
                        //返回0，表示成功注册
                        toastShow("注册成功");
                        //注册成功就返回登录界面，并且帮助用户填上账号密码
                        Intent intent=new Intent();
                        intent.putExtra("userName",account);
                        intent.putExtra("passWord",password);
                        setResult(1,intent);//1代表的是正确注册的结果
                        finish();
                    }else if (responseData.indexOf("1") != -1) {
                        toastShow("email地址已经被注册");
                    }else if (responseData.indexOf("2") != -1) {
                        toastShow("昵称已被注册");
                    }else if (responseData.indexOf("3") != -1) {
                        toastShow("昵称和email地址都被注册了");
                    }else if (responseData.indexOf("4") != -1) {
                        toastShow("用户名已存在");
                    }else if (responseData.indexOf("5") != -1) {
                        toastShow("用户名和email地址均已存在");
                    }else if (responseData.indexOf("6") != -1) {
                        toastShow("用户名和昵称均已存在");
                    }else if (responseData.indexOf("7") != -1) {
                        toastShow("用户名和昵称和email地址均已存在！");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void toastShow(final String msg) {//提示用户无法连接到校园网
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserRegister.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //如果用户没有注册而是通过返回键返回的，那么返回值是2
        Intent intent=new Intent();
        setResult(2,intent);
        finish();
    }
}

