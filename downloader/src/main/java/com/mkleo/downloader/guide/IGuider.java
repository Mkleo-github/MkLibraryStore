package com.mkleo.downloader.guide;

import com.mkleo.downloader.DownloadError;

public interface IGuider<T> {

    interface Callback<T> {
        void onError(DownloadError error);

        void onSuccess(T result);
    }

    void guide(Callback<T> callback);
}
