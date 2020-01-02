package com.croshe.android.base.entity;

/**
 * 消息承载体
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/29 10:53.
 */
public class MessageEntity {

    private MessageType type;
    private String action;
    private Object data;
    private int chatType;
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public enum MessageType{
        Other,//其他消息
        Image,//图片
        Text,//文本
        Web//网页
    }

}
