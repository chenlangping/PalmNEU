package com.example.palmneu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Note extends AppCompatActivity {

//    private ListView listView = null;

    private RecyclerView mRecyclerView = null;

    private ArrayAdapter<String> adapter = null;

    String[] noteMessage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //添加返回按钮到导航栏

        //SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        //int id= pref.getInt("ID",0);
        //listView = (ListView) findViewById(R.id.list_view);

        getLatestNote();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮的实现
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:return  true;
        }

    }

    private void getLatestNote() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("clp", "开始传输信息");
                    OkHttpClient client = new OkHttpClient();

                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    String ID = pref.getString("ID", "0");

                    RequestBody requestBody = new FormBody.Builder()
                            .add("ID", ID)
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "push_note.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();


                    //测试用字符串
//                    responseDate ="ID#userName#time#noteTitle#@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@";

                    noteMessage = responseDate.split("@");
                    Log.d("clp", responseDate);


                    showNoteMessage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void showNoteMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新
//                adapter = new ArrayAdapter<String>(Note.this, android.R.layout.simple_list_item_1, noteMessage);
//                listView.setAdapter(adapter);
//                  mRecyclerView.postInvalidate();


                mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//这里用线性显示 类似于listview
                NormalRecyclerViewAdapter mAdapter =new NormalRecyclerViewAdapter(noteMessage);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                mAdapter.setOnItemClickListener(new NormalRecyclerViewAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view , int position){
                        ToastShow(noteMessage[position].split("#")[0]);
                        Log.d("clp","ID为："+String.valueOf(noteMessage[position].split("#")[0]));
                        Intent intent=new Intent(getApplication(),ReplyNote.class);
                        intent.putExtra("ID",String.valueOf(noteMessage[position].split("#")[0]));
                        intent.putExtra("userName",String.valueOf(noteMessage[position].split("#")[1]));
                        intent.putExtra("time",String.valueOf(noteMessage[position].split("#")[2]));
                        intent.putExtra("noteTitle",String.valueOf(noteMessage[position].split("#")[3]));
                        intent.putExtra("noteContent",String.valueOf(noteMessage[position].split("#")[4]));
                        startActivity(intent);

                    }
                });
            }
        });
    }

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Note.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
