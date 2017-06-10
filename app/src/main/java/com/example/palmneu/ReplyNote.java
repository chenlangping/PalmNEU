package com.example.palmneu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReplyNote extends AppCompatActivity {

    private Button button=null;
    private EditText editText=null;
    private String ID=null;
    private String userName=null;
    private String time=null;
    private String noteTitle=null;
    private String noteContent=null;

    //陈瑜峰做显示用到的
    private RecyclerView mRecyclerView = null;
    private ArrayAdapter<String> adapter = null;
    String[] noteMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_note);

        initViews();
        initData();

        Log.d("clp",ID+userName+time+noteTitle+noteContent);

        showNoteDetail();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyNote();
            }
        });


    }

    private void initViews(){
        button=(Button)findViewById(R.id.button);
        editText=(EditText)findViewById(R.id.edittext);
        mRecyclerView = (RecyclerView)findViewById(R.id.reply_recycler_view);
    }

    private void initData(){
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        userName=intent.getStringExtra("userName");
        time=intent.getStringExtra("time");
        noteTitle=intent.getStringExtra("noteTitle");
        noteContent=intent.getStringExtra("noteContent");
    }

    private void replyNote(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("clp", "开始传输信息");

                    OkHttpClient client = new OkHttpClient();

                    //SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    //String ID = pref.getString("ID", "0");

                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName","chen")
                            .add("passWord","1234")
                            .add("ID",ID)
                            .add("replyContent",editText.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "reply_note.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("clp",responseDate);

                    if(responseDate.indexOf("SUCCESS")!=-1){
                        ToastShow("成功发帖");
                        //TODO 刷新一下
                    }else{
                        ToastShow("失败");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showNoteDetail(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("clp", "开始传输信息");

                    OkHttpClient client = new OkHttpClient();

                    //SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    //String ID = pref.getString("ID", "0");

                    RequestBody requestBody = new FormBody.Builder()
                            .add("noteID",ID)
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "show_note_detail.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    //TODO 这里的responseData就是需要放在列表里的数据

                    Log.d("clp",responseDate);
                    //chen#2017-05-19 19:20:41#测试@chen#2017-05-19 20:28:26#测试2@chen#2017-05-19 20:28:29#测试3@chen#2017-05-19 20:28:30#测试4@chen#2017-06-10 14:58:33#@chen#2017-06-10 14:58:52#huitiela@chen#2017-06-10 14:59:29#huitiela@chen#2017-06-10 15:01:48#huitiela@
                    noteMessage =responseDate.split("@");

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//这里用线性显示 类似于listview
                    ReplyRecyclerViewAdapter mAdapter =new ReplyRecyclerViewAdapter(noteMessage);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
