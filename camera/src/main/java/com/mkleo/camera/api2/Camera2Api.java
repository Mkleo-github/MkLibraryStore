package com.mkleo.camera.api2;

import android.content.Context;

import com.mkleo.camera.BaseCameraApi;
import com.mkleo.camera.Config;

public class Camera2Api extends BaseCameraApi {

    public Camera2Api(Context context, Config config) {
        super(context, config);
    }

    @Override
    public void setCallback(Callback callback) {

    }

    @Override
    public void startPreview(Object surface) {

    }

    @Override
    public Object getSurface() {
        return null;
    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void requestFocus(float scaleX, float scaleY) {

    }

    @Override
    public boolean switchCamera(Object cameraId) {
        return false;
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        return false;
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
        return false;
    }

    @Override
    public Object getCameraId() {
        return null;
    }

    @Override
    public Infos getCameraInfos() {
        return null;
    }

    @Override
    public Size getPreviewSize() {
        return null;
    }

    @Override
    public void release() {

    }
}
