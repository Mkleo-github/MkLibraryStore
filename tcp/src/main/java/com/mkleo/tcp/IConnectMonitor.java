package com.mkleo.tcp;

/**
 * @说明: 监视器
 * @作者: Wang HengJin
 * @日期: 2018/12/7 10:29 星期五
 */
public interface IConnectMonitor {

    void start();

    void stop();

    void received();


    interface OnTimeoutListener {
        void onTimeout();
    }

}
