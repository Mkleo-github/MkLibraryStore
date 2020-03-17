package com.mkleo.chat.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mkleo.chat.R;
import com.mkleo.chat.utils.KeyboardUtil;
import com.mkleo.chat.utils.WindowUtil;

/**
 * 控件逻辑管理
 */
public class ChatWidgetManager implements ChatViewManager.IWidgetManager {

    private KeyboardUtil mKeyboardUtil;
    private boolean isRecord = true;
    private boolean isEmoji = true;

    @Override
    public void addKeyboardLisenter(final ChatHolder holder) {
        final int defaultHeight = (int) (WindowUtil.getWindowWidth(holder.mChatLayout.getContext().getApplicationContext()) * 0.8);
        mKeyboardUtil = new KeyboardUtil(holder.mChatLayout, defaultHeight);
        mKeyboardUtil.setKeyBoardListener(new KeyboardUtil.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isKeyboardShow, int keyboardHeight) {
                if (!isKeyboardShow) return;
                hideToolbar(holder);//如果键盘显示 toolbar一定要隐藏
                checkInput(holder);//检测输入框
                holder.mInput.requestFocus();//获取焦点
                unlockLauyout(holder);
                holder.mMessageView.scrollToBottom();
            }
        });
    }

    @Override
    public void onMessageViewClick(ChatHolder holder) {
        if (holder.mRecordButton.getVisibility() == View.VISIBLE) return;//当是录音状态该操作无效
        holder.mInput.clearFocus(); //取消焦点
        setEmojiButtonShow(holder, true);//还原图标
        hideToolbar(holder);//隐藏拓展栏
        unlockLauyout(holder);//解锁布局
        mKeyboardUtil.hideKeyboard(holder.mInput); //隐藏键盘
    }

    @Override
    public void onInputViewClick(ChatHolder holder) {
        mKeyboardUtil.showKeyboard(holder.mInput);//显示键盘
        holder.mInput.requestFocus(); //请求焦点
        hideToolbar(holder); //隐藏状态栏
        setEmojiButtonShow(holder, true);//还原图标
    }

    @Override
    public void checkInput(ChatHolder holder) {
        //如果更多功能被禁用，不执行下面逻辑
        if (holder.mMoreButton.getVisibility() == View.GONE) return;

        if (holder.mInput.getText().toString().length() > 0) {
            holder.mSendButton.setVisibility(View.VISIBLE);
            holder.mMoreButton.setVisibility(View.INVISIBLE);
        } else {
            holder.mSendButton.setVisibility(View.INVISIBLE);
            holder.mMoreButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecordOrKeyboardClick(ChatHolder holder) {
        if (isRecord) {
            holder.mInput.clearFocus();//失去焦点
            setEmojiButtonShow(holder, true);//还原图标
            setRecordButtonShow(holder, false);//改变当前状态
            holder.mRecordButton.setVisibility(View.VISIBLE);//显示录音按钮
            holder.mInput.setVisibility(View.GONE);//隐藏输入框
            if (holder.mMoreButton.getVisibility() != View.GONE) {
                holder.mMoreButton.setVisibility(View.VISIBLE);
                holder.mSendButton.setVisibility(View.INVISIBLE);
            }
            mKeyboardUtil.hideKeyboard(holder.mInput);//隐藏键盘
            hideToolbar(holder);//隐藏工具栏
            unlockLauyout(holder);
        } else {
            checkInput(holder);
            holder.mRecordButton.setVisibility(View.GONE);//隐藏录音按钮
            holder.mInput.setVisibility(View.VISIBLE);//显示输入框
            clickKeyboardIcon(holder);//点击键盘图标
            if (holder.mMoreButton.getVisibility() == View.GONE) {
                holder.mSendButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onEmojiOrKeyboardClick(ChatHolder holder) {
        if (isEmoji) {
            holder.mEmojiLayout.setVisibility(View.VISIBLE);
            holder.mMoreLayout.setVisibility(View.INVISIBLE);
            mKeyboardUtil.hideKeyboard(holder.mInput);
            holder.mInput.setVisibility(View.VISIBLE);
            holder.mRecordButton.setVisibility(View.GONE);
            holder.mInput.clearFocus();//丢失焦点
            setRecordButtonShow(holder, true);
            setEmojiButtonShow(holder, false);
            showToolbar(holder);
        } else {
            clickKeyboardIcon(holder);//点击键盘图标
        }
    }

    @Override
    public void onRecordButtonClick(ChatHolder holder) {
        //TODO
    }

    @Override
    public void onSendButtonClick(ChatHolder holder) {
        holder.mInput.setText("");
    }

    @Override
    public void onMoreButtonClick(ChatHolder holder) {
        holder.mEmojiLayout.setVisibility(View.INVISIBLE);
        holder.mMoreLayout.setVisibility(View.VISIBLE);
        showToolbar(holder);
        mKeyboardUtil.hideKeyboard(holder.mInput);
        holder.mInput.clearFocus();
        holder.mInput.setVisibility(View.VISIBLE);
        holder.mRecordButton.setVisibility(View.GONE);
        setEmojiButtonShow(holder, true);
        setRecordButtonShow(holder, true);
    }

    /**
     * 显示底部工具栏
     *
     * @param holder
     */
    private void showToolbar(final ChatHolder holder) {
        lockLayout(holder);//锁定布局
        int toolbarHeight = mKeyboardUtil.getKeyboardHeight();
        //显示工具栏 就一定要隐藏键盘
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarHeight);

        holder.mToolbar.setLayoutParams(layoutParams);
        holder.mToolbar.setVisibility(View.VISIBLE);
        holder.mToolbar.post(new Runnable() {
            @Override
            public void run() {
                holder.mMessageView.scrollToBottom();
            }
        });
    }

    /**
     * 隐藏底部工具栏
     *
     * @param holder
     */
    private void hideToolbar(final ChatHolder holder) {
        if (holder.mToolbar.getVisibility() == View.GONE) return;
        holder.mToolbar.setVisibility(View.GONE);
    }

    /**
     * 锁定布局
     *
     * @param holder
     */
    private void lockLayout(ChatHolder holder) {
        int height = mKeyboardUtil.getContentHeight() - mKeyboardUtil.getKeyboardHeight() - holder.mFooter.getHeight() - 2;//分割线高度
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        layoutParams.weight = 0.0f;
        holder.mSwipeLayout.setLayoutParams(layoutParams);
    }

    /**
     * 解锁布局
     *
     * @param holder
     */
    private void unlockLauyout(ChatHolder holder) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1.0f;
        holder.mSwipeLayout.setLayoutParams(layoutParams);
    }

    /**
     * 点击键盘图标
     *
     * @param holder
     */
    private void clickKeyboardIcon(ChatHolder holder) {
        holder.mInput.requestFocus();//请求焦点
        mKeyboardUtil.showKeyboard(holder.mInput);//弹出键盘
        setRecordButtonShow(holder, true);//还原图标
        setEmojiButtonShow(holder, true);
        hideToolbar(holder);//隐藏状态栏
    }

    /**
     * 语音录制图标
     *
     * @param holder
     * @param isRecord
     */
    private void setRecordButtonShow(ChatHolder holder, boolean isRecord) {
        if (holder.mRecord_Keyboard.getVisibility() == View.GONE) return;
        this.isRecord = isRecord;
        if (this.isRecord)
            holder.mRecord_Keyboard.setBackgroundResource(R.mipmap.icon_record_normal);
        else holder.mRecord_Keyboard.setBackgroundResource(R.mipmap.icon_keyboard_normal);
    }

    /**
     * 表情按钮显示
     *
     * @param holder
     * @param isEmoji
     */
    private void setEmojiButtonShow(ChatHolder holder, boolean isEmoji) {
        if (holder.mEmoji_Keyboard.getVisibility() == View.GONE) return;
        this.isEmoji = isEmoji;
        if (this.isEmoji) holder.mEmoji_Keyboard.setBackgroundResource(R.mipmap.icon_emoji);
        else holder.mEmoji_Keyboard.setBackgroundResource(R.mipmap.icon_keyboard_normal);
    }
}
