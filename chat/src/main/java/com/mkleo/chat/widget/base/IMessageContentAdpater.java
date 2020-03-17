package com.mkleo.chat.widget.base;

import android.view.ViewGroup;

import com.mkleo.chat.bean.IContent;
import com.mkleo.chat.bean.Message;

public interface IMessageContentAdpater<T extends IContent> {

    /**
     * 回调
     */
    interface Callback {
        void onResendClick(Message message);
    }

    /**
     * 设置布局监听
     *
     * @param callback
     */
    void setCallback(Callback callback);

    /**
     * 收到消息
     *
     * @param messageLayout
     * @param message
     */
    void onBindMessage(ViewGroup messageLayout, Message<T> message);

}
