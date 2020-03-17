package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.utils.Helper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * des:HTTP引导
 * by: Mk.leo
 * date: 2019/3/29
 */
public class HttpGuider implements IGuider<HttpGuider.Result> {

    /* 测试range是否支持 */
    private static final String TEST_RANGE = "bytes=0-";

    private Config mConfig;

    HttpGuider(Config config) {
        mConfig = config;
    }

    @Override
    public void guide(Callback<Result> callback) {
        //开始引导
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(mConfig.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(DownloadPolicy.Default.REQUEST_METHOD);
            connection.setConnectTimeout(DownloadPolicy.Default.DOWNLOAD_TIMEOUT);
            connection.setRequestProperty("Range", TEST_RANGE);

            int responseCode = connection.getResponseCode();
            boolean isSupportBreakpoint;
            switch (responseCode) {

                case HttpURLConnection.HTTP_PARTIAL:
                    //说明支持断点续传
                    isSupportBreakpoint = true;
                    break;

                case HttpURLConnection.HTTP_OK:
                    //说明不支持断点续传
                    isSupportBreakpoint = false;
                    break;

                default:
                    //表示响应失败
                    if (null != callback)
                        callback.onError(
                                new DownloadError(DownloadPolicy.Error.GUIDE_ERROR, "网络响应失败:" + responseCode)
                        );
                    return;
            }

//            Map<String, List<String>> headers = connection.getHeaderFields();
//            logHeaders(headers);
            //获取文件长度
            long fileLength = connection.getContentLength();
            //引导完成
            if (null != callback)
                callback.onSuccess(new Result(isSupportBreakpoint, fileLength));
        } catch (Exception e) {
            if (null != callback)
                callback.onError(
                        new DownloadError(DownloadPolicy.Error.GUIDE_ERROR, e.toString())
                );
        } finally {
            try {
                if (null != connection)
                    connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印header信息
     *
     * @param headers
     */
    private void logHeaders(Map<String, List<String>> headers) {
        if (null == headers) return;
        Iterator headersEntry = headers.entrySet().iterator();
        while (headersEntry.hasNext()) {
            Map.Entry entry = (Map.Entry) headersEntry.next();
            String key = (String) entry.getKey();
            List<String> values = (List<String>) entry.getValue();
            StringBuilder builder = new StringBuilder();
            for (String value : values) {
                builder.append("Key:").append(key)
                        .append(" Value:").append(value)
                        .append("\r\n");
            }
            Helper.log(builder.toString());
        }
    }


    protected static class Result {

        private final boolean isSupportBreakpoint;

        private final long size;

        Result(boolean isSupportBreakpoint, long size) {
            this.isSupportBreakpoint = isSupportBreakpoint;
            this.size = size;
        }

        public boolean isSupportBreakpoint() {
            return isSupportBreakpoint;
        }

        public long getSize() {
            return size;
        }
    }

}
