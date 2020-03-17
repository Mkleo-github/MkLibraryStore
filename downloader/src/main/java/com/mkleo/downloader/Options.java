package com.mkleo.downloader;


import com.mkleo.downloader.utils.Helper;

public class Options {

    /* 单个包大小 */
    private final long packetSize;
    /* 是否启用断点下载 */
    private final boolean isBreakpoint;
    /* 并发线程数 */
    private final int threadCount;
    /* 可同时执行的任务数 */
    private final int taskCountAtSameTime;

    public int getTaskCountAtSameTime() {
        return taskCountAtSameTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public long getPacketSize() {
        return packetSize;
    }

    public boolean isBreakpoint() {
        return isBreakpoint;
    }

    private Options(long packetSize, boolean isBreakpoint, int threadCount, int taskCountAtSameTime) {
        this.packetSize = packetSize;
        this.isBreakpoint = isBreakpoint;
        this.threadCount = threadCount;
        this.taskCountAtSameTime = taskCountAtSameTime;
    }

    public static class Builder {

        /* 单个包大小 */
        private long packetSize = DownloadPolicy.Default.PACKET_SIZE;
        /* 是否启用断点下载 */
        private boolean isBreakpoint = false;
        /* 并发线程数 */
        private int threadCount = DownloadPolicy.Default.THREAD_COUNT;
        /* 可同时执行的任务数 */
        private int taskCountAtSameTime = DownloadPolicy.Default.TASK_COUNT;


        /**
         * 设置断点下载
         *
         * @param packetSize  每个包的大小
         * @param threadCount 每个任务的线程数
         * @return
         */
        public Builder setBreakpoint(long packetSize, int threadCount) {
            this.isBreakpoint = true;
            if (packetSize > 0)
                this.packetSize = packetSize;
            else Helper.log("包数值异常:" + packetSize
                    + " 将使用默认值:" + DownloadPolicy.Default.PACKET_SIZE);
            if (threadCount > 0 && threadCount <= DownloadPolicy.Default.MAX_THREAD_COUNT)
                this.threadCount = threadCount;
            else Helper.log("线程数设置异常:" + threadCount
                    + " 将使用默认值:" + DownloadPolicy.Default.THREAD_COUNT);
            return this;
        }


        /**
         * 设置同一时间可执行的任务数
         *
         * @param count 任务数
         */
        public Builder setTaskCountAtSameTime(int count) {
            this.taskCountAtSameTime = count;
            return this;
        }

        public Options build() {
            return new Options(packetSize, isBreakpoint, threadCount, taskCountAtSameTime);
        }
    }
}
