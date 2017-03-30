package com.example.palmneu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        //SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        //int id= pref.getInt("ID",0);
        //listView = (ListView) findViewById(R.id.list_view);

        getLatestNote();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(new NormalRecyclerViewAdapter(noteMessage));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

    }

    private void getLatestNote() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

//                    Log.d("clp", "开始传输信息");
//                    OkHttpClient client = new OkHttpClient();
//
//                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
//                    String ID = pref.getString("ID", "0");
//
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("ID", ID)
//                            .build();
//
//                    Request request = new Request.Builder()
//                            .url(new DataClass().serveraddress + "push_note.php")
//                            .post(requestBody)
//                            .build();
//
//                    Response response = client.newCall(request).execute();
//                    String responseDate = response.body().string();


                    //测试用字符串
                    String responseDate ="ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@ID#userName#time#noteTitle#noteContent@";

                    noteMessage = responseDate.split("@");
                    Log.d("clp", responseDate);


//                    showNoteMessage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

//    private void showNoteMessage() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //更新
////                adapter = new ArrayAdapter<String>(Note.this, android.R.layout.simple_list_item_1, noteMessage);
////                listView.setAdapter(adapter);
//                  mRecyclerView.postInvalidate();
//
//            }
//        });
//    }
}
