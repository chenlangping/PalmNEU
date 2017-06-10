package com.example.palmneu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by chenyufeng on 17/6/10.
 */

public class ReplyRecyclerViewAdapter extends RecyclerView.Adapter<ReplyRecyclerViewAdapter.ReplyTextViewHolder> implements View.OnClickListener {

    //贴子内容字符串数组
    private String[] noteMessage = null;

    public ReplyRecyclerViewAdapter(String[] noteMessage) {
        this.noteMessage = noteMessage;
    }

    @Override
    public ReplyRecyclerViewAdapter.ReplyTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.activity_note, parent, false));
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_note_item, parent, false);
        ReplyRecyclerViewAdapter.ReplyTextViewHolder replyTextViewHolder = new ReplyRecyclerViewAdapter.ReplyTextViewHolder(view);
        //为创建的view注册点击事件
        view.setOnClickListener(this);
        return replyTextViewHolder;
    }


    private ReplyRecyclerViewAdapter.OnItemClickListener mOnItemClickListener = null;


    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //chen#2017-05-19 19:20:41#测试

    @Override
    public void onBindViewHolder(ReplyRecyclerViewAdapter.ReplyTextViewHolder holder, int position) {
        if ((noteMessage[position].split("#").length == 3)) {
            holder.userName.setText(noteMessage[position].split("#")[0]);

            holder.time.setText(noteMessage[position].split("#")[1]);

            holder.noteContent.setText(noteMessage[position].split("#")[2]);

            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return noteMessage == null ? 0 : noteMessage.length;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }

    }


    static class ReplyTextViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView time;
        TextView noteContent;

        public ReplyTextViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.note_userName);
            time = (TextView) itemView.findViewById(R.id.note_time);
            noteContent = (TextView) itemView.findViewById(R.id.note_noteContent);
        }
    }
}
