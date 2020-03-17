package com.mkleo.downloader.model;

/**
 * 下载包
 */
public class DownloadPacket {

    //未锁定
    public static final int UNLOCK = 0;
    //锁定
    public static final int LOCK = 1;
    //完成
    public static final int COMPLETE = 2;

    //存储路径
    private String path;
    //包号
    private int number;
    //开始位置
    private long begin;
    //结束位置
    private long end;
    //状态
    private int status;

    public DownloadPacket(String path, int number, long begin, long end, int status) {
        this.path = path;
        this.number = number;
        this.begin = begin;
        this.end = end;
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public int getNumber() {
        return number;
    }

    public int getStatus() {
        return status;
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public void setEnd(long end) {
        this.end = end;
    }


}
