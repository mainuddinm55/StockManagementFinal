package com.kcirque.stockmanagementfinal.Database.Model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String sender;
    private String receiver;
    private String msg;
    private String imageUrl;
    private boolean isSeen;

    public Chat() {
    }

    public Chat(String sender, String receiver, String msg, String imageUrl, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.imageUrl = imageUrl;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
