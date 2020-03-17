package com.mkleo.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.mkleo.encoder.base.IMuxer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 编码混合器
 */
final class Muxer implements IMuxer {

    static class Config {

        //是否开启音频
        boolean isAudioEnable = true;
        //文件夹路径
        String folderPath;
        //文件名称
        String fileName;

        Config(String folderPath, String fileName) {
            this.folderPath = folderPath;
            this.fileName = fileName;
        }

        public Config setAudioEnable(boolean audioEnable) {
            isAudioEnable = audioEnable;
            return this;
        }
    }


    //输出格式
    private static final int OUT_PUT_FORMAT = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;
    //混合器
    private MediaMuxer mMediaMuxer;
    //当前加入的轨道数
    private int mTrackCounts = 0;
    //是否可用
    private volatile boolean isMuxerAvailable = false;
    //同步锁
    private final Object mLock = new Object();

    private Config mConfig;

    private String mPath;

    Muxer(Config config) {
        this.mConfig = config;
        this.mPath = mConfig.folderPath + File.separator + mConfig.fileName + ".mp4";
        init();
    }

    private void init() {
        //检测路径的合法性
        File file = new File(mPath);
        if (null == file.getParentFile()) throw new RuntimeException("Please check the file path");
        //检测文件是否存在
        if (file.exists()) file.delete();
        //创建路径
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            mMediaMuxer = new MediaMuxer(mPath, OUT_PUT_FORMAT);
        } catch (IOException e) {
            throw new RuntimeException("MeidiaMuxer create faild:" + e.toString());
        }
    }

    @Override
    public int addTrack(MediaFormat format) {
        mTrackCounts++;
        int track = mMediaMuxer.addTrack(format);
        if (mConfig.isAudioEnable) {
            if (mTrackCounts == 2) {
                start();
            }
        } else {
            start();
        }
        return track;
    }

    @Override
    public void removeTrack() {
        mTrackCounts--;
        if (mTrackCounts == 0) {
            stop();
        }
    }

    @Override
    public void writeSampleData(int track, ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        synchronized (mLock) {
            if (!isMuxerAvailable) return;
        }
        mMediaMuxer.writeSampleData(track, buffer, bufferInfo);
    }

    String getPath() {
        return mPath;
    }

    /**
     * 开始混合
     */
    private void start() {
        //开始混合
        mMediaMuxer.start();
        synchronized (mLock) {
            isMuxerAvailable = true;
        }
    }

    /**
     * 停止混合
     */
    private void stop() {
        //停止混合
        synchronized (mLock) {
            isMuxerAvailable = false;
        }
        try {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


}
