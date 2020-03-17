package com.mkleo.chat.widget;

import android.support.v4.widget.SwipeRefreshLayout;

import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.Record;
import com.mkleo.chat.bean.Text;
import com.mkleo.chat.bean.User;
import com.mkleo.chat.utils.MediaUtil;

/**
 * 事件管理器
 */
public class ChatEventManager implements ChatViewManager.IEventManager {

    @Override
    public void onSwipeRefresh(final ChatLayout chatLayout, final SwipeRefreshLayout swipeRefreshLayout) {
        ChatLayout.OnChatListener listener = chatLayout.getOptions().getChatListener();
        if (null != listener) {
            listener.onSwipeRefresh(chatLayout);
        }
    }

    @Override
    public void onTextSend(ChatLayout chatLayout, User mine, String text) {
        Message<Text> message = new Message<>(mine, System.currentTimeMillis());//创建一个message
        message.setContent(new Text(text));
        message.setState(Message.State.PROCESSING);
        chatLayout.sendMessage(message);
    }

    @Override
    public void onPictureSend(ChatLayout chatLayout, User mine, String path) {
        //需要自己实现
    }

    @Override
    public void onVideoSend(ChatLayout chatLayout, User mine, String path) {
        //需要自己实现
    }

    @Override
    public void onRecordSend(ChatLayout chatLayout, User mine, String path) {
        Message<Record> message = new Message<>(mine, System.currentTimeMillis());
        int length = MediaUtil.getMediaLength(chatLayout.getContext(), path);
        Record record = new Record(path, length);
        message.setContent(record);
        message.setState(Message.State.PROCESSING);
        chatLayout.sendMessage(message);
    }
}
