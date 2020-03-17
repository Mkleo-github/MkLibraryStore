package com.mkleo.downloader;

import java.util.Vector;

/**
 * 下载管理器
 */
class DownloadManager {

    /* 任务管理 */
    private Vector<DownloadTask> mTasks = new Vector<>();
    /* 任务最大值 */
    private final int mMaxTask;
    /* 正在执行的任务数 */
    private int mWorkingTaskCount = 0;

    DownloadManager() {
        //同时并发的线程数不能超过最大线程数
        //理论最大任务数
        int theoryTaskMax = DownloadPolicy.Default.MAX_THREAD_COUNT /
                Downloader.getOptions().getThreadCount();
        //如果设置的任务数小于等于理论最大值,以设置的为最大值
        //否则以理论值为准
        if (Downloader.getOptions().getTaskCountAtSameTime() <= theoryTaskMax) {
            this.mMaxTask = Downloader.getOptions().getTaskCountAtSameTime();
        } else {
            this.mMaxTask = theoryTaskMax;
        }
    }

    synchronized void download(final Config config, final DownloadListener listener) {
        if (isRepeat(config)) return;
        //创建下载任务
        final DownloadTask task = new DownloadTask(config);
        task.setDownloadListener(new DownloadListener() {
            @Override
            public void onStart() {
                if (null != listener)
                    listener.onStart();
            }

            @Override
            public void onProcessing(long current, long total) {
                if (null != listener)
                    listener.onProcessing(current, total);
            }

            @Override
            public void onCancel() {
                if (null != listener)
                    listener.onCancel();
                onTaskFinish(task);
            }

            @Override
            public void onCompleted(String path) {
                if (null != listener)
                    listener.onCompleted(path);
                onTaskFinish(task);
            }

            @Override
            public void onError(DownloadError error) {
                if (null != listener)
                    listener.onError(error);
                onTaskFinish(task);
            }
        });
        mTasks.add(task);
        //开始执行
        doTask();
    }

    /**
     * 取消任务
     *
     * @param path
     */
    synchronized void cancel(String path) {
        for (DownloadTask task : mTasks) {
            if (path.equals(task.getTask())) {
                if (task.isWorking()) {
                    //结束任务
                    task.cancel();
                } else {
                    //还未开始执行
                    mTasks.remove(task);
                }
            }
        }
    }


    /**
     * 取消所有任务
     */
    void cancelAll() {
        for (DownloadTask task : mTasks) {
            if (task.isWorking()) {
                //结束任务
                task.cancel();
            } else {
                //还未开始执行
                mTasks.remove(task);
            }
        }
    }

    /**
     * 任务结束
     *
     * @param task
     */
    private void onTaskFinish(DownloadTask task) {
        //将任务从列表中清除
        mTasks.remove(task);
        mWorkingTaskCount--;
        //执行下一个任务
        doTask();
    }

    /**
     * 任务是否重复
     *
     * @param config
     * @return
     */
    private boolean isRepeat(Config config) {
        for (DownloadTask task : mTasks) {
            if (task.getTask().equals(config.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行任务
     */
    private synchronized void doTask() {
        if (mWorkingTaskCount < mMaxTask) {
            for (DownloadTask task : mTasks) {
                if (!task.isWorking()) {
                    task.doTask();
                    mWorkingTaskCount++;
                    break;
                }
            }
        }
    }

}
