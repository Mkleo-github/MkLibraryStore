package com.mkleo.downloader.process;

import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadListener;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.model.DownloadPacket;
import com.mkleo.downloader.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * des:普通下载模式
 * by: Mk.leo
 * date: 2019/3/28
 */
public class NormalProcess implements DownloadProcess {

    private String mUrl;
    private String mPath;
    private DownloadPacket mPacket;

    public NormalProcess(String url, String path, DownloadPacket packet) {
        this.mUrl = url;
        this.mPath = path;
        this.mPacket = packet;
        //锁定包
        mPacket.setStatus(DownloadPacket.LOCK);
    }

    @Override
    public void process(DownloadListener listener) {

        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        InputStream input = null;
        long currentLenght = 0;
        long packetSize = mPacket.getEnd() - mPacket.getBegin();

        try {

            //创建文件
            File file = Helper.createFile(mPath, true);
            //开启连接
            URL url = new URL(mUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(DownloadPolicy.Default.DOWNLOAD_TIMEOUT);
            conn.setRequestMethod(DownloadPolicy.Default.REQUEST_METHOD);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //读取数据
                input = conn.getInputStream();
                byte[] buffer = new byte[DownloadPolicy.Default.BUFFER_SIZE];
                int len = -1;//每次读取长度
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(mPacket.getBegin());//写入位置

                while ((len = input.read(buffer)) != -1) {
                    if (Thread.interrupted()) {
                        //线程被打断,停止下载
                        break;
                    }
                    //写入文件
                    raf.write(buffer, 0, len);
                    currentLenght += len;
                    if (null != listener)
                        listener.onProcessing(currentLenght, packetSize);
                }

            } else {
                //解除锁定
                mPacket.setStatus(DownloadPacket.UNLOCK);
                if (null != listener)
                    listener.onError(
                            new DownloadError(DownloadPolicy.Error.DOWNLOAD_ERROR, "下载请求失败:" + responseCode)
                    );
                return;
            }

            //下载完成
            mPacket.setStatus(DownloadPacket.COMPLETE);
            if (null != listener)
                listener.onCompleted(mPath);

        } catch (Exception e) {
            //解除锁定
            mPacket.setStatus(DownloadPacket.UNLOCK);
            if (null != listener)
                listener.onError(
                        new DownloadError(DownloadPolicy.Error.DOWNLOAD_ERROR, "下载异常:" + e.toString())
                );
        } finally {
            try {
                if (null != conn) conn.disconnect();
                if (null != raf) raf.close();
                if (null != input) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
