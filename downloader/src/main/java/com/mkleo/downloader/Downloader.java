package com.mkleo.downloader;

import android.content.Context;

import com.mkleo.downloader.db.DaoFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载器
 */
public class Downloader {

    private static Options sOptions;
    /* 线程池 */
    private static ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private static DownloadManager sDownloadManager;

    private Downloader() {

    }

    /**
     * 初始化下载器
     *
     * @param context
     * @param options
     */
    public static void init(Context context, Options options) {
        if (null == context || null == options)
            throw new RuntimeException("下载器初始化失败,初始化参数不能为Null");
        //初始化数据库
        DaoFactory.init(context);
        sOptions = options;
        if (null == sDownloadManager)
            sDownloadManager = new DownloadManager();
    }

    public static Options getOptions() {
        if (null == sOptions) throw new RuntimeException("请先初始化下载器");
        return sOptions;
    }

    /**
     * 下载
     *
     * @param config
     * @param listener
     */
    public static void download(final Config config, final DownloadListener listener) {
        if (null != sDownloadManager) {
            sExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    sDownloadManager.download(config, listener);
                }
            });
        }
    }

    /**
     * 取消下载
     *
     * @param path
     */
    public static void cancel(String path) {
        if (null != sDownloadManager)
            sDownloadManager.cancel(path);
    }

    /**
     * 取消所有下载
     */
    public static void cancelAll() {
        if (null != sDownloadManager)
            sDownloadManager.cancelAll();
    }


}
