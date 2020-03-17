package com.mkleo.tcp;

import android.os.Handler;
import android.os.HandlerThread;


/**
 * @说明: 客户端连接监视器
 * @作者: Wang HengJin
 * @日期: 2018/12/7 10:35 星期五
 */
final class ConnectMonitor implements IConnectMonitor {

    private final String TAG = getClass().getSimpleName();
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private final int mTimeoutSec;
    private final OnTimeoutListener mOnTimeoutListener;

    ConnectMonitor(int timeoutSec, OnTimeoutListener onTimeoutListener) {
        //默认为30秒
        if (timeoutSec <= 0) timeoutSec = 30;
        this.mTimeoutSec = timeoutSec;
        this.mOnTimeoutListener = onTimeoutListener;
    }


    @Override
    public void start() {
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.postDelayed(mTimeoutRunnable, mTimeoutSec * 1000);
    }

    @Override
    public synchronized void received() {
        if (null == mHandler) return;
        //收到服务器响应,重置计时器
        mHandler.removeCallbacks(mTimeoutRunnable);
        mHandler.postDelayed(mTimeoutRunnable, mTimeoutSec * 1000);
    }

    @Override
    public void stop() {
        if (null != mHandler)
            mHandler.removeCallbacks(mTimeoutRunnable);
        if (null != mHandlerThread)
            mHandlerThread.quitSafely();
        mHandlerThread = null;
        mHandler = null;
    }


    /* 超时操作 */
    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            stop();
            //超时
            if (null != mOnTimeoutListener)
                mOnTimeoutListener.onTimeout();
        }
    };
}
