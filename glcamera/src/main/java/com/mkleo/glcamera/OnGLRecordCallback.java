package com.mkleo.glcamera;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/21
 */
public interface OnGLRecordCallback {
    void onStartRecord();

    void onStopRecord(String path);
}
