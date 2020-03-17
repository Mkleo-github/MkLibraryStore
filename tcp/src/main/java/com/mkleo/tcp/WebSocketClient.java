package com.mkleo.tcp;


import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @说明:
 * @作者: Wang HengJin
 * @日期: 2018/12/10 11:52 星期一
 */
public class WebSocketClient<T extends Sender> extends IClient.Internal<T> {

    private final Connector mConnector;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private WebSocket mWebSocket;

    private WebSocketClient(Connector connector) {
        this.mConnector = connector;
        if (null != mConnector.mSender)
            mConnector.mSender.bindClient(this);
    }

    @Override
    public void connect() {
        close();
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(mConnector.mConnectTimeoutSec, TimeUnit.SECONDS).build();
        mRequest = new Request.Builder().url(mConnector.mURL).build();
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, new WebSocketListener() {

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                if (null != mConnector.mListener)
                    mConnector.mListener.onConnect();
                if (null != mConnector.mMonitor)
                    mConnector.mMonitor.start();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                if (null != mConnector.mMonitor)
                    mConnector.mMonitor.received();
                if (null != mConnector.mReceiver)
                    mConnector.mReceiver.onMessage(text);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                if (null != mConnector.mListener)
                    mConnector.mListener.onFaild(t);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                if (null != mConnector.mListener)
                    mConnector.mListener.onClosed();
            }
        });
    }

    @Override
    public void close() {
        if (null != mConnector.mMonitor)
            mConnector.mMonitor.stop();
        if (null != mWebSocket) {
            mWebSocket.cancel();
            mWebSocket = null;
            mRequest = null;
            mOkHttpClient = null;
        }
    }

    @Override
    public T send() {
        return (T) mConnector.mSender;
    }

    @Override
    protected void sendMessage(String text) {
        if (null != mWebSocket && null != text) {
            mWebSocket.send(text);
        }
    }


    public static class Connector {

        /* 访问地址 */
        final String mURL;
        OnClientListener mListener;
        /* 连接监视器 */
        ConnectMonitor mMonitor;
        /* 发送器 */
        Sender mSender;
        /* 解析器 */
        Receiver mReceiver;
        /* 超时时间(秒) */
        int mConnectTimeoutSec = 10 ;

        public Connector(String url) {
            this.mURL = url;
        }

        public Connector openMonitor(int timeoutSec, IConnectMonitor.OnTimeoutListener onTimeoutListener) {
            this.mMonitor = new ConnectMonitor(timeoutSec, onTimeoutListener);
            return this;
        }

        public Connector addSender(Sender sender) {
            this.mSender = sender;
            return this;
        }

        public Connector addReceiver(Receiver receiver) {
            this.mReceiver = receiver;
            return this;
        }

        public Connector setOnClientListener(OnClientListener listener) {
            this.mListener = listener;
            return this;
        }

        public Connector setConnectTimeout(int timeoutSec) {
            this.mConnectTimeoutSec = timeoutSec;
            return this;
        }

        public <T extends Sender> WebSocketClient<T> build() {
            return new WebSocketClient<>(this);
        }
    }
}
