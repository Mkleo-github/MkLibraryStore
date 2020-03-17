package com.mkleo.downloader.model;

/**
 * 下载信息
 */
public class DownloadInfo {

    //下载地址
    private String url;
    //存储路径
    private String path;
    //文件大小
    private long size;
    //文件md5
    private String md5;

    public DownloadInfo(String url, String path, long size, String md5) {
        this.url = url;
        this.path = path;
        this.size = size;
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public String getMd5() {
        return md5;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }
}
