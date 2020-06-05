package com.mkleo.camera;

import android.graphics.SurfaceTexture;

class StateTakePicture extends CameraState {


    private boolean isTakingPicture = false;

    StateTakePicture(ICamera api) {
        super(api);
    }

    @Override
    public void setCallback(Callback callback) {
        mApi.setCallback(callback);
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        //拍照状态无法调用
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mApi.getSurfaceTexture();
    }

    @Override
    public void stopPreview() {
        //拍照状态无法调用
    }

    @Override
    public void requestFocus(float scaleX, float scaleY) {
        //拍照状态无法调用
    }

    @Override
    public boolean switchCamera(Object cameraId) {
        //拍照状态无法调用
        return false;
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        //拍照状态无法调用
        return false;
    }

    @Override
    public synchronized boolean takePicture(String path, final PictureCallback callback) {
        if (isTakingPicture) return false;
        isTakingPicture = true;
        return mApi.takePicture(path, new PictureCallback() {
            @Override
            public void onPictureTaken(String path) {
                StateTakePicture.this.onPictureTaken();
                isTakingPicture = false;
                if (null != callback) {
                    callback.onPictureTaken(path);
                }
            }
        });
    }

    @Override
    public boolean startRecord(String path, VideoCallback callback) {
        //拍照状态无法调用
        return false;
    }

    @Override
    public boolean stopRecord() {
        //拍照状态无法调用
        return false;
    }

    @Override
    public boolean zoom(boolean isEnlarge) {
        return false;
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
        mApi.release();
    }

    protected void onPictureTaken() {

    }
}
