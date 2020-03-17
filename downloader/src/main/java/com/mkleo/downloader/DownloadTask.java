package com.mkleo.downloader;

import com.mkleo.downloader.db.DaoFactory;
import com.mkleo.downloader.guide.Guider;
import com.mkleo.downloader.guide.IGuider;
import com.mkleo.downloader.model.DownloadPacket;
import com.mkleo.downloader.model.DownloadRequest;
import com.mkleo.downloader.process.BreakpointProcess;
import com.mkleo.downloader.process.DownloadProcess;
import com.mkleo.downloader.process.NormalProcess;
import com.mkleo.downloader.process.Processer;
import com.mkleo.downloader.utils.Helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DownloadTask {

    private Config mConfig;

    private DownloadListener mDownloadListener;
    /* 下载的线程池 */
    private ExecutorService mProcessPool;
    /* 已经下载的大小 */
    private volatile long mDownloadSize;
    /* 总大小 */
    private long mTotalSize;
    /* 下载请求参数 */
    private DownloadRequest mRequest;

    private final Object mLock = new Object();

    private static final int INIT = 0;
    private static final int DOWNLOADING = 1;
    private static final int STOP = 2;

    /* 任务状态 */
    private volatile int mStatus = INIT;


    DownloadTask(Config config) {
        this.mConfig = config;
        this.mProcessPool = Executors.newFixedThreadPool(Downloader.getOptions().getThreadCount());
    }

    void setDownloadListener(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    /**
     * 执行下载任务
     */
    void doTask() {
        //开始运行
        mStatus = DOWNLOADING;
        //开始执行
        if (null != mDownloadListener)
            mDownloadListener.onStart();
        //开始前置工作
        new Guider(mConfig).guide(new IGuider.Callback<DownloadRequest>() {
            @Override
            public void onError(DownloadError error) {
                if (null != mDownloadListener)
                    mDownloadListener.onError(error);
            }

            @Override
            public void onSuccess(DownloadRequest request) {
                //开始下载
                mRequest = request;
                mTotalSize = mRequest.getDownloadInfo().getSize();
                //获取已经下载的大小
                mDownloadSize = getDownloadSize();
                Helper.log("当前下载:" + mDownloadSize + "  总大小:" + mTotalSize);
                download();
            }
        });
    }

    /**
     * 取消下载
     */
    void cancel() {
        if (null != mProcessPool) {
            cancelInternal();
            if (null != mDownloadListener)
                mDownloadListener.onCancel();
        }
    }

    private synchronized void cancelInternal() {
        if (mStatus != STOP) {
            mStatus = STOP;
            if (null != mProcessPool) {
                mProcessPool.shutdownNow();
                mProcessPool = null;
            }
        }
    }

    private void download() {
        //判断包是否都下载完成
        if (isComplete()) {
            //下载完毕
            onComplete();
            return;
        }

        for (DownloadPacket packet : mRequest.getDownloadPackets()) {
            //只有未锁定的包才会被加入执行线程
            if (packet.getStatus() == DownloadPacket.UNLOCK) {
                addProcess(packet);
            }
        }

    }


    /**
     * 添加处理线程
     *
     * @param packet
     */
    private void addProcess(final DownloadPacket packet) {
        //创建下载线程
        DownloadProcess process = mRequest.isBreakpoint() ?
                new BreakpointProcess(mRequest.getDownloadInfo().getUrl(), mRequest.getDownloadInfo().getPath(), packet) :
                new NormalProcess(mRequest.getDownloadInfo().getUrl(), mRequest.getDownloadInfo().getPath(), packet);
        Processer processer = new Processer(process, new DownloadListener() {

            private long processNotes = 0;


            @Override
            public void onStart() {
                Helper.log("包开始下载:" + packet.getNumber());
            }

            /**
             *
             * @param current 当前下载大小
             * @param total   包总大小
             */
            @Override
            public void onProcessing(long current, long total) {
                synchronized (mLock) {
                    mDownloadSize += current - processNotes;
                    processNotes = current;
                    if (null != mDownloadListener) {
                        mDownloadListener.onProcessing(mDownloadSize, mTotalSize);
                    }
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onCompleted(String path) {
                synchronized (mLock) {
                    if (isComplete()) {
                        if (mStatus != STOP) {
                            mStatus = STOP;
                            //检测所有包是否完成
                            onComplete();
                        }
                    }
                }
            }

            @Override
            public void onError(DownloadError error) {
                synchronized (mLock) {
                    cancelInternal();
                    if (null != mDownloadListener)
                        mDownloadListener.onError(error);
                }
            }
        });
        //加入线程执行
        if (null != mProcessPool)
            mProcessPool.execute(processer);
    }

    /**
     * 完成下载
     */
    private void onComplete() {
        //更新进度
        if (null != mDownloadListener)
            mDownloadListener.onProcessing(mTotalSize, mTotalSize);
        //读取文件的MD5
        String md5Local = Helper.getMD5(mRequest.getDownloadInfo().getPath());
        //服务器的MD5
        String md5Remote = mRequest.getDownloadInfo().getMd5();

        if (null == md5Remote) {
            //不需要校验
            if (null != mDownloadListener)
                mDownloadListener.onCompleted(mRequest.getDownloadInfo().getPath());
        } else {
            //需要校验
            if (md5Remote.equals(md5Local)) {
                //校验成功!
                if (null != mDownloadListener)
                    mDownloadListener.onCompleted(mRequest.getDownloadInfo().getPath());
            } else {
                //校验失败!
                //删除记录
                DaoFactory.getDownloadDao().delete(mRequest.getDownloadInfo().getPath());
                if (null != mDownloadListener)
                    mDownloadListener.onError(
                            new DownloadError(DownloadPolicy.Error.FILE_VERIFY_ERROR,
                                    "md5Local:" + md5Local
                                            + "  md5Remote:" + md5Remote)
                    );
            }
        }
    }

    /**
     * 获取已经下载的部分
     *
     * @return
     */
    private long getDownloadSize() {
        long size = mRequest.getDownloadInfo().getSize();
        for (DownloadPacket packet : mRequest.getDownloadPackets()) {
            if (packet.getStatus() != DownloadPacket.COMPLETE) {
                //减去未下载的部分
                size -= packet.getEnd() - packet.getBegin();
            }
        }
        return size;
    }

    /**
     * 是否已经完成
     *
     * @return
     */
    private boolean isComplete() {
        for (DownloadPacket packet : mRequest.getDownloadPackets()) {
            if (packet.getStatus() != DownloadPacket.COMPLETE) {
                return false;
            }
        }
        return true;
    }

    String getTask() {
        return mRequest.getDownloadInfo().getPath();
    }

    boolean isWorking() {
        return mStatus != INIT;
    }
}
