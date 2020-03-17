package com.mkleo.chat.widget;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mkleo.chat.R;
import com.mkleo.chat.widget.emoji.EmojiLayout;
import com.mkleo.chat.widget.more.MoreLayout;
import com.mkleo.chat.widget.record.RecordButton;

class ChatHolder {

    ChatLayout mChatLayout;
    /* 主布局 */
    SwipeRefreshLayout mSwipeLayout;
    MessageView mMessageView;
    FrameLayout mToolbar;
    LinearLayout mFooter;
    /* 工具栏 */
    Button mRecord_Keyboard;
    Button mEmoji_Keyboard;
    EditText mInput;
    RecordButton mRecordButton;
    Button mSendButton;
    Button mMoreButton;
    EmojiLayout mEmojiLayout;
    MoreLayout mMoreLayout;

    ChatHolder(ChatLayout chatLayout) {
        mChatLayout = chatLayout;
        mSwipeLayout = chatLayout.getContentView().findViewById(R.id.swipe_layout);
        mMessageView = chatLayout.getContentView().findViewById(R.id.message_view);
        mToolbar = chatLayout.getContentView().findViewById(R.id.toolbar);
        mFooter = chatLayout.getContentView().findViewById(R.id.footer);
        mRecord_Keyboard = chatLayout.getContentView().findViewById(R.id.btn_record_keyboard);
        mEmoji_Keyboard = chatLayout.getContentView().findViewById(R.id.btn_emoji_keyboard);
        mInput = chatLayout.getContentView().findViewById(R.id.et_input);
        mRecordButton = chatLayout.getContentView().findViewById(R.id.btn_record);
        mRecordButton.init(chatLayout.getContext());
        mSendButton = chatLayout.getContentView().findViewById(R.id.btn_send);
        mMoreButton = chatLayout.getContentView().findViewById(R.id.btn_more);
        mEmojiLayout = new EmojiLayout.Builder(chatLayout.getContext())
                .bindInput(mInput)
                .setViewGroup(mToolbar)
                .setRow(4)
                .setCol(6)
                .build();
        mMoreLayout = new MoreLayout.Builder(chatLayout.getContext())
                .setViewGroup(mToolbar)
                .setRow(2)
                .setCol(4)
                .build();
    }

}
