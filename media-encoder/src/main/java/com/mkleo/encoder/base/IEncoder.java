package com.mkleo.encoder.base;

/**
 * 编码器
 */
public interface IEncoder {

    interface Callback {
        void onStart();

        void onStop();
    }

    /**
     * 设置回调
     *
     * @param callback
     */
    void setCallback(Callback callback);

    /**
     * 编码帧
     */
    void start();

    /**
     * 停止编码
     */
    void stop();

}
