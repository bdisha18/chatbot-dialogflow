package com.example.chatbot.models;

public class Message {
    private String message;
    private Boolean isReceived;

    public Message(String message, Boolean isReceived) {
        this.message = message;
        this.isReceived = isReceived;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(Boolean isReceived) {
        this.isReceived = isReceived;
    }
}
