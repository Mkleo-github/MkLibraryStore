package com.mkleo.downloader.model;

import java.util.Vector;

public class DownloadRequest {

    private boolean isBreakpoint;
    private DownloadInfo downloadInfo;
    private Vector<DownloadPacket> downloadPackets;

    public DownloadRequest(boolean isBreakpoint, DownloadInfo downloadInfo, Vector<DownloadPacket> downloadPackets) {
        this.isBreakpoint = isBreakpoint;
        this.downloadInfo = downloadInfo;
        this.downloadPackets = downloadPackets;
    }

    public boolean isBreakpoint() {
        return isBreakpoint;
    }

    public DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public Vector<DownloadPacket> getDownloadPackets() {
        return downloadPackets;
    }

}
