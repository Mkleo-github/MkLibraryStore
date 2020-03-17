package com.mkleo.chat.widget;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.Record;
import com.mkleo.chat.bean.User;
import com.mkleo.chat.utils.AudioPlayer;
import com.mkleo.chat.utils.WindowUtil;

/**
 * @Description:
 * @author: Wang HengJin
 * @date: 2018/2/11 14:13 星期日
 */
public class RecordMessageAdpater extends MessageContentAdpater<Record> {
    //全局是否有正在播放
    private static boolean isPlaying = false;
    //记录的消息
    private static Message<Record> sNoteMessage;
    //正在播放的图标
    private static ImageView sPlayingIcon;

    public RecordMessageAdpater() {
    }

    @Override
    protected int setContentView() {
        return R.layout.message_content_record;
    }

    @Override
    protected void onContentViewCreated(View view, final Message<Record> message) {
        View recordView = null;
        if (message.getUser().getType() == User.Type.MINE) {        //自己
            recordView = LayoutInflater.from(view.getContext()).inflate(R.layout.view_record_right, (ViewGroup) view, false);
        } else if (message.getUser().getType() == User.Type.OTHER) {//其他用户
            recordView = LayoutInflater.from(view.getContext()).inflate(R.layout.view_record_left, (ViewGroup) view, false);
        }

        ((ViewGroup) view).removeAllViews();
        ((ViewGroup) view).addView(recordView);
        //时长
        final TextView tvLength = recordView.findViewById(R.id.tv_length);
        //消息体
        final RelativeLayout rlVoiceBody = recordView.findViewById(R.id.rl_voice_body);
        //声音图标
        final ImageView ivVoice = recordView.findViewById(R.id.iv_voice);

        int length = message.getContent().getLength();

        //将文件时长和宽度相互对应
        final int windowWidth = WindowUtil.getWindowWidth(view.getContext());
        final int minWidth = windowWidth / 8;
        final int maxWidth = windowWidth / 2;
        int itemWidth = maxWidth / 10;
        if (itemWidth == 0) itemWidth = 1;
        int bodyWidth = itemWidth * length;
        if (bodyWidth < minWidth) {
            bodyWidth = minWidth;
        } else if (bodyWidth > maxWidth) {
            bodyWidth = maxWidth;
        }
        tvLength.setText(length + "″");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bodyWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        rlVoiceBody.setLayoutParams(layoutParams);

        rlVoiceBody.setClickable(true);
        rlVoiceBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 音频处理
                if (isPlaying) {    //如果有正在播放
                    if (sNoteMessage == message) {              //如果是同一个message
                        //停止播放
                        AudioPlayer.getInstance().stopAudio();//停止
                        isPlaying = false;
                        stopAnim(message, ivVoice);
                    } else {
                        //播放
                        stopAnim(sNoteMessage, sPlayingIcon);
                        //重新记录
                        sNoteMessage = message;
                        sPlayingIcon = ivVoice;
                        playAnim(message, ivVoice);
                        AudioPlayer.getInstance().playAudio(message.getContent().getPath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                                stopAnim(message, ivVoice);
                            }
                        });
                    }
                } else {            //如果没有正在播放
                    //播放
                    isPlaying = true;
                    sNoteMessage = message;
                    sPlayingIcon = ivVoice;
                    playAnim(message, ivVoice);
                    AudioPlayer.getInstance().playAudio(message.getContent().getPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            isPlaying = false;
                            stopAnim(message, ivVoice);
                        }
                    });
                }
            }
        });
    }

    /**
     * 开始动画
     *
     * @param message
     * @param imageView
     */
    private void playAnim(final Message<Record> message, ImageView imageView) {
        if (imageView == null || message == null) return;
        if (message.getUser().getType() == User.Type.MINE) {        //右边
            AnimationDrawable drawable = (AnimationDrawable) imageView.getResources().getDrawable(R.drawable.anim_voice_right);
            imageView.setBackgroundDrawable(drawable);
            drawable.start();
        } else if (message.getUser().getType() == User.Type.OTHER) {//左边
            AnimationDrawable drawable = (AnimationDrawable) imageView.getResources().getDrawable(R.drawable.anim_voice_left);
            imageView.setBackgroundDrawable(drawable);
            drawable.start();
        }
    }

    /**
     * 停止动画
     *
     * @param message
     * @param imageView
     */
    private void stopAnim(final Message<Record> message, ImageView imageView) {
        if (imageView == null || message == null) return;
        if (message.getUser().getType() == User.Type.MINE) {        //右边
            imageView.setBackgroundResource(R.mipmap.icon_voice_right3);
        } else if (message.getUser().getType() == User.Type.OTHER) {//左边
            imageView.setBackgroundResource(R.mipmap.icon_voice_left3);
        }
    }

}
