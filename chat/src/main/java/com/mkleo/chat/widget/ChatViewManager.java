package com.mkleo.chat.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mkleo.chat.bean.User;
import com.mkleo.chat.utils.AudioPlayer;
import com.mkleo.chat.widget.record.RecordButton;

@SuppressLint("ClickableViewAccessibility")
public class ChatViewManager implements
        View.OnClickListener,
        View.OnTouchListener,
        SwipeRefreshLayout.OnRefreshListener,
        TextWatcher,
        RecordButton.RecordCallback {

    //holder
    private ChatHolder mHolder;
    //自己
    private User mMine;
    //事件管理
    private IEventManager mEventManager;
    //控件管理
    private IWidgetManager mWidgetManager;

    public ChatViewManager(User mine, ChatHolder holder) {
        this.mMine = mine;
        this.mHolder = holder;
    }

    /* 创建管理 */
    public ChatViewManager build() {
        mEventManager = new ChatEventManager();
        mWidgetManager = new ChatWidgetManager();

        mWidgetManager.addKeyboardLisenter(mHolder);
        mHolder.mSwipeLayout.setOnRefreshListener(this);
        mHolder.mMessageView.setOnTouchListener(this);

        mHolder.mRecord_Keyboard.setOnClickListener(this);
        mHolder.mEmoji_Keyboard.setOnClickListener(this);
        mHolder.mSendButton.setOnClickListener(this);
        mHolder.mMoreButton.setOnClickListener(this);
        mHolder.mInput.setClickable(true);
        mHolder.mInput.setOnTouchListener(this);

        mHolder.mInput.addTextChangedListener(this);
        mHolder.mRecordButton.setRecordCallback(this);
        mHolder.mRecordButton.setRecordPath(mHolder.mChatLayout.getOptions().getAudioPath());
        return this;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == mHolder.mRecord_Keyboard.getId()) {

            if (ContextCompat.checkSelfPermission(mHolder.mChatLayout.getContext().getApplicationContext(),
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(mHolder.mChatLayout.getContext(), "请先获取录音权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mWidgetManager.onRecordOrKeyboardClick(mHolder);

        } else if (id == mHolder.mEmoji_Keyboard.getId()) {

            mWidgetManager.checkInput(mHolder);
            mWidgetManager.onEmojiOrKeyboardClick(mHolder);

        } else if (id == mHolder.mRecordButton.getId()) {

            mWidgetManager.onRecordButtonClick(mHolder);

        } else if (id == mHolder.mSendButton.getId()) {

            if (mHolder.mInput.getText().toString().equals("")) {
                Toast.makeText(mHolder.mChatLayout.getContext(), "消息不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            mEventManager.onTextSend(mHolder.mChatLayout, mMine, mHolder.mInput.getText().toString());
            mWidgetManager.onSendButtonClick(mHolder);

        } else if (id == mHolder.mMoreButton.getId()) {

            mWidgetManager.onMoreButtonClick(mHolder);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == mHolder.mMessageView.getId())
                mWidgetManager.onMessageViewClick(mHolder);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (v.getId() == mHolder.mInput.getId())
                mWidgetManager.onInputViewClick(mHolder);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        mEventManager.onSwipeRefresh(mHolder.mChatLayout, mHolder.mSwipeLayout);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mWidgetManager.checkInput(mHolder);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onRecordCompleted(String path) {
        mEventManager.onRecordSend(mHolder.mChatLayout, mMine, path);
    }

    /**
     * 销毁
     */
    public void destroy() {
        mMine = null;
        mEventManager = null;
        mHolder = null;
        mWidgetManager = null;
        AudioPlayer.getInstance().stopAudio();
    }

    public interface IEventManager {

        void onSwipeRefresh(ChatLayout chatLayout, SwipeRefreshLayout swipeRefreshLayout);

        void onTextSend(ChatLayout chatLayout, User mine, String text);

        void onPictureSend(ChatLayout chatLayout, User mine, String path);

        void onVideoSend(ChatLayout chatLayout, User mine, String path);

        void onRecordSend(ChatLayout chatLayout, User mine, String path);
    }

    public interface IWidgetManager {

        void addKeyboardLisenter(ChatHolder holder);

        void onMessageViewClick(ChatHolder holder);

        void onInputViewClick(ChatHolder holder);

        void checkInput(ChatHolder holder);

        void onRecordOrKeyboardClick(ChatHolder holder);

        void onEmojiOrKeyboardClick(ChatHolder holder);

        void onRecordButtonClick(ChatHolder holder);

        void onSendButtonClick(ChatHolder holder);

        void onMoreButtonClick(ChatHolder holder);
    }
}
