package com.example.palmneu;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by 陈浪平 on 2017/3/1.
 */

public class BottomLayout extends LinearLayout {

    public BottomLayout (final Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.bottom,this);
        Button bottomChat = (Button)findViewById(R.id.chat);
        Button bottomFriend =(Button)findViewById(R.id.friend);
        Button bottomLife=(Button)findViewById(R.id.life);
        Button bottomMe=(Button)findViewById(R.id.me);

        bottomChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Chat.class);
                context.startActivity(intent);
            }
        });

        bottomFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Friend.class);
                context.startActivity(intent);
            }
        });

        bottomLife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Life.class);
                context.startActivity(intent);
            }
        });

        bottomMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Me.class);
                context.startActivity(intent);
            }
        });
    }
}
