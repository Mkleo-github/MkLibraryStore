package com.mkleo.downloader.guide;

import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.model.DownloadInfo;
import com.mkleo.downloader.model.DownloadPacket;

import java.util.Vector;

/**
 * des:分包策略
 * by: Mk.leo
 * date: 2019/4/1
 */
public interface Packetizer {

    interface Callback {
        /**
         * 成功
         *
         * @param info
         * @param packets
         */
        void onSuccess(DownloadInfo info, Vector<DownloadPacket> packets);

        void onError(DownloadError error);
    }

    void doPacket(Callback callback);
}
