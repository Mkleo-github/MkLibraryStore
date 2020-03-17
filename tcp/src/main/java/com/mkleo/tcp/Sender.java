package com.mkleo.tcp;

import java.lang.ref.WeakReference;

/**
 * @说明:
 * @作者: Wang HengJin
 * @日期: 2018/12/7 11:53 星期五
 */
public abstract class Sender {

    private WeakReference<IClient.Internal> mClientRefrence;

    public Sender() {
    }

    void bindClient(IClient.Internal client) {
        mClientRefrence = new WeakReference<>(client);
    }


    /**
     * 是否允许发送
     *
     * @return
     */
    protected boolean isPrepare() {
        return null != mClientRefrence && null != mClientRefrence.get();
    }

    /**
     * 发送
     *
     * @param text
     */
    protected void send(String text) {
        if (isPrepare())
            mClientRefrence.get().sendMessage(text);
    }

}
