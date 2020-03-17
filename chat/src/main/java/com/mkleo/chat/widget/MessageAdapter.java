package com.mkleo.chat.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.Picture;
import com.mkleo.chat.bean.Record;
import com.mkleo.chat.bean.Text;
import com.mkleo.chat.bean.Video;
import com.mkleo.chat.widget.base.IMessageContentAdpater;

import java.util.List;
import java.util.Vector;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public interface Callback {
        void onMessageResend(Message message);

        void onMessageChanged(Message message);
    }

    private final Vector<Message> mMessages = new Vector<>();
    private Callback mCallback;
    private IMessageContentAdpater.Callback mContentCallback = new IMessageContentAdpater.Callback() {
        @Override
        public void onResendClick(Message message) {
            if (null != mCallback)
                mCallback.onMessageResend(message);
        }
    };
    private Message.OnMessageListener mOnMessageListener = new Message.OnMessageListener() {
        @Override
        public void onMessageChanged(Message message) {
            if (null != mCallback)
                mCallback.onMessageChanged(message);
        }
    };
    //记录最早显示时间的消息
    private Message mOldDisplayTimeMessage = null;
    //记录最新显示时间的消息
    private Message mNewDisplayTimeMessage = null;
    //显示时间间隔
    private static final long DISPLAY_TIME_INTERVAL = 5 * 60 * 1000;

    MessageAdapter() {
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        IMessageContentAdpater<?> messageContentAdpater = null;
        final Message message = mMessages.get(position);
        message.setOnMessageListener(mOnMessageListener);
        if (message.getContent() instanceof Text) {             //文本内容
            messageContentAdpater = new TextMessageAdapter();
        } else if (message.getContent() instanceof Record) {    //录音内容
            messageContentAdpater = new RecordMessageAdpater();
        } else if (message.getContent() instanceof Picture) {   //图片内容
            messageContentAdpater = new PictureMessageAdapter();
        } else if (message.getContent() instanceof Video) {     //视频内容
            messageContentAdpater = new VideoMessageAdapter();
        }
        if (messageContentAdpater != null) {
            messageContentAdpater.setCallback(mContentCallback);
            messageContentAdpater.onBindMessage(holder.flMessageLayout, message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * 设置回调
     *
     * @param callback
     * @return
     */
    public MessageAdapter setCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }


    /**
     * 发送消息给adapter
     *
     * @param message
     */
    public void sendMessage(final Message message) {
        if (null != message) {
            mMessages.add(message);
            onSetMessageTimeDisplay();
            notifyItemChanged(mMessages.size() - 1);
        }
    }

    /**
     * 添加历史消息
     *
     * @param messages
     */
    public void addHistoryMessages(List<Message> messages) {
        if (null != messages) {
            mMessages.addAll(0, messages);
            onSetMessageTimeDisplay();
            notifyItemRangeInserted(0, messages.size());
        }
    }

    /**
     * 更新消息
     *
     * @param message
     */
    public void updateMessage(Message message) {
        synchronized (mMessages) {
            if (mMessages.contains(message)) {
                for (int i = 0; i < mMessages.size(); i++) {
                    Message localMessage = mMessages.get(i);
                    if (localMessage == message) {
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 设置消息,将原本的消息列表替换
     *
     * @param messages
     */
    public void setMessages(final List<Message> messages) {
        if (null != messages) {
            mMessages.clear();
            mMessages.addAll(messages);
            onSetMessageTimeDisplay();
            notifyDataSetChanged();
        }
    }

    /**
     * 消息发生变化,主要控制消息显示的时间
     */
    private void onSetMessageTimeDisplay() {
        synchronized (mMessages) {
            //消息是按照时间戳从小到大的排序,需要从大到小遍历
            if (mMessages.size() == 0) return;
            for (int i = mMessages.size() - 1; i >= 0; i--) {
                Message message = mMessages.get(i);
                //处理最新消息
                if (null == mNewDisplayTimeMessage) {
                    mNewDisplayTimeMessage = message;
                    message.setDisplayTime(true);
                } else {
                    if (message.getTimestamp() - mNewDisplayTimeMessage.getTimestamp() >= DISPLAY_TIME_INTERVAL) {
                        mNewDisplayTimeMessage = message;
                        message.setDisplayTime(true);
                    }
                }
                //处理老消息
                if (null == mOldDisplayTimeMessage) {
                    mOldDisplayTimeMessage = message;
                    message.setDisplayTime(true);
                } else {
                    if (mOldDisplayTimeMessage.getTimestamp() - message.getTimestamp() >= DISPLAY_TIME_INTERVAL) {
                        mOldDisplayTimeMessage = message;
                        message.setDisplayTime(true);
                    }
                }
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout flMessageLayout;       //父布局

        ViewHolder(View view) {
            super(view);
            flMessageLayout = view.findViewById(R.id.fl_message_layout);
        }
    }

}
