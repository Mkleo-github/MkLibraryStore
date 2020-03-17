package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.Downloader;
import com.mkleo.downloader.model.DownloadInfo;
import com.mkleo.downloader.model.DownloadPacket;
import com.mkleo.downloader.model.DownloadRequest;

import java.util.Vector;

/**
 * 分包引导
 */
public class PacketGuider implements IGuider<DownloadRequest> {


    private Config mConfig;
    /* 本地数据是否有效 */
    private boolean isDataAvailable;
    /* 是否支持断点下载 */
    private boolean isSupportBreakpoint;
    /* 文件大小 */
    private long mSize;

    PacketGuider(Config config, boolean isDataAvailable, boolean isSupportBreakpoint, long size) {
        this.mConfig = config;
        this.isDataAvailable = isDataAvailable;
        this.isSupportBreakpoint = isSupportBreakpoint;
        this.mSize = size;
    }


    @Override
    public void guide(final Callback<DownloadRequest> callback) {

        Packetizer packetizer = null;

        final boolean isBreakpoint = isSupportBreakpoint && Downloader.getOptions().isBreakpoint();

        if (isBreakpoint) {
            //用户设置为断点下载,并且服务器支持的情况
            packetizer = new MutiPacketizer(mConfig, isDataAvailable, mSize);
        } else {
            //不支持断点续传
            packetizer = new SinglePacketizer(mConfig);
        }

        packetizer.doPacket(new Packetizer.Callback() {
            @Override
            public void onSuccess(DownloadInfo info, Vector<DownloadPacket> packets) {
                if (packets.size() == 0) {
                    if (null != callback)
                        callback.onError(new DownloadError(
                                DownloadPolicy.Error.PACKETIZE_ERROR, "分包出现异常,数量为0"
                        ));
                } else {
                    if (null != callback)
                        callback.onSuccess(new DownloadRequest(isBreakpoint, info, packets));
                }
            }

            @Override
            public void onError(DownloadError error) {
                if (null != callback)
                    callback.onError(error);
            }
        });

    }

}
