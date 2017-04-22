package com.example.palmneu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by chenyufeng on 17/3/30.
 */

public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {


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
        return normalTextViewHolder;
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.ID.setText(noteMessage[position].split("#")[0]);
        holder.userName.setText(noteMessage[position].split("#")[1]);
        holder.time.setText(noteMessage[position].split("#")[2]);
        holder.noteTitle.setText(noteMessage[position].split("#")[3]);
        holder.noteContent.setText(noteMessage[position].split("#")[4]);
    }

    @Override
    public int getItemCount() {
        return noteMessage == null ? 0 : noteMessage.length;
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
