package com.mkleo.camera1;

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
    public synchronized void startPreview(Object surface) {
        if (!isPreview) {
            isPreview = true;
            mApi.startPreview(surface);
        }
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
    public boolean takePicture(String path, ICamera.PictureCallback callback) {
        return false;
    }

    @Override
    public boolean startRecord(String path, ICamera.VideoCallback callback) {
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
