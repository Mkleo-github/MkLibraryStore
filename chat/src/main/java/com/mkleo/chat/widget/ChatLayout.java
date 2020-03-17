package com.mkleo.chat.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.User;
import com.mkleo.chat.robot.IRobot;
import com.mkleo.chat.widget.emoji.EmojiLayoutAdapter;
import com.mkleo.chat.widget.more.MoreLayoutAdapter;

/**
 * 聊天的布局
 */
public class ChatLayout extends FrameLayout {

    private Options mOptions;
    private ChatHolder mHolder;
    private ChatViewManager mChatViewManager;
    private View mContentView;

    public ChatLayout(@NonNull Context context) {
        super(context);
    }

    public ChatLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mChatViewManager) {
            mChatViewManager.destroy();
            mChatViewManager = null;
        }
    }

    protected Options getOptions() {
        return mOptions;
    }

    /**
     * 获取内容布局
     *
     * @return
     */
    protected View getContentView() {
        return mContentView;
    }

    protected void onCreateLayout() {
        //创建当前View
        mContentView = View.inflate(getContext(), R.layout.chat_layout, null);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(mContentView);
        //创建布局管理器
        mHolder = new ChatHolder(this);
        //设置聊天布局
        mHolder.mMessageView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHolder.mMessageView.setAdapter(new MessageAdapter().setCallback(new MessageAdapter.Callback() {
            @Override
            public void onMessageResend(Message message) {
                if (null != mOptions.getChatListener())
                    mOptions.getChatListener().onMessageSend(message, ChatLayout.this);
            }

            @Override
            public void onMessageChanged(Message message) {
                mHolder.mMessageView.updateMessage(message);
            }
        }));

        if (!mOptions.isEmojiEnable()) { //如果关闭表情功能将隐藏该图标
            mHolder.mEmoji_Keyboard.setVisibility(GONE);
        }

        if (!mOptions.isMoreEnable()) {  //关闭更多功能
            mHolder.mMoreButton.setVisibility(GONE);
            mHolder.mSendButton.setVisibility(VISIBLE);
        }

        if (!mOptions.isRecordEnable()) { //关闭录音功能
            mHolder.mRecord_Keyboard.setVisibility(GONE);
        }

        //初始化表情布局
        if (null != mOptions.getEmojiAdapter()) {
            mOptions.getEmojiAdapter().build(mHolder.mEmojiLayout);
        }

        //初始化更多布局
        if (null != mOptions.getMoreAdapter()) {
            mOptions.getMoreAdapter().build(mHolder.mMoreLayout);
        }

        //创建图形逻辑管理器
        mChatViewManager = new ChatViewManager(mOptions.getMine(), mHolder).build();
        //当布局创建完成,机器人如果被设置,就发送问候语
        if (null != mOptions.getRobot())
            mOptions.getRobot().greetings(this);
    }

    /**
     * 设置配置,不设置将无法显示
     *
     * @param options
     */
    public void setupLayout(Options options) {
        this.mOptions = options;
        onCreateLayout();
    }


    /**
     * 发送消息给聊天界面
     *
     * @param message
     */
    public void sendMessage(final Message message) {
        if (null != message && null != message.getUser()) {
            mHolder.mMessageView.sendMessage(message);//发送消息到内容布局
            if (message.getUser().getType() == User.Type.MINE) {
                if (mOptions.getRobot() != null)
                    mOptions.getRobot().autoReply(message, this);//自动回复,只有是当前用户发送的消息才会发送
                if (mOptions.getChatListener() != null)
                    mOptions.getChatListener().onMessageSend(message, ChatLayout.this);
            }
        }
    }

    /**
     * 访问messageView
     *
     * @return
     */
    public MessageView getMessageView() {
        return mHolder.mMessageView;
    }

    /**
     * 停止刷新
     */
    public void setRefreshing(final boolean enable) {
        if (null != mHolder) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHolder.mSwipeLayout.setRefreshing(enable);
                }
            });
        }
    }

    /**
     * 当消息以Mine的用户发送会被该方法拦截
     */
    public interface OnChatListener {

        void onSwipeRefresh(ChatLayout chatLayout);

        void onMessageSend(Message<?> message, ChatLayout chatLayout);
    }

    public static class Options {
        //自身用户
        private final User mine;
        //机器人
        private IRobot robot = null;
        //当mine用户操作会通过该监听返回
        private OnChatListener chatListener = null;
        //表情
        private EmojiLayoutAdapter emojiAdapter = null;
        //更多
        private MoreLayoutAdapter moreAdapter = null;
        //录音路径
        private String audioPath = Constants.DefaultPath.PATH_AUDIO;
        //表情功能开关
        private boolean emojiEnable = true;
        //更多功能开关
        private boolean moreEnable = true;
        //录音功能开关
        private boolean recordEnable = true;

        public Options(User mine) {
            if (mine == null)
                throw new NullPointerException("用户不能为null");
            if (mine.getType() != User.Type.MINE)
                throw new RuntimeException("您必须添加一个\"当前\"用户 (User.Type.MINE)");
            this.mine = mine;
        }

        public Options setRobot(IRobot robot) {
            this.robot = robot;
            return this;
        }

        public Options setChatListener(OnChatListener chatListener) {
            this.chatListener = chatListener;
            return this;
        }

        public Options setEmojiAdapter(EmojiLayoutAdapter emojiAdapter) {
            this.emojiAdapter = emojiAdapter;
            return this;
        }

        public Options setMoreAdapter(MoreLayoutAdapter moreAdapter) {
            this.moreAdapter = moreAdapter;
            return this;
        }

        public Options setAudioPath(String audioPath) {
            this.audioPath = audioPath;
            return this;
        }

        public Options setEmojiEnable(boolean emojiEnable) {
            this.emojiEnable = emojiEnable;
            return this;
        }

        public Options setMoreEnable(boolean moreEnable) {
            this.moreEnable = moreEnable;
            return this;
        }

        public Options setRecordEnable(boolean recordEnable) {
            this.recordEnable = recordEnable;
            return this;
        }

        public User getMine() {
            return mine;
        }

        public IRobot getRobot() {
            return robot;
        }

        public OnChatListener getChatListener() {
            return chatListener;
        }

        public EmojiLayoutAdapter getEmojiAdapter() {
            return emojiAdapter;
        }

        public MoreLayoutAdapter getMoreAdapter() {
            return moreAdapter;
        }

        public String getAudioPath() {
            return audioPath;
        }

        public boolean isEmojiEnable() {
            return emojiEnable;
        }

        public boolean isMoreEnable() {
            return moreEnable;
        }

        public boolean isRecordEnable() {
            return recordEnable;
        }
    }
}
