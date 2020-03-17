package com.mkleo.encoder.base;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseEncoder implements IEncoder {

    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private MediaCodec.BufferInfo mBufferInfo;
    //线程池
    private ExecutorService mExecutor;
    private Callback mCallback;
    //是否正在编码
    private boolean isEncoding = false;

    private final Object mLock = new Object();

    protected BaseEncoder() {
    }

    private void init() {
        try {
            //初始化线程池
            mExecutor = Executors.newSingleThreadExecutor();
            mBufferInfo = new MediaCodec.BufferInfo();
            mMediaFormat = onCreateMediaFormat();
            mMediaCodec = onCreateMediaCodec(mMediaFormat);
            mMediaCodec.start();
            if (null != mCallback)
                mCallback.onStart();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Encoder init faild:" + e.toString());
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }


    @Override
    public synchronized void start() {
        if (isEncoding) return;  //不能被重复开始
        isEncoding = true;
        init();
        encode();
    }

    /**
     * 编码
     */
    private void encode() {
        //开始编码
        post(new Runnable() {
            @Override
            public void run() {
                int outputIndex = mMediaCodec.dequeueOutputBuffer(
                        mBufferInfo, 0
                );

                if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    onOutputFormatChanged();
                } else {
                    //数据有可能包含多帧
                    while (outputIndex >= 0) {
                        ByteBuffer outputBuffer = mMediaCodec.getOutputBuffers()[outputIndex];
//                        //设置演示时间
                        mBufferInfo.presentationTimeUs = getPresentationTimeUs();
                        //回调编码
                        onFrameCoding(outputBuffer, mBufferInfo);
                        //不需要渲染
                        mMediaCodec.releaseOutputBuffer(outputIndex, false);

                        outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
                    }
                }

                if (isEncoding) {
                    //循环
                    post(this);
                } else {
                    release();
                }
            }
        });
    }

    private void post(Runnable runnable) {
        if (isEncoding && null != mExecutor)
            mExecutor.execute(runnable);
    }

    @Override
    public synchronized void stop() {
        synchronized (mLock) {
            if (isEncoding) {
                isEncoding = false;
            }
        }
    }

    private void release() {
        synchronized (mLock) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
            mMediaFormat = null;
            mBufferInfo = null;
            if (null != mCallback)
                mCallback.onStop();
            mCallback = null;
        }
    }

    /**
     * 获取演示时间
     *
     * @return
     */
    private long getPresentationTimeUs() {
        return System.nanoTime() / 1000;
    }

    protected abstract MediaFormat onCreateMediaFormat();

    protected abstract MediaCodec onCreateMediaCodec(MediaFormat format) throws IOException;

    protected MediaCodec getMediaCodec() {
        return mMediaCodec;
    }

    protected boolean isEncoding() {
        return isEncoding;
    }

    /**
     * 获取线程同步锁
     *
     * @return
     */
    public Object getSyncLock() {
        return mLock;
    }

    protected abstract void onFrameCoding(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo);


    protected void onOutputFormatChanged() {
    }


}
