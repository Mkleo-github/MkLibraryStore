package com.mkleo.chat.utils;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer {

    //是否正在播放
    private boolean isPlaying = false;
    //媒体播放器
    private MediaPlayer mMediaPlayer;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        return Provider.INSTANCE;
    }

    private static class Provider {
        final static AudioPlayer INSTANCE = new AudioPlayer();
    }

    /**
     * 开始播放音频
     *
     * @param path
     * @param listener
     */
    public void playAudio(String path, MediaPlayer.OnCompletionListener listener) {

        if (isPlaying) {   //如果正在播放
            stopAudio();
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        mMediaPlayer.setOnCompletionListener(listener);

        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {

        }
    }

    /**
     * 停止播放
     */
    public synchronized void stopAudio() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
        } catch (Exception e) {

        }
        mMediaPlayer = null;
        isPlaying = false;
    }


}
