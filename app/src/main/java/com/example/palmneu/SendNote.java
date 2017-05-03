package com.example.palmneu;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.jar.Manifest;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNote extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
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
    private int picNum = 0;
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
        picNum = 0;
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
                        if (ContextCompat.checkSelfPermission(SendNote.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SendNote.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            openAlbum();
                        }
                        return true;
                    default:
                        return false;
                }

            }
        });


        popup.show(); //showing popup menu
    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                Log.d("clp", "test121");
                if (resultCode == RESULT_OK) {
                    Log.d("clp", "test11");
                    try {
                        Log.d("clp", "test113");
                        //将拍摄的照片显示出来
                        Bitmap bitmap = null;
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Log.d("clp", String.valueOf(bitmap));
//                        pic1.setImageBitmap(bitmap);
                        showPicture(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("clp", "test122221");
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4及以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                    break;
                }
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //如果是content类型的Uri,则使用普通方式处理
                imagePath = getImagePath(uri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的照片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            switch (picNum) {
                case 0:
                    pic1.setImageBitmap(bitmap);
                    picNum++;
                    break;
                case 1:
                    pic2.setImageBitmap(bitmap);
                    picNum++;
                    break;
                case 2:
                    pic3.setImageBitmap(bitmap);
                    picNum++;
                    break;
                default:
                    ToastShow("没有位置显示照片了");
            }
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPicture(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (picNum) {
                    case 0:
                        pic1.setImageBitmap(bitmap);
                        picNum++;
                        break;
                    case 1:
                        pic2.setImageBitmap(bitmap);
                        picNum++;
                        break;
                    case 2:
                        pic3.setImageBitmap(bitmap);
                        picNum++;
                        break;
                    default:
                        ToastShow("没有位置显示照片了");
                }

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
