package com.mkleo.downloader;

public class DownloadError {

    private String mMessage;
    private int mErrorCode;

    public DownloadError(int errorCode, String message) {
        this.mMessage = message;
        this.mErrorCode = errorCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
