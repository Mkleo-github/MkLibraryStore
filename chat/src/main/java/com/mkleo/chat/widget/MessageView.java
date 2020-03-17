package com.mkleo.chat.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.mkleo.chat.bean.Message;

import java.util.List;

public class MessageView extends RecyclerView {

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 移动到底部
     */
    public void scrollToBottom() {
        post(new Runnable() {
            @Override
            public void run() {
                if (null != getAdapter()) {
                    if (getAdapter().getItemCount() > 0) {
                        scrollToPosition(getAdapter().getItemCount() - 1);
                    }
                }
            }
        });
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(final Message message) {
        post(new Runnable() {
            @Override
            public void run() {
                getMessageAdapter().sendMessage(message);
                scrollToBottom();
            }
        });
    }

    /**
     * 添加消息
     *
     * @param messages
     */
    public void addHistoryMessages(final List<Message> messages) {
        post(new Runnable() {
            @Override
            public void run() {
                getMessageAdapter().addHistoryMessages(messages);
            }
        });
    }

    /**
     * 更新消息
     *
     * @param message
     */
    public void updateMessage(final Message message) {
        post(new Runnable() {
            @Override
            public void run() {
                getMessageAdapter().updateMessage(message);
            }
        });
    }

    /**
     * 设置新消息
     *
     * @param messages
     */
    public void setMessages(final List<Message> messages) {
        post(new Runnable() {
            @Override
            public void run() {
                getMessageAdapter().setMessages(messages);
            }
        });
    }

    /**
     * 刷新数据
     */
    public void postRefresh() {
        post(new Runnable() {
            @Override
            public void run() {
                getMessageAdapter().notifyDataSetChanged();
            }
        });
    }


    private MessageAdapter getMessageAdapter() {
        return (MessageAdapter) getAdapter();
    }
}
