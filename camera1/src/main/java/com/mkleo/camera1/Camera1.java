package com.mkleo.camera1;

import android.content.Context;

import java.io.File;

public class Camera1 implements ICamera {

    private Camera1Api mApi;
    private CameraState mState;
    private CameraState mPreview;
    private CameraState mTakePicture;
    private CameraState mVideoRecord;


    public Camera1(Context context, Config config) {
        mApi = new Camera1Api(context.getApplicationContext(), config);
        mPreview = new StatePreview(mApi);
        mTakePicture = new StateTakePicture(mApi) {
            @Override
            protected void onPictureTaken() {
                //图片生成后,状态设置为预览,并重新开启预览
                mState = mPreview;
                rePreview();
            }
        };
        mVideoRecord = new StateVideoRecord(mApi) {
            @Override
            protected void onStopRecord(boolean isRepreview) {
                //视频生成后,状态设置为预览
                mState = mPreview;
                if (isRepreview) {
                    rePreview();
                }
            }
        };
        //状态设置为预览
        mState = mPreview;
    }

    /**
     * 重新预览
     */
    private void rePreview() {
        mPreview.stopPreview();
        mPreview.startPreview(mApi.getSurface());
    }


    @Override
    public void setCallback(Callback callback) {
        mApi.setCallback(callback);
    }

    @Override
    public void startPreview(Object surface) {
        mState.startPreview(surface);
    }

    @Override
    public void stopPreview() {
        mState.stopPreview();
    }

    @Override
    public void requestFocus(float scaleX, float scaleY) {
        mState.requestFocus(scaleX, scaleY);
    }

    @Override
    public boolean switchCamera(Object cameraId) {
        return mState.switchCamera(cameraId);
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        return mState.setFlashMode(flashMode);
    }

    @Override
    public synchronized boolean takePicture(String path, PictureCallback callback) {
        //只有路径合法并且状态在预览
        if (isPathLegal(path) && mState == mPreview) {
            mState = mTakePicture;
            return mState.takePicture(path, callback);
        } else {
            return false;
        }
    }

    @Override
    public boolean startRecord(String path, VideoCallback callback) {
        //只有路径合法并且状态在预览
        if (isPathLegal(path) && mState == mPreview) {
            mState = mVideoRecord;
            return mState.startRecord(path, callback);
        } else {
            return false;
        }
    }

    @Override
    public boolean stopRecord() {
        return mState.stopRecord();
    }

    @Override
    public boolean zoom(boolean isEnlarge) {
        return mState.zoom(isEnlarge);
    }

    @Override
    public Object getCameraId() {
        return mState.getCameraId();
    }

    @Override
    public Infos getCameraInfos() {
        return mState.getCameraInfos();
    }

    @Override
    public Size getPreviewSize() {
        return mState.getPreviewSize();
    }

    @Override
    public void release() {
        mState.release();
    }


    /**
     * 路径是否合法
     *
     * @param path
     * @return
     */
    private boolean isPathLegal(String path) {
        return null != new File(path).getParentFile();
    }
}
