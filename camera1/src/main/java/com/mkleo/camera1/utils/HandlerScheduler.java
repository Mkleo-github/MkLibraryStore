package com.mkleo.camera1.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;

public class HandlerScheduler {


    private HandlerThread mIoThread;
    private Handler mIoHandler;
    private Handler mUiHanlder;
    private String mName;

    public HandlerScheduler(String name) {
        this.mName = name;
    }


    public void prepare() {
        mIoThread = new HandlerThread(mName);
        mIoThread.start();
        mIoHandler = new Handler(mIoThread.getLooper());
        mUiHanlder = new Handler(Looper.getMainLooper());
    }


    /**
     * ui线程
     *
     * @param runnable
     */
    public void mainThread(@NonNull Runnable runnable) {
        if (isAvailable()) mUiHanlder.post(runnable);
    }

    /**
     * 后台线程
     *
     * @param runnable
     */
    public void ioThread(@NonNull Runnable runnable) {
        if (isAvailable()) mIoHandler.post(runnable);
    }


    /**
     * 是否可用
     *
     * @return
     */
    private synchronized boolean isAvailable() {
        return mIoHandler != null &&
                mIoThread != null &&
                mUiHanlder != null;
    }

    /**
     * 销毁线程
     */
    public synchronized void destroy() {
        if (null != mIoThread) {
            mIoThread.quitSafely();
            mIoThread = null;
        }
        if (null != mIoHandler) {
            mIoHandler.removeCallbacksAndMessages(null);
            mIoHandler = null;
        }
        if (null != mUiHanlder) {
            mUiHanlder.removeCallbacksAndMessages(null);
            mUiHanlder = null;
        }
    }

}
