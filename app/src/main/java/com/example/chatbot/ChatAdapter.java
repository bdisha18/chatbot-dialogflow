package com.example.chatbot;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ViewHolder> {

    TextView messageSend, messageReceive;
    ImageView userImage, botImage;
    View view;
    private List<Message> messageList;
    private Activity activity;


    public ChatAdapter(List<Message> messageList, Activity activity) {
        this.messageList = messageList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_message, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String message = messageList.get(position).getMessage();
        boolean isReceived = messageList.get(position).getIsReceived();
        if (isReceived) {
            holder.messageReceive.setVisibility(View.VISIBLE);
            holder.messageSend.setVisibility(View.GONE);
            holder.botImage.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.GONE);
            holder.messageReceive.setText(message);
        } else {
            holder.messageSend.setVisibility(View.VISIBLE);
            holder.messageReceive.setVisibility(View.GONE);
            holder.botImage.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.messageSend.setText(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}



