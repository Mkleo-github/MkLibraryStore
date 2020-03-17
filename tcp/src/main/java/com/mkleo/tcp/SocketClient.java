package com.mkleo.tcp;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @说明:
 * @作者: Wang HengJin
 * @日期: 2018/12/6 16:46 星期四
 */
public class SocketClient<T extends Sender> extends IClient.Internal<T> {

    private final Connector mConnector;

    private SocketThread mSocketThread;
    /* 发送器 */
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private SocketClient(Connector connector) {
        this.mConnector = connector;
        if (null != mConnector.mSender)
            mConnector.mSender.bindClient(this);
    }


    private Socket mSocket;

    private class SocketThread extends Thread {

        private static final int BUFFER_SIZE = 16 * 1024;
        private boolean isLoop = true;

        /**
         * 关闭
         */
        synchronized void close() {
            if (!isLoop) return;
            isLoop = false;
            this.interrupt();
            if (null != mSocket && !mSocket.isClosed()) {
                try {
                    mSocket.close();
                    mSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != mConnector.mListener) {
                mConnector.mListener.onClosed();
            }
        }

        @Override
        public void run() {

            try {
                if (null != mSocket && !mSocket.isClosed()) {
                    mSocket.close();
                    mSocket = null;
                }

                mSocket = new Socket();
                SocketAddress address = new InetSocketAddress(mConnector.mIP, mConnector.mPort);
                mSocket.connect(address, mConnector.mConnectTimeoutSec * 1000);
                mSocket.setReceiveBufferSize(BUFFER_SIZE);

                if (null != mConnector.mListener)
                    mConnector.mListener.onConnect();
                if (null != mConnector.mMonitor)
                    mConnector.mMonitor.start();

                InputStream is = mSocket.getInputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while (isLoop && (length = is.read(buffer)) != -1) {
                    if (length > 0) {
                        //收到消息
                        String text = new String(Arrays.copyOf(buffer, length), mConnector.mChatsetName).trim();
                        //表示连接存活
                        if (null != mConnector.mMonitor)
                            mConnector.mMonitor.received();
                        if (null != mConnector.mReceiver)
                            mConnector.mReceiver.onMessage(text);
                    }
                }
                if (null != mSocket && !mSocket.isClosed()) {
                    mSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();

                if (null != mConnector.mListener) {
                    mConnector.mListener.onFaild(e);
                }
            }

        }
    }

    @Override
    public void connect() {
        close();
        mSocketThread = new SocketThread();
        mSocketThread.start();
    }


    @Override
    public void close() {
        if (null != mConnector.mMonitor)
            mConnector.mMonitor.stop();
        if (mSocketThread != null)
            mSocketThread.close();
        mSocketThread = null;
    }

    @Override
    protected void sendMessage(final String text) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (null != mSocket) {
                    try {
                        OutputStream outputStream = mSocket.getOutputStream();
                        if (null == outputStream) return;
                        outputStream.write(text.getBytes());
                        outputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public T send() {
        return (T) mConnector.mSender;
    }


    public static class Connector {

        final String mIP;
        final int mPort;
        /* 连接监听 */
        OnClientListener mListener;
        /* 连接监视器 */
        ConnectMonitor mMonitor;
        /* 发送器 */
        Sender mSender;
        /* 接收器 */
        Receiver mReceiver;
        /* 字符集,编码格式 */
        String mChatsetName = "UTF-8";
        /* 连接超时 */
        int mConnectTimeoutSec = 5;

        public Connector(String ip, int port) {
            this.mIP = ip;
            this.mPort = port;
        }

        public Connector openMonitor(int timeoutSec, IConnectMonitor.OnTimeoutListener onTimeoutListener) {
            this.mMonitor = new ConnectMonitor(timeoutSec, onTimeoutListener);
            return this;
        }

        public Connector setConnectTimeout(int timeoutSec) {
            this.mConnectTimeoutSec = timeoutSec;
            return this;
        }

        public Connector setCharsetName(String charsetName) {
            this.mChatsetName = charsetName;
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

        public <T extends Sender> SocketClient<T> build() {
            return new SocketClient<>(this);
        }
    }

}
