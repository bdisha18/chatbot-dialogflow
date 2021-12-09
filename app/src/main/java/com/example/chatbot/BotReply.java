package com.example.chatbot;

import android.view.View;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

public interface BotReply {
    void onClick(View v);

    void callback(DetectIntentResponse returnResponse);
}

