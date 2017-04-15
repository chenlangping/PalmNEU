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

public class SendNote extends AppCompatActivity {

    private String userName="";
    private String passWord="";
    //用户名和密码

    private String noteTitle="";
    private String noteContent="";
    //帖子标题和帖子内容

    EditText noteTitleEditText=null;
    EditText noteContentEditText=null;
    private Button button=null;
    //分别对应三个控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_note);
        initView();
        userName="chen";
        passWord="1234";
        //TODO 用来测试，直接取固定值，以后改

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteTitle=noteTitleEditText.getText().toString();
                noteContent=noteContentEditText.getText().toString();
                sendNote();
            }
        });
    }

    private void initView(){
        noteTitleEditText=(EditText)findViewById(R.id.noteTitle);
        noteContentEditText=(EditText)findViewById(R.id.noteContent);
        button=(Button)findViewById(R.id.sendnote);
    }

    private void sendNote(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();

                    RequestBody requestBody=new FormBody.Builder()
                            .add("userName",userName)
                            .add("passWord",passWord)
                            .add("noteTitle",noteTitle)
                            .add("noteContent",noteContent)
                            .build();

                    Request request= new Request.Builder()
                            .url(new DataClass().serveraddress + "add_note.php")
                            .post(requestBody)
                            .build();
                    Response response= client.newCall(request).execute();
                    String responseData= response.body().string();
                    Log.d("clp",responseData);

                    if(responseData.indexOf("INTODBS")!=-1){
                        ToastShow("发帖成功！");
                        Intent intent =new Intent(SendNote.this,Note.class);
                        startActivity(intent);
                    }else if(responseData.indexOf("PWDWORNG")!=-1){
                        ToastShow("密码错误！");
                    }else if(responseData.indexOf("NOUSER")!=-1){
                        ToastShow("用户不存在！");
                    }else {
                        ToastShow("远端数据库出错");
                    }
                }catch (Exception e){
                    ToastShow("错误");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ToastShow(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
