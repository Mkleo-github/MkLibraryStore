package com.mkleo.helper;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/7
 */
public class AudioRecorder {

    @IntDef({
            AudioChannel.CHANNEL_DEFAULT,
            AudioChannel.CHANNEL_LEFT,
            AudioChannel.CHANNEL_RIGHT,
            AudioChannel.CHANNEL_STEREO
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioChannel {
        /* 立体声 */
        int CHANNEL_STEREO = AudioFormat.CHANNEL_IN_STEREO;
        /* 默认 */
        int CHANNEL_DEFAULT = AudioFormat.CHANNEL_IN_DEFAULT;
        /* 左声道 */
        int CHANNEL_LEFT = AudioFormat.CHANNEL_IN_LEFT;
        /* 右声道 */
        int CHANNEL_RIGHT = AudioFormat.CHANNEL_IN_RIGHT;
    }

    @IntDef({
            AudioEncodeFormat.ENCODE_DEFAULT,
            AudioEncodeFormat.ENCODE_PCM_8BIT,
            AudioEncodeFormat.ENCODE_PCM_16BIT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioEncodeFormat {
        int ENCODE_DEFAULT = AudioFormat.ENCODING_DEFAULT;
        int ENCODE_PCM_8BIT = AudioFormat.ENCODING_PCM_8BIT;
        int ENCODE_PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;
    }


    public static class Config {
        /* 采样率 */
        private int sampleRate = 44100;
        /* 音频通道 */
        private int audioChannel = AudioChannel.CHANNEL_STEREO;
        /* 编码格式 */
        private int audioFormat = AudioEncodeFormat.ENCODE_PCM_16BIT;

        public Config setAudioChannel(@AudioChannel int audioChannel) {
            this.audioChannel = audioChannel;
            return this;
        }

        public Config setAudioFormat(@AudioEncodeFormat int audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public Config setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }
    }

    public interface OnRecordListener {
        void onStart();

        void onRecording(byte[] data, int size);

        void onStop();
    }


    private Config config;
    private OnRecordListener onRecordListener;
    private AudioThread audioThread;

    public AudioRecorder(Config config) {
        this.config = config;
    }

    public void start() {
        audioThread = new AudioThread(new WeakReference<AudioRecorder>(this));
        audioThread.start();
    }

    public void stop() {
        audioThread.quit();
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    private static class AudioThread extends Thread {

        private int bufferSize;
        private AudioRecord audioRecord;
        private int readSize;

        private WeakReference<AudioRecorder> audioRecorder;
        private boolean isQuit = false;

        private AudioThread(WeakReference<AudioRecorder> audioRecorder) {
            this.audioRecorder = audioRecorder;
        }


        @Override
        public void run() {
            super.run();
            //初始化参数
            this.init();
            audioRecord.startRecording();
            byte[] audioBuffer = new byte[bufferSize];

            if (null != audioRecorder.get() && null != audioRecorder.get().onRecordListener)
                audioRecorder.get().onRecordListener.onStart();

            while (!isQuit) {
                readSize = audioRecord.read(audioBuffer, 0, bufferSize);
                if (null != audioRecorder.get() && null != audioRecorder.get().onRecordListener)
                    audioRecorder.get().onRecordListener.onRecording(audioBuffer, readSize);
            }
            //线程结束销毁record
            this.release();
            if (null != audioRecorder.get() && null != audioRecorder.get().onRecordListener)
                audioRecorder.get().onRecordListener.onStop();
        }

        private void init() {

            bufferSize = AudioRecord.getMinBufferSize(
                    audioRecorder.get().config.sampleRate,
                    audioRecorder.get().config.audioChannel,
                    audioRecorder.get().config.audioFormat
            );

            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    audioRecorder.get().config.sampleRate,
                    AudioFormat.CHANNEL_IN_STEREO,
                    audioRecorder.get().config.audioFormat,
                    bufferSize
            );
        }

        private void release() {
            if (null != audioRecord) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }


        private void quit() {
            isQuit = true;
        }
    }

}
