package com.mkleo.downloader;

/**
 * 下载协议
 */
public class DownloadPolicy {

    /* 默认值 */
    public static class Default {
        /* 超时时间 */
        public static final int DOWNLOAD_TIMEOUT = 3 * 1000;
        /* 请求方式 */
        public static final String REQUEST_METHOD = "GET";
        /* 缓存大小 */
        public static final int BUFFER_SIZE = 4 * 1024;
        /* 单个包大小 */
        public static final long PACKET_SIZE = 2 * 1024 * 1024;
        /* 默认线程数 */
        public static final int THREAD_COUNT = 2;
        /* CPU个数 */
        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        /* 最大线程数 */
        public static final int MAX_THREAD_COUNT = CPU_COUNT + 1;
        /* 任务数 */
        public static final int TASK_COUNT = 1;
    }


    /**
     * 错误编码
     */
    public static class Error {

        /* 引导异常 */
        public static final int GUIDE_ERROR = 1;
        /* 分包失败 */
        public static final int PACKETIZE_ERROR = 2;
        /* 文件校验失败 */
        public static final int FILE_VERIFY_ERROR = 3;
        /* 下载错误 */
        public static final int DOWNLOAD_ERROR = 4;

    }
}
