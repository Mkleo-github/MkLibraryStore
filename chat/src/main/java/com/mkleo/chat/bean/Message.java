package com.mkleo.chat.bean;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

public class Message<T extends IContent> {

    public interface OnMessageListener {
        void onMessageChanged(Message message);
    }

    @IntDef({
            State.INIT,
            State.PROCESSING,
            State.SUCCESS,
            State.FAILD,
            State.LOST,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        //未发送
        int INIT = 0;
        //正在处理
        int PROCESSING = 1;
        //成功
        int SUCCESS = 2;
        //失败
        int FAILD = 3;
        //丢失
        int LOST = 4;
    }

    //消息id
    private final String id;
    //发送消息的用户
    private final User user;
    //内容
    private T content;
    //发送时间戳
    private final long timestamp;
    //发送状态
    private int state = State.INIT;
    //用于排序,临时下标
    private int position;

    private boolean isDisplayTime = false;

    private OnMessageListener onMessageListener;

    public Message(User user, long timestamp) {
        this(getRandomId(), user, timestamp);
    }


    public Message(String id, User user, long timestamp) {
        this.id = id;
        this.user = user;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    /**
     * 设置状态
     *
     * @param state
     */
    public void setState(@State int state) {
        this.state = state;
        if (null != onMessageListener)
            onMessageListener.onMessageChanged(this);
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public boolean isDisplayTime() {
        return isDisplayTime;
    }

    public void setDisplayTime(boolean isDisplayTime) {
        this.isDisplayTime = isDisplayTime;
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    /**
     * 获取随机ID
     *
     * @return
     */
    private static String getRandomId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomNum = String.valueOf(new Random().nextInt(100000));
        return timestamp + randomNum;
    }
}
