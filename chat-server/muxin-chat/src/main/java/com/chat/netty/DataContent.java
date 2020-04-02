package com.chat.netty;

import com.chat.pojo.ChatMsg;

import java.io.Serializable;

public class DataContent implements Serializable {
    private Integer action;
    private ChatMsg chatMsg;
    private String extend;

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
