package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRegister extends AppCompatActivity {

    private EditText accountEdit;//账号编辑框
    private EditText passwordEdit;//密码编辑框
    private EditText ensurepasswordEdit; //确认密码编辑框
    private EditText nickNameEdit;//昵称编辑框
    private EditText emailAddressEdit;//邮箱编辑框
    private Button register;//注册按钮

    private String account;
    private String password;
    private String ensurepassword;
    private String nickname;
    private String emailaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        initView();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                ensurepassword = ensurepasswordEdit.getText().toString();
                nickname = nickNameEdit.getText().toString();
                emailaddress = emailAddressEdit.getText().toString();

                //密码确认
                while (!ensurePassword(password,ensurepassword)) {
                    showToast("你两次输入的密码不一样，重新输入确认密码");
                }
                //重新输入确认密码后点击注册button

                    switch (check(account, password, nickname, emailaddress)){
                    case 1:
                        sendRegisterMessageToServer(account, password, nickname, emailaddress);
                        break;
                    case 2:
                        showToast("账户长度必须为6-18位");
                        break;
                    case 3:
                        showToast("账户中存在非法字符");
                        break;
                    case 4:
                        showToast("密码长度必须为6-16位");
                        break;
                    case 5:
                        showToast("昵称长度必须为6-18位");
                        break;
                    case 6:
                        showToast("昵称中存在非法字符");
                        break;
                    case 7:
                        showToast("邮箱地址长度非法");
                        break;
                    case 8:
                        showToast("邮箱地址中存在非法字符");
                        break;
                    case 9:
                        showToast("邮箱地址非法");
                        break;
                    case 10:
                        showToast("发生未知错误");
                        break;

                }
//                if (check(account, password, nickname, emailaddress)) {
//                    //手机端通过检查 发送信息给服务器
//                    sendRegisterMessageToServer(account, password, nickname, emailaddress);
//                } else {
//                    //告知用户错误信息
//                }


            }
        });
    }

    private void initView() {
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        ensurepasswordEdit = (EditText) findViewById(R.id.ensurepassword);
        nickNameEdit = (EditText) findViewById(R.id.nickname);
        emailAddressEdit = (EditText) findViewById(R.id.emailaddress);
        register = (Button) findViewById(R.id.register);
    }

    private boolean ensurePassword (String password, String ensurepassword){
        if (password.equals(ensurepassword)){
            return true;   //密码和确认密码一致
        }
        else
            return  false;   //密码和确认密码不一致
    }
    private int check(String account, String password, String nickname, String emailaddress) {
        //username：6~18位字符，只能包含英文字母、数字、下划线
        //password:6-16位字符，数字，字母(区分大小写），特殊字符组成
        //nickname:6~18 位字符，只能包含英文字母、数字、下划线
        //emailaddress:地址4-16个字符，字母（区分大小写），数字，下划线组成。下划线不能在首尾。

        if (isLigitimateAccount(account) == 1 && isLigitimatePassword(password) == 1 && isLigitimateNickname(nickname) == 1 && isLigitimateEmailAddress(emailaddress) == 1)
            return 1;
        else if (isLigitimateAccount(account) == 2) {
            //提示：账户位数超出
            return 2;
        } else if (isLigitimateAccount(account) == 3) {
            //提示：账户只能包含字母，数字，下划线。
            return 3;
        } else if (isLigitimatePassword(password) == 2) {
            //提示：密码长度超
            return 4;
        } else if (isLigitimateNickname(nickname) == 2) {
            //提示：昵称长度超出
            return 5;
        } else if (isLigitimateNickname(nickname) == 3) {
            //提示：昵称只能含有数字，字母，下划线
            return 6;
        } else if (isLigitimateEmailAddress(emailaddress) == 2) {
            //提示：邮箱长度超出
            return 7;
        } else if (isLigitimateEmailAddress(emailaddress) == 3) {
            //提示：邮箱只能含有数字，字母，下划线
            return 8;
        } else if (isLigitimateEmailAddress(emailaddress) == 4) {
            //提示：邮箱首位和末位不能使用下划线
            return 9;
        }else{
            return 10;
        }



    }

    private int isLigitimateAccount(String account) {
        int size = account.length();
        if (size >= 6 && size <= 18) {
            char[] charArray = account.toCharArray();
            for (char c : charArray) {
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '1' && c <= '9' || c == '_'))
                    return 3; //不正确，只能包含字母，数字，下划线。
            }
        } else {
            return 2; //不正确，username超过6~18位
        }
        return 1; //正确
    }

    private int isLigitimatePassword(String password) {
        int size = password.length();
        if (size >= 6 && size <= 16) {
            return 1; //正确
        }
        return 2; //不正确，密码长度超
    }

    private int isLigitimateNickname(String nickname) {
        int size = nickname.length();
        if (size >= 6 && size <= 18) {
            char[] charArray = nickname.toCharArray();
            for (char c : charArray) {
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '1' && c <= '9' || c == '_'))
                    return 3; //不正确，只能字母数字下划线
            }
        } else {
            return 2;//不正确，长度超出
        }

        return 1; //正确
    }

    private int isLigitimateEmailAddress(String emailaddress) {
        String[] newstr = emailaddress.split("@");
        int size = newstr[0].length();
        int first = newstr[0].indexOf("_");
        int last = newstr[0].lastIndexOf("_");
        if (first == 0 || last == newstr[0].length() - 1) {
            return 4;   //不正确，首位末尾不能为下划线
        }
        if (size >= 4 && size <= 16) {
            char[] charArray = newstr[0].toCharArray();
            for (char c : charArray) {
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '1' && c <= '9' || c == '_')) {
                    return 3;  //不正确，邮箱地址不能为字母数字下划线
                }
            }
        } else {
            return 2; //不正确，长度超出
        }

        return 1;  //正确
    }

    private void sendRegisterMessageToServer(final String account, final String password, final String nickname, final String emailaddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //userName=chen&passWord=1234&nickName=123jeo&emailAddress=123@163.com
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName", account)
                            .add("passWord", password)
                            .add("nickName", nickname)
                            .add("emailAddress", emailaddress)
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "register.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("clp", "返回信息:" + responseData);


                    if (responseData.indexOf("0") != -1) {
                        //返回0，表示成功注册
                        toastShow("注册成功");
                        //注册成功就返回登录界面，并且帮助用户填上账号密码
                        Intent intent = new Intent();
                        intent.putExtra("userName", account);
                        intent.putExtra("passWord", password);
                        setResult(1, intent);//1代表的是正确注册的结果
                        finish();
                    } else if (responseData.indexOf("1") != -1) {
                        toastShow("email地址已经被注册");
                    } else if (responseData.indexOf("2") != -1) {
                        toastShow("昵称已被注册");
                    } else if (responseData.indexOf("3") != -1) {
                        toastShow("昵称和email地址都被注册了");
                    } else if (responseData.indexOf("4") != -1) {
                        toastShow("用户名已存在");
                    } else if (responseData.indexOf("5") != -1) {
                        toastShow("用户名和email地址均已存在");
                    } else if (responseData.indexOf("6") != -1) {
                        toastShow("用户名和昵称均已存在");
                    } else if (responseData.indexOf("7") != -1) {
                        toastShow("用户名和昵称和email地址均已存在！");
                    }
                } catch (Exception e) {
                    toastShow("连接到服务器超时");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void toastShow(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserRegister.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String msg){
        Toast.makeText(UserRegister.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        //如果用户没有注册而是通过返回键返回的，那么返回值是2
        Intent intent = new Intent();
        setResult(2, intent);
        finish();
    }
}

