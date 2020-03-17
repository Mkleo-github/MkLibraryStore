package com.mkleo.downloader;

/**
 * des:
 * by: Mk.leo
 * date: 2019/3/29
 */
public interface DownloadListener {

    /**
     * 任务开始
     */
    void onStart();

    /**
     * 正在处理回调
     *
     * @param current 当前下载大小
     * @param total   总大小
     */
    void onProcessing(long current, long total);

    /**
     * 任务取消
     */
    void onCancel();

    /**
     * 任务完成
     *
     * @param path
     */
    void onCompleted(String path);

    /**
     * 发生错误
     *
     * @param error
     */
    void onError(DownloadError error);

}
