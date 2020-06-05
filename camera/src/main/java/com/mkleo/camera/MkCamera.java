package com.mkleo.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;

import com.mkleo.camera.api1.Camera1Api;
import com.mkleo.camera.api2.Camera2Api;

import java.io.File;

public class MkCamera implements ICamera {

    private BaseCameraApi mApi;
    private CameraState mState;
    private CameraState mPreview;
    private CameraState mTakePicture;
    private CameraState mVideoRecord;

    MkCamera(Context context, Config config) {
        switch (config.getVersion()) {
            case Params.Version.CAMERA_1:
                mApi = new Camera1Api(context.getApplicationContext(), config);
                break;
            case Params.Version.CAMERA_2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mApi = new Camera2Api(context.getApplicationContext(), config);
                } else {
                    throw new RuntimeException("[安卓版本过低]:" + Build.VERSION.SDK_INT);
                }
                break;
            default:
                throw new RuntimeException("[不支持的Api版本]:" + config.getVersion());
        }
        initStateMachine();
    }

    @Override
    public void setCallback(Callback callback) {
        mApi.setCallback(callback);
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        mState.startPreview(surfaceTexture);
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mState.getSurfaceTexture();
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
     * 初始化状态机
     */
    private void initStateMachine() {
        mPreview = new StatePreview(mApi);
        mTakePicture = new StateTakePicture(mApi) {
            @Override
            protected void onPictureTaken() {
                //图片生成后,状态设置为预览,并重新开启预览
                mState = mPreview;
                repreview();
            }
        };
        mVideoRecord = new StateVideoRecord(mApi) {
            @Override
            protected void onStopRecord(boolean isRepreview) {
                //视频生成后,状态设置为预览
                mState = mPreview;
                if (isRepreview) {
                    repreview();
                }
            }
        };
        //状态设置为预览
        mState = mPreview;
    }

    /**
     * 重新预览
     */
    private void repreview() {
        mPreview.stopPreview();
        mPreview.startPreview(mApi.getSurfaceTexture());
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
