package com.mkleo.chat.widget;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.IContent;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.User;
import com.mkleo.chat.widget.base.IMessageContentAdpater;

import java.text.SimpleDateFormat;

public abstract class MessageContentAdpater<T extends IContent> implements IMessageContentAdpater<T> {

    protected static final String TAG = MessageContentAdpater.class.getSimpleName();
    private Callback mCallback;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    MessageContentAdpater() {
    }

    /**
     * 将布局加入
     *
     * @param messageLayout
     * @param message
     */
    @Override
    public void onBindMessage(final ViewGroup messageLayout, final Message<T> message) {
        if (message.getUser().getType() == User.Type.MINE) {            //自己
            onMineMessage(messageLayout, message);
        } else if (message.getUser().getType() == User.Type.OTHER) {    //其他用户
            onOtherMessage(messageLayout, message);
        } else if (message.getUser().getType() == User.Type.SYSTEM) {   //系统用户
            onSystemMessage(messageLayout, message);
        }
    }

    /**
     * 处理自己发送的消息
     *
     * @param messageLayout
     * @param message
     */
    private void onMineMessage(final ViewGroup messageLayout, final Message<T> message) {
        final View messageView = LayoutInflater.from(messageLayout.getContext()).inflate(R.layout.message_right, messageLayout, false);
        final ImageView ivAvatar = messageView.findViewById(R.id.iv_avatar);
        final FrameLayout flMessageContent = messageView.findViewById(R.id.fl_message_content);
        final ProgressBar pbSending = messageView.findViewById(R.id.pb_sending);
        final ImageView ivSendFaild = messageView.findViewById(R.id.iv_send_fail);
        final TextView tvUserName = messageView.findViewById(R.id.tv_user_name);
        final TextView tvDate = messageView.findViewById(R.id.tv_date);
        messageLayout.removeAllViews();
        messageLayout.addView(messageView);

        //设置消息状态
        this.updateViewByMessageStatus(message.getState(), pbSending, ivSendFaild);
        //把头像先加入
        int avatar = message.getUser().getAvatar();
        if (avatar > 0)
            ivAvatar.setBackgroundResource(avatar);
        //发送失败按钮
        ivSendFaild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setState(Message.State.PROCESSING);
                updateViewByMessageStatus(Message.State.PROCESSING, pbSending, ivSendFaild);
                if (null != mCallback)
                    mCallback.onResendClick(message);
            }
        });
        if (message.isDisplayTime()) {
            tvDate.setText(mDateFormat.format(message.getTimestamp()));
            tvDate.setVisibility(View.VISIBLE);
        } else {
            tvDate.setVisibility(View.GONE);
        }
        tvUserName.setText(message.getUser().getName());
        //创建消息内容
        View messageContentView = LayoutInflater.from(messageLayout.getContext()).inflate(setContentView(), flMessageContent, false);
        flMessageContent.removeAllViews();
        flMessageContent.addView(messageContentView);
        //
        onContentViewCreated(messageContentView, message);
    }

    /**
     * 处理其他人的消息
     *
     * @param messageLayout
     * @param message
     */
    private void onOtherMessage(ViewGroup messageLayout, final Message<T> message) {
        View messageView = LayoutInflater.from(messageLayout.getContext()).inflate(R.layout.message_left, messageLayout, false);
        ImageView ivAvatar = messageView.findViewById(R.id.iv_avatar);
        FrameLayout flMessageContent = messageView.findViewById(R.id.fl_message_content);
        TextView tvUserName = messageView.findViewById(R.id.tv_user_name);
        TextView tvDate = messageView.findViewById(R.id.tv_date);
        messageLayout.removeAllViews();
        messageLayout.addView(messageView);
        //把头像先加入
        int avatar = message.getUser().getAvatar();
        if (avatar > 0)
            ivAvatar.setBackgroundResource(avatar);
        if (message.isDisplayTime()) {
            tvDate.setText(mDateFormat.format(message.getTimestamp()));
            tvDate.setVisibility(View.VISIBLE);
        } else {
            tvDate.setVisibility(View.GONE);
        }
        tvUserName.setText(message.getUser().getName());
        //创建消息内容
        View messageContentView = LayoutInflater.from(messageLayout.getContext()).inflate(setContentView(), flMessageContent, false);
        flMessageContent.removeAllViews();
        flMessageContent.addView(messageContentView);
        //
        onContentViewCreated(messageContentView, message);
    }

    /**
     * 处理系统消息
     *
     * @param messageLayout
     * @param message
     */
    private void onSystemMessage(ViewGroup messageLayout, final Message<T> message) {
        View messageView = LayoutInflater.from(messageLayout.getContext()).inflate(R.layout.message_system, messageLayout, false);
        messageLayout.removeAllViews();
        messageLayout.addView(messageView);
        FrameLayout flMessageContent = messageView.findViewById(R.id.fl_message_content);
        TextView tvDate = messageView.findViewById(R.id.tv_date);
        if (message.isDisplayTime()) {
            tvDate.setText(mDateFormat.format(message.getTimestamp()));
            tvDate.setVisibility(View.VISIBLE);
        } else {
            tvDate.setVisibility(View.GONE);
        }
        //创建消息内容
        View messageContentView = LayoutInflater.from(messageLayout.getContext()).inflate(setContentView(), flMessageContent, false);
        flMessageContent.removeAllViews();
        flMessageContent.addView(messageContentView);
        //
        onContentViewCreated(messageContentView, message);
    }

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateViewByMessageStatus(@Message.State final int status, final ProgressBar pbSending, final ImageView ivSendFaild) {
        if (null != pbSending && null != ivSendFaild) {
            pbSending.post(new Runnable() {
                @Override
                public void run() {
                    if (status == Message.State.PROCESSING) {                 //正在发送
                        pbSending.setVisibility(View.VISIBLE);
                        ivSendFaild.setVisibility(View.INVISIBLE);
                    } else if (status == Message.State.FAILD) {      //发送失败
                        pbSending.setVisibility(View.INVISIBLE);
                        ivSendFaild.setVisibility(View.VISIBLE);
                    } else if (status == Message.State.SUCCESS) {   //发送成功
                        pbSending.setVisibility(View.INVISIBLE);
                        ivSendFaild.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    /**
     * 设置内容图形
     *
     * @return
     */
    protected abstract int setContentView();

    /**
     * 内容布局创建完成
     *
     * @param view
     * @param message
     */
    protected abstract void onContentViewCreated(View view, Message<T> message);

}
