package com.mkleo.downloader.process;

import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadListener;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.db.DaoFactory;
import com.mkleo.downloader.model.DownloadPacket;
import com.mkleo.downloader.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * des:
 * by: Mk.leo
 * date: 2019/3/28
 */
public class BreakpointProcess implements DownloadProcess {

    private String mUrl;
    private String mPath;
    private DownloadPacket mPacket;

    public BreakpointProcess(String url, String path, DownloadPacket packet) {
        this.mUrl = url;
        this.mPath = path;
        this.mPacket = packet;
        //锁定包
        mPacket.setStatus(DownloadPacket.LOCK);
    }

    @Override
    public void process(DownloadListener listener) {

        if (null != listener) listener.onStart();

        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        InputStream input = null;

        try {
            // Range: bytes=100- 从 101 bytes 之后开始传，一直传到最后。
            // Range: bytes=100-200 指定开始到结束这一段的长度，
            // 记住 Range 是从 0 计数 的，所以这个是要求服务器从 101 字节开始传，一直到 201 字节结束。

            long currentLenght = 0;
            long packetSize = mPacket.getEnd() - mPacket.getBegin();
            if (packetSize < 0) throw new RuntimeException("包大小异常");

            //创建文件
            File file = Helper.createFile(mPath, false);
            //开启连接
            URL url = new URL(mUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(DownloadPolicy.Default.DOWNLOAD_TIMEOUT);
            conn.setRequestMethod(DownloadPolicy.Default.REQUEST_METHOD);
            String requestProperty = "bytes="
                    + mPacket.getBegin()
                    + "-"
                    + mPacket.getEnd();
            //设置范围请求参数
            conn.setRequestProperty("Range", requestProperty);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
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
                    //更新进度
                    mPacket.setBegin(mPacket.getBegin() + len);
                    currentLenght += len;
                    if (null != listener)
                        listener.onProcessing(currentLenght, packetSize);
                }
            } else {
                //解锁
                mPacket.setStatus(DownloadPacket.UNLOCK);
                if (null != listener)
                    listener.onError(
                            new DownloadError(DownloadPolicy.Error.DOWNLOAD_ERROR, "断点下载请求失败:" + responseCode)
                    );
                return;
            }

            //下载完成
            mPacket.setStatus(DownloadPacket.COMPLETE);
            //修改数据库
            DaoFactory.getDownloadDao().update(mPacket);
            if (null != listener)
                listener.onCompleted(mPath);


        } catch (Exception e) {
            //解锁
            mPacket.setStatus(DownloadPacket.UNLOCK);
            if (null != listener)
                listener.onError(
                        new DownloadError(DownloadPolicy.Error.DOWNLOAD_ERROR, "断点下载异常:" + e.toString())
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
