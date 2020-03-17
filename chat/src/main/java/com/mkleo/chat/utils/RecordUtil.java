package com.mkleo.chat.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class RecordUtil {

    private RecordUtil() {
    }

    private static RecordUtil instance = null;

    public synchronized static RecordUtil getInstance() {
        if (instance == null)
            instance = new RecordUtil();
        return instance;
    }

    public static final int MAX_LENGTH = 1000 * 60 * 3;// 最大录音时长

    private String mFolderPath;//存放文件夹的路径
    private String mRecordName;//录音的名称
    private String mFilePath;//文件路径

    private MediaRecorder mMediaRecorder; //媒体播放器

    private long mNotesTime;//记录时间
    private boolean isRecording = false;//是否已经开始
    private Callback mCallback;
    private DecibelThread mDecibelThread;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * @param folderPath
     * @param recordName
     */
    public synchronized void start(String folderPath, String recordName) {
        if (!isRecording) {
            isRecording = true;
            this.mFolderPath = folderPath;
            this.mRecordName = recordName;
            this.mFilePath = folderPath + "/" + recordName;
            //生成文件夹
            File file = new File(mFolderPath);
            if (!file.exists())
                file.mkdirs();
            if (mMediaRecorder == null)
                mMediaRecorder = new MediaRecorder();

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //准备
            mMediaRecorder.setOutputFile(mFilePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                //流异常,终止录制
                release(true);
                if (mCallback != null)
                    mCallback.onFailed("录音异常终止了...");
                return;
            }
            mMediaRecorder.start();
            mNotesTime = System.currentTimeMillis();//记录开始时间
            //启动分贝线程
            mDecibelThread = new DecibelThread(mMediaRecorder, new DecibelThread.Callback() {
                @Override
                public void onDecibelRefresh(int db) {
                    if (null != mCallback)
                        mCallback.onRecording(db, System.currentTimeMillis() - mNotesTime);
                }
            });
            mDecibelThread.start();
            //开始录音
            if (null != mCallback)
                mCallback.onStart();
        }
    }

    /**
     * 停止录制
     */
    public synchronized void stop() {
        if (isRecording) {
            if (System.currentTimeMillis() - mNotesTime <= 1500) { //录制时间小于1.5秒,不保存
                release(true);
                if (null != mCallback)
                    mCallback.onFailed("录音时间太短");
                return;
            } else {//保存
                release(false);
            }
            if (null != mCallback)
                mCallback.onStop(mFolderPath, mRecordName);
        }
    }

    /**
     * 取消录制
     */
    public synchronized void cancel() {
        release(true);
        if (null != mCallback)
            mCallback.onCancel();
    }


    /**
     * 释放资源
     */
    private synchronized void release(boolean isDel) {
        if (null != mMediaRecorder) {
            try {
                if (null != mDecibelThread) {
                    mDecibelThread.quit();
                    mDecibelThread = null;
                }
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            } catch (Exception e) {
                //有时候stop会抛出异常
            }
            mMediaRecorder = null;
            isRecording = false;
            if (isDel) delete();
        }
    }

    /**
     * 删除文件
     */
    private void delete() {
        File file = new File(mFilePath);
        if (file.exists())//如果文件存在
            file.delete();//删除文件
    }


    /**
     * 获取分贝的线程
     */
    private static class DecibelThread extends Thread {

        interface Callback {
            void onDecibelRefresh(int db);
        }

        private boolean isQuit = false;
        private WeakReference<MediaRecorder> mMediaRecorderReference;
        private Callback mCallback;

        private DecibelThread(MediaRecorder recorder, Callback callback) {
            this.mMediaRecorderReference = new WeakReference<>(recorder);
            this.mCallback = callback;
        }

        @Override
        public void run() {

            while (!isQuit) {
                try {
                    if (mMediaRecorderReference.get() == null) return;
                    double ratio = (double) mMediaRecorderReference.get().getMaxAmplitude();
                    double db = 0;// 分贝
                    if (ratio > 1) db = 20 * Math.log10(ratio);
                    if (mCallback != null)
                        mCallback.onDecibelRefresh((int) db);
                    sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void quit() {
            isQuit = true;
            mCallback = null;
        }
    }

    public interface Callback {

        /**
         * 开始录制
         */
        void onStart();

        /**
         * 正在录制
         *
         * @param db     分贝
         * @param length 录制时长
         */
        void onRecording(int db, long length);

        /**
         * 停止
         *
         * @param folderPath 保存的文件夹路径
         * @param recordName 文件名称
         */
        void onStop(String folderPath, String recordName);

        //取消
        void onCancel();

        //失败
        void onFailed(String msg);
    }

}
