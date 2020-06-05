package com.mkleo.camera;

import android.graphics.SurfaceTexture;

class StatePreview extends CameraState {

    private boolean isPreview = false;

    StatePreview(ICamera api) {
        super(api);
    }

    @Override
    public void setCallback(Callback callback) {
        mApi.setCallback(callback);
    }

    @Override
    public synchronized void startPreview(SurfaceTexture surfaceTexture) {
        if (!isPreview) {
            isPreview = true;
            mApi.startPreview(surfaceTexture);
        }
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mApi.getSurfaceTexture();
    }

    @Override
    public synchronized void stopPreview() {
        if (isPreview) {
            isPreview = false;
            mApi.stopPreview();
        }
    }

    @Override
    public void requestFocus(float scaleX, float scaleY) {
        mApi.requestFocus(scaleX, scaleY);
    }

    @Override
    public boolean switchCamera(Object cameraId) {
        return mApi.switchCamera(cameraId);
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        return mApi.setFlashMode(flashMode);
    }

    @Override
    public boolean takePicture(String path, PictureCallback callback) {
        return false;
    }

    @Override
    public boolean startRecord(String path, VideoCallback callback) {
        return false;
    }

    @Override
    public boolean stopRecord() {
        return false;
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
        mApi.release();
    }
}
