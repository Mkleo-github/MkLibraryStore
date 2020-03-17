package com.mkleo.glcamera;

import android.opengl.EGLContext;

import com.mkleo.encoder.MediaEncoder;
import com.mkleo.gles.GLRender;
import com.mkleo.gles.GLThread;
import com.mkleo.helper.AudioRecorder;
import com.mkleo.helper.MkLog;


/**
 * des:
 * by: Mk.leo
 * date: 2019/5/14
 */
public class GLVideoRecord {

    public interface RecordListener {
        void onStart();

        void onCompleted(String path);
    }

    public static class Config {
        private int width = 720;
        private int height = 1280;
        private int videoBitRate = width * height * 4;
        private int videoFps = 20;
        private int audioBitRate = 96 * 1000;
        private int audioSampleRate = 44100;
        private boolean isAudioEnable = true;
        private String folderPath;
        private String fileName;

        public Config(String folderPath, String fileName) {
            this.folderPath = folderPath;
            this.fileName = fileName;
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

        public Config setAudioBitRate(int audioBitRate) {
            this.audioBitRate = audioBitRate;
            return this;
        }

        public Config setAudioSampleRate(int audioSampleRate) {
            this.audioSampleRate = audioSampleRate;
            return this;
        }

        public Config setAudioEnable(boolean audioEnable) {
            isAudioEnable = audioEnable;
            return this;
        }

        public Config setFolderPath(String folderPath) {
            this.folderPath = folderPath;
            return this;
        }

        public Config setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
    }

    /* GL线程 */
    private GLThread mGLThread;
    /* 编码器 */
    private MediaEncoder mMediaEncoder;
    /* 音频录制(提供音频数据) */
    private AudioRecorder mAudioRecorder;


    /**
     * 开始录制
     *
     * @param config        录制配置
     * @param sharedContext 共享环境
     * @param glRender      需要录制的Render
     */
    public synchronized void startRecord(final Config config,
                                         final EGLContext sharedContext,
                                         GLRender glRender,
                                         final RecordListener listener) {
        if (null != mGLThread) return;
        mGLThread = new GLThread();
        mGLThread.start();
        //设置需要录制的render
        mGLThread.setRender(glRender);


        mMediaEncoder = new MediaEncoder(
                new MediaEncoder.Config(config.folderPath, config.fileName)
                        .setWidth(config.width)
                        .setHeight(config.height)
                        .setVideoBitRate(config.videoBitRate)
                        .setAudioBitRate(config.audioBitRate)
                        .setAudioSampleRate(config.audioSampleRate)
                        .setAudioEnable(config.isAudioEnable)
                        .setVideoFps(config.videoFps)
        );

        mMediaEncoder.setCallback(new MediaEncoder.Callback() {
            @Override
            public void onStart() {
                MkLog.print("开始编码");
                //启动GL线程
                mGLThread.create(mMediaEncoder.getVideoSurface(), sharedContext);
                mGLThread.change(config.width, config.height);
                if (null != listener)
                    listener.onStart();
            }

            @Override
            public void onStop(String path) {
                MkLog.print("停止编码:" + path);
                if (null != listener)
                    listener.onCompleted(path);
            }
        });
        //启动录音线程
        mAudioRecorder = new AudioRecorder(
                new AudioRecorder.Config()
                        .setAudioChannel(AudioRecorder.AudioChannel.CHANNEL_STEREO)
        );
        mAudioRecorder.setOnRecordListener(new AudioRecorder.OnRecordListener() {
            @Override
            public void onStart() {
                MkLog.print("AuidoRecorder开始运行");
            }

            @Override
            public void onRecording(byte[] data, int size) {
                if (null != mMediaEncoder)
                    mMediaEncoder.putPcmData(data, size);
            }

            @Override
            public void onStop() {
                MkLog.print("AuidoRecorder停止运行");
            }
        });
        //开始录音
        mAudioRecorder.start();
        //启动编码
        mMediaEncoder.start();
    }

    /**
     * 停止录制
     */
    public synchronized void stopRecord() {
        //停止录制
        if (null != mMediaEncoder) {
            mMediaEncoder.stop();
            mMediaEncoder = null;
        }
        if (null != mAudioRecorder) {
            mAudioRecorder.stop();
            mAudioRecorder = null;
        }
        if (null != mGLThread) {
            //停止GL线程
            mGLThread.quit();
            mGLThread = null;
        }
    }
}
