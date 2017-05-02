package com.example.palmneu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by chenyufeng on 17/3/30.
 */

public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> implements View.OnClickListener {


    //贴子内容字符串数组
    private String[] noteMessage = null;

    public NormalRecyclerViewAdapter(String[] noteMessage) {
        this.noteMessage = noteMessage;
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.activity_note, parent, false));
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        NormalTextViewHolder normalTextViewHolder = new NormalTextViewHolder(view);
        //为创建的view注册点击事件
        view.setOnClickListener(this);
        return normalTextViewHolder;
    }

    private OnItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }


    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.ID.setText(noteMessage[position].split("#")[0]);
        holder.userName.setText(noteMessage[position].split("#")[1]);
        holder.time.setText(noteMessage[position].split("#")[2]);
        holder.noteTitle.setText(noteMessage[position].split("#")[3]);
        holder.noteContent.setText(noteMessage[position].split("#")[4]);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return noteMessage == null ? 0 : noteMessage.length;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view,(int)view.getTag());
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class NormalTextViewHolder extends RecyclerView.ViewHolder {

        TextView ID;
        TextView userName;
        TextView time;
        TextView noteTitle;
        TextView noteContent;

        public NormalTextViewHolder(View itemView) {
            super(itemView);
            ID = (TextView) itemView.findViewById(R.id.note_ID);
            userName = (TextView) itemView.findViewById(R.id.note_userName);
            time = (TextView) itemView.findViewById(R.id.note_time);
            noteTitle = (TextView) itemView.findViewById(R.id.note_noteTitle);
            noteContent = (TextView) itemView.findViewById(R.id.note_noteContent);


        }
    }

}
