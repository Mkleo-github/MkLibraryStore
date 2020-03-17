package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.model.DownloadInfo;
import com.mkleo.downloader.model.DownloadPacket;

import java.util.Vector;

/**
 * 单个包(不支持断点下载)
 */
public class SinglePacketizer implements Packetizer {

    private Config mConfig;

    SinglePacketizer(Config config) {
        this.mConfig = config;
    }

    @Override
    public void doPacket(Callback callback) {
        try {
            //如果不支持断点续传,无关用户配置
            Vector<DownloadPacket> packets = new Vector<>();
            DownloadInfo info = new DownloadInfo(
                    mConfig.getUrl(),
                    mConfig.getPath(),
                    0,
                    mConfig.getMd5()
            );

            //不支持断点续传,只有一个下载包
            packets.add(new DownloadPacket(
                    mConfig.getPath(),
                    0, //包号
                    0, //开始位置
                    0, //结束位置
                    DownloadPacket.UNLOCK
            ));
            if (null != callback)
                callback.onSuccess(info, packets);
        } catch (Exception e) {
            if (null != callback)
                callback.onError(new DownloadError(DownloadPolicy.Error.PACKETIZE_ERROR, e.toString()));
        }

    }
}
