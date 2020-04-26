package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.model.DownloadRequest;


public class Guider implements IGuider<DownloadRequest> {

    private Config mConfig;
    /* 本地数据是否可用 */
    private boolean isDataAvailable;
    /* http引导结果 */
    private HttpGuider.Result mHttpResult;

    public Guider(Config config) {
        mConfig = config;
    }

    /**
     * 引导,单线程
     *
     * @param callback
     */
    @Override
    public void guide(final Callback<DownloadRequest> callback) {
        guideFile(callback);
    }

    private void guideFile(final Callback<DownloadRequest> callback) {
        //文件检测
        new FileGuider(mConfig).guide(new Callback<Boolean>() {
            @Override
            public void onError(DownloadError error) {
                if (null != callback)
                    callback.onError(error);
            }

            @Override
            public void onSuccess(Boolean result) {
                isDataAvailable = result;
                guideHttp(callback);
            }
        });
    }

    private void guideHttp(final Callback<DownloadRequest> callback) {
        //检测服务是否支持断点续传
        new HttpGuider(mConfig).guide(new Callback<HttpGuider.Result>() {

            @Override
            public void onError(DownloadError error) {
                if (null != callback)
                    callback.onError(error);
            }

            @Override
            public void onSuccess(HttpGuider.Result result) {
                mHttpResult = result;
                guidePacket(callback);
            }
        });
    }

    private void guidePacket(final Callback<DownloadRequest> callback) {
        //分包处理
        new PacketGuider(mConfig, isDataAvailable,
                mHttpResult.isSupportBreakpoint(),
                mHttpResult.getSize()).guide(new Callback<DownloadRequest>() {

            @Override
            public void onError(DownloadError error) {
                if (null != callback)
                    callback.onError(error);
            }

            @Override
            public void onSuccess(DownloadRequest request) {
                if (null != callback)
                    callback.onSuccess(request);
            }
        });
    }


}
