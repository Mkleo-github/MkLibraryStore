package com.mkleo.downloader.process;

import com.mkleo.downloader.DownloadListener;

/**
 * des:下载线程
 * by: Mk.leo
 * date: 2019/3/28
 */
public class Processer implements Runnable {

    private final DownloadProcess mDownloadProcess;
    private final DownloadListener mListener;

    public Processer(DownloadProcess downloadProcess, DownloadListener listener) {
        this.mDownloadProcess = downloadProcess;
        this.mListener = listener;
    }

    @Override
    public void run() {
        if (null != mDownloadProcess)
            mDownloadProcess.process(mListener);
    }

    public void cancel() {
        try {
            Thread.currentThread().interrupt();
            if (null != mListener)
                mListener.onCancel();
        } catch (Exception e) {
        }
    }

}
