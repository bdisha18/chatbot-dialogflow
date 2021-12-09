package com.example.chatbot;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView messageSend, messageReceive;
    ImageView userImage, botImage;
    View view;


    public ViewHolder(View itemView) {
        super(itemView);
        this.messageSend = itemView.findViewById(R.id.message_send);
        this.messageReceive = itemView.findViewById(R.id.message_receive);
        this.userImage = itemView.findViewById(R.id.user_image);
        this.botImage = itemView.findViewById(R.id.bot_image);
        this.view = itemView;
    }
}
