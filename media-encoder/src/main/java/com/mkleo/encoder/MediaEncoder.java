package com.mkleo.encoder;

import android.view.Surface;

import com.mkleo.encoder.base.IEncoder;


public class MediaEncoder {
    public interface Callback {
        void onStart();

        void onStop(String path);
    }

    public static class Config {
        //文件夹路径
        String folderPath;
        //文件名称
        String fileName;
        //是否开启音频
        boolean isAudioEnable = true;
        //视频宽度
        int width = 720;
        //视频高度
        int height = 1280;
        //视频比特率 argb
        int videoBitRate = width * height * 4;
        //视频帧率
        int videoFps = 20;
        //视频关键帧
        int videoIFrameRate = 1;
        //音频采样率
        int audioSampleRate = 44100;
        //音频通道数
        int audioChannelCount = 2;
        //音频比特率 fm96k
        int audioBitRate = 96 * 1000;

        public Config(String folderPath, String fileName) {
            this.folderPath = folderPath;
            this.fileName = fileName;
        }

        public Config setAudioEnable(boolean audioEnable) {
            isAudioEnable = audioEnable;
            return this;
        }

        public Config setWidth(int width) {
            this.width = width;
            return this;
        }

        public Config setHeight(int height) {
            this.height = height;
            return this;
        }

        public Config setVideoBitRate(int videoBitRate) {
            this.videoBitRate = videoBitRate;
            return this;
        }

        public Config setVideoFps(int videoFps) {
            this.videoFps = videoFps;
            return this;
        }

        public Config setVideoIFrameRate(int videoIFrameRate) {
            this.videoIFrameRate = videoIFrameRate;
            return this;
        }

        public Config setAudioSampleRate(int audioSampleRate) {
            this.audioSampleRate = audioSampleRate;
            return this;
        }

        public Config setAudioChannelCount(int audioChannelCount) {
            this.audioChannelCount = audioChannelCount;
            return this;
        }

        public Config setAudioBitRate(int audioBitRate) {
            this.audioBitRate = audioBitRate;
            return this;
        }
    }

    private Config mConfig;
    //视频编码器
    private VideoEncoder mVideoEncoder;
    //音频编码器
    private AudioEncoder mAudioEncoder;
    //混合器
    private Muxer mMuxer;
    //回调
    private Callback mCallback;
    //需要加载的编码数量
    private int mSetupNumber = 0;
    //当前加载的编码数量
    private int mCurrentSetup = 0;
    //是否准备完毕
    private boolean isPrepare;

    public MediaEncoder(Config config) {
        this.mConfig = config;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public synchronized void start() {
        if (!isPrepare) {
            prepare();
            if (null != mVideoEncoder)
                mVideoEncoder.start();
            if (null != mAudioEncoder)
                mAudioEncoder.start();
            isPrepare = true;
        }
    }

    public synchronized void stop() {
        if (isPrepare) {
            if (null != mVideoEncoder)
                mVideoEncoder.stop();
            if (null != mAudioEncoder)
                mAudioEncoder.stop();
        }
    }

    public Surface getVideoSurface() {
        return mVideoEncoder.getVideoSurface();
    }

    public void putPcmData(final byte[] data, final int size) {
        if (null != mAudioEncoder)
            mAudioEncoder.putPcmData(data, size);
    }


    private void prepare() {
        mCurrentSetup = 0;
        mSetupNumber = mConfig.isAudioEnable ? 2 : 1;
        mMuxer = new Muxer(
                new Muxer.Config(mConfig.folderPath, mConfig.fileName)
                        .setAudioEnable(mConfig.isAudioEnable)
        );
        //加载视频编码器
        setupVideo();
        if (mConfig.isAudioEnable) {
            //加载音频播放器
            setupAudio();
        }
    }

    private void setupVideo() {
        mVideoEncoder = new VideoEncoder(
                new VideoEncoder.Config()
                        .setBitRate(mConfig.videoBitRate)
                        .setFps(mConfig.videoFps)
                        .setHeight(mConfig.height)
                        .setWidth(mConfig.width)
                        .setiFrameRate(mConfig.videoIFrameRate),
                mMuxer);
        mVideoEncoder.setCallback(new IEncoder.Callback() {
            @Override
            public void onStart() {
                onEncoderStart();
            }

            @Override
            public void onStop() {
                onEncoderStop();
            }
        });
    }


    private void setupAudio() {
        mAudioEncoder = new AudioEncoder(
                new AudioEncoder.Config()
                        .setBitRate(mConfig.audioBitRate)
                        .setChannelCount(mConfig.audioChannelCount)
                        .setSampleRate(mConfig.audioSampleRate),
                mMuxer);
        mAudioEncoder.setCallback(new IEncoder.Callback() {
            @Override
            public void onStart() {
                onEncoderStart();
            }

            @Override
            public void onStop() {
                onEncoderStop();
            }
        });
    }

    private synchronized void onEncoderStart() {
        mCurrentSetup++;
        if (mCurrentSetup == mSetupNumber) {
            if (null != mCallback)
                mCallback.onStart();
        }
    }

    private synchronized void onEncoderStop() {
        mCurrentSetup--;
        if (mCurrentSetup == 0) {
            if (null != mCallback)
                mCallback.onStop(mMuxer.getPath());
            release();
        }
    }

    private void release() {
        if (null != mVideoEncoder) {
            mVideoEncoder.setCallback(null);
            mVideoEncoder = null;
        }
        if (null != mAudioEncoder) {
            mAudioEncoder.setCallback(null);
            mAudioEncoder = null;
        }
        mMuxer = null;
        isPrepare = false;
    }

}
