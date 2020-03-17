package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.Downloader;
import com.mkleo.downloader.db.DaoFactory;
import com.mkleo.downloader.model.DownloadInfo;
import com.mkleo.downloader.model.DownloadPacket;

import java.util.Vector;

public class MutiPacketizer implements Packetizer {


    private Config mConfig;
    /* 本地数据库数据是否可用 */
    private boolean isDataAvailable;
    private long mSize;

    MutiPacketizer(Config config, boolean isDataAvailable, long size) {
        this.mConfig = config;
        this.isDataAvailable = isDataAvailable;
        this.mSize = size;
    }

    @Override
    public void doPacket(Callback callback) {
        try {

            if (isDataAvailable) {
                //数据可用
                DownloadInfo info = DaoFactory.getDownloadDao().getDownloadInfo(mConfig.getPath());
                Vector<DownloadPacket> packets = DaoFactory.getDownloadDao().getDownloadPackets(mConfig.getPath());
                if (null == info || null == packets || packets.size() == 0)
                    throw new Exception("获取本地数据失败  下载信息:" + info + " 包信息:" + packets);
                if (null != callback)
                    callback.onSuccess(info, packets);
            } else {
                //数据不可用,创建数据
                //包大小
                long packetSize = Downloader.getOptions().getPacketSize();
                int packetCount = (int) (mSize % packetSize == 0 ?
                        mSize / packetSize : mSize / packetSize + 1);
                Vector<DownloadPacket> packets = new Vector<>();
                DownloadPacket packet;
                for (int i = 0; i < packetCount; i++) {
                    //如果是最后一个包
                    if (i == packetCount - 1) {
                        // Range是从 0 计数的所以末尾值需要-1
                        packet = new DownloadPacket(
                                mConfig.getPath(),
                                i, i * packetSize, mSize - 1,
                                DownloadPacket.UNLOCK);
                    } else {
                        packet = new DownloadPacket(mConfig.getPath(),
                                i, i * packetSize, (i + 1) * packetSize - 1,
                                DownloadPacket.UNLOCK);
                    }
                    packets.add(packet);
                    //插入数据
                    DaoFactory.getDownloadDao().insert(packet);
                }

                DownloadInfo info = new DownloadInfo(
                        mConfig.getUrl(),
                        mConfig.getPath(),
                        mSize,
                        mConfig.getMd5()
                );
                //将数据插入数据库
                DaoFactory.getDownloadDao().insert(info);
                if (null != callback)
                    callback.onSuccess(info, packets);
            }

        } catch (Exception e) {
            //删除所有数据库数据
            DaoFactory.getDownloadDao().delete(mConfig.getPath());
            if (null != callback)
                callback.onError(new DownloadError(DownloadPolicy.Error.PACKETIZE_ERROR, e.toString()));
        }
    }
}
