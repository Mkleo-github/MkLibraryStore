package com.mkleo.tcp;

/**
 * @说明:
 * @作者: Wang HengJin
 * @日期: 2018/12/7 10:32 星期五
 */
public interface IClient<T extends Sender> {

    void connect();

    void close();

    T send();

    /**
     * 内部的实现
     */
    abstract class Internal<T extends Sender> implements IClient<T> {
        protected abstract void sendMessage(String text);
    }


    interface OnClientListener {

        void onConnect();

        void onFaild(Throwable t);

        void onClosed();
    }
}
