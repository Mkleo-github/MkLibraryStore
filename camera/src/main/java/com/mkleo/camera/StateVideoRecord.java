package com.mkleo.camera;

import android.graphics.SurfaceTexture;

class StateVideoRecord extends CameraState {

    /* 是否正在录制 */
    private boolean isRecording = false;
    /* 是否重启预览 */
    private boolean isRepreview = false;

    StateVideoRecord(ICamera api) {
        super(api);
    }

    @Override
    public void setCallback(Callback callback) {
        mApi.setCallback(callback);
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        //视频录制不可调用
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mApi.getSurfaceTexture();
    }

    @Override
    public void stopPreview() {
        if (isRecording) {
            stopRecord();
            isRepreview = false;
        }
    }

    @Override
    public void requestFocus(float scaleX, float scaleY) {
        mApi.requestFocus(scaleX, scaleY);
    }

    @Override
    public boolean switchCamera(Object cameraId) {
        //视频录制不可调用
        return false;
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        //视频录制中途不能设置闪光灯模式
        return false;
    }

    @Override
    public boolean takePicture(String path, PictureCallback callback) {
        //视频录制不可调用
        return false;
    }

    @Override
    public boolean startRecord(String path, final VideoCallback callback) {
        if (isRecording) return false;
        isRecording = true;
        isRepreview = true;
        return mApi.startRecord(path, new VideoCallback() {
            @Override
            public void onStartRecord() {
                if (null != callback)
                    callback.onStartRecord();
            }

            @Override
            public void onStopRecord(String path) {
                StateVideoRecord.this.onStopRecord(isRepreview);
                if (null != callback)
                    callback.onStopRecord(path);
            }
        });
    }

    @Override
    public boolean stopRecord() {
        if (!isRecording) return false;
        isRecording = false;
        return mApi.stopRecord();
    }

    @Override
    public boolean zoom(boolean isEnlarge) {
        return mApi.zoom(isEnlarge);
    }

    @Override
    public Object getCameraId() {
        return mApi.getCameraId();
    }

    @Override
    public Infos getCameraInfos() {
        return mApi.getCameraInfos();
    }

    @Override
    public Size getPreviewSize() {
        return mApi.getPreviewSize();
    }

    @Override
    public void release() {
        if (isRecording) stopRecord();
        isRepreview = false;
        mApi.release();
    }

    protected void onStopRecord(boolean isRepreview) {
    }
}
