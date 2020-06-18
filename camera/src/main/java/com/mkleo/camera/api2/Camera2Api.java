package com.mkleo.camera.api2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import com.mkleo.camera.BaseCameraApi;
import com.mkleo.camera.Config;

import java.io.File;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Api extends BaseCameraApi {

    public Camera2Api(Context context, Config config) {
        super(context, config);
    }

    @Override
    public void setCallback(Callback callback) {

    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {

    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
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

    private Object mCameraId;
    private Infos mCameraInfos;
    private File mPictureFile;
    private CameraDevice mCameraDevice;
    //处理静态图像捕获。
    private ImageReader mImageReader;
    //预览
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            log("[相机已经打开]");
            mCameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            close();
            log("[相机连接关闭]");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            close();
            log("[相机发生异常]:" + error);
        }
    };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (null != mPictureFile) {
                mScheduler.ioThread(Camera2Util.getPictureSaver(reader.acquireLatestImage(), mPictureFile));
            }
        }
    };

    /**
     * 装载相机
     */
    private void setupCamera() throws Exception {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (!Camera2Util.isSupportCamera(manager)) throw new RuntimeException("[该设备不支持相机]");
        CameraCharacteristics characteristics = manager.getCameraCharacteristics((String) mCameraId);
        //创建相机信息
        mCameraInfos = new Infos(mCameraId, Camera2Util.getFacing(characteristics), Camera2Util.getSensorOrientation(characteristics));
        Size pictureSize = mConfig.getPictureSize();
        //创建拍照捕捉大小
        mImageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(),
                ImageFormat.JPEG, /*maxImages*/2);
        mImageReader.setOnImageAvailableListener(
                mOnImageAvailableListener, mScheduler.getIoHandler());
    }


    private void createSession(SurfaceTexture surfaceTexture) throws Exception {
        if (null == surfaceTexture) throw new NullPointerException("[相机纹理丢失]");
        //我们将默认缓冲区的大小配置为所需的摄像机预览的大小。
        surfaceTexture.setDefaultBufferSize(getPreviewSize().getWidth(), getPreviewSize().getHeight());
        //这是我们需要开始预览的输出Surface。
        Surface surface = new Surface(surfaceTexture);
        //设置预览
        mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewRequestBuilder.addTarget(surface);
        //在这里，我们创建了CameraCaptureSession来进行摄像机预览。
        mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        if (null == mCameraDevice) return;

                        // When the session is ready, we start displaying the preview.
                        mCaptureSession = cameraCaptureSession;
                        // Auto focus should be continuous for camera preview.
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                        // Flash is automatically enabled when necessary.
//                        setAutoFlash(mPreviewRequestBuilder);
//
//                        // Finally, we start displaying the camera preview.
//                        mPreviewRequest = mPreviewRequestBuilder.build();
//                        mCaptureSession.setRepeatingRequest(mPreviewRequest,
//                                mCaptureCallback, mBackgroundHandler);
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    }
                }, null
        );
    }

    private void close() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }


    private void log(String log) {
        Camera2Util.log(log);
    }
}
