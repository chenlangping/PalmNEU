package com.example.palmneu;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNote extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    private String userName = "";
    private String passWord = "";
    //用户名和密码

    private String noteTitle = "";
    private String noteContent = "";
    //帖子标题和帖子内容

    EditText noteTitleEditText = null;
    EditText noteContentEditText = null;
    private Button button = null;
    private Button addPic = null;  //添加图片按钮

    //分别对应三个控件


    //三个显示图片的
    private ImageView pic1 = null;
    private ImageView pic2 = null;
    private ImageView pic3 = null;


    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_note);
        initView();
        userName = "chen";
        passWord = "1234";
        //TODO 用来测试，直接取固定值，以后改

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteTitle = noteTitleEditText.getText().toString();
                noteContent = noteContentEditText.getText().toString();
                sendNote();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(addPic);
            }
        });

    }

    private void show(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.addpic, popup.getMenu());



        //设置item的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_camera:
                        //创建File对象,用于存储拍照后的图片
                        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            imageUri = FileProvider.getUriForFile(SendNote.this, "com.example.palmneu.fileprovider", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机程序
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);
                        return true;
                    case R.id.action_album:
                        return true;
                    default:
                        return false;
                }

            }
        });

        popup.show(); //showing popup menu
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                Log.d("clp","test121");
                if (resultCode == RESULT_OK) {
                    Log.d("clp","test11");
                    try {
                        Log.d("clp","test113");
                        //将拍摄的照片显示出来
                        Bitmap bitmap=null;
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Log.d("clp",String.valueOf(bitmap));
//                        pic1.setImageBitmap(bitmap);
                        showPicture(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("clp","test122221");
                    }
                }
                break;
            default:
                break;
        }
    }
    private void showPicture(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pic1.setImageBitmap(bitmap);
            }
        });
    }

    private void initView() {
        noteTitleEditText = (EditText) findViewById(R.id.noteTitle);
        noteContentEditText = (EditText) findViewById(R.id.noteContent);
        button = (Button) findViewById(R.id.sendnote);
        addPic = (Button) findViewById(R.id.addpic);
        pic1 = (ImageView) findViewById(R.id.picture1);
        pic2 = (ImageView) findViewById(R.id.picture2);
        pic3 = (ImageView) findViewById(R.id.picture3);

    }

    private void sendNote() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName", userName)
                            .add("passWord", passWord)
                            .add("noteTitle", noteTitle)
                            .add("noteContent", noteContent)
                            .build();

                    Request request = new Request.Builder()
                            .url(new DataClass().serveraddress + "add_note.php")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("clp", responseData);

                    if (responseData.indexOf("INTODBS") != -1) {
                        ToastShow("发帖成功！");
                        finish();
                        Intent intent = new Intent(SendNote.this, Note.class);
                        startActivity(intent);
                    } else if (responseData.indexOf("PWDWORNG") != -1) {
                        ToastShow("密码错误！");
                    } else if (responseData.indexOf("NOUSER") != -1) {
                        ToastShow("用户不存在！");
                    } else {
                        ToastShow("远端数据库出错");
                    }
                } catch (Exception e) {
                    ToastShow("错误");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ToastShow(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
