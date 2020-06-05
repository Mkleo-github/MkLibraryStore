package com.mkleo.camera;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;

/**
 * 相机的抽象
 */
public interface ICamera {

    interface OnResultCallback<T> {
        void onSuccess(T result);

        void onError(String msg);
    }

    interface Callback {
        /**
         * 开始预览
         */
        void onStartPreview(Size previewSize);

        /**
         * 停止预览
         */
        void onStopPreview();
    }


    interface PictureCallback {
        /**
         * 拍照完成
         *
         * @param path
         */
        void onPictureTaken(String path);
    }


    interface VideoCallback {

        /**
         * 开始录制
         */
        void onStartRecord();

        /**
         * 停止录制
         *
         * @param path
         */
        void onStopRecord(String path);

    }


    /**
     * 设置相机监听
     *
     * @param callback
     */
    void setCallback(Callback callback);

    /**
     * 开始预览
     *
     * @param surfaceTexture (SurfaceTexture)
     */
    void startPreview(SurfaceTexture surfaceTexture);

    /**
     * 获取当前纹理
     *
     * @return
     */
    SurfaceTexture getSurfaceTexture();

    /**
     * 停止预览
     */
    void stopPreview();

    /**
     * 请求对焦
     *
     * @param scaleX 触摸点x的比例(占视图的比例),当value不在[0-1]之间 启动自动对焦
     * @param scaleY 触摸点y的比例(占视图的比例),当value不在[0-1]之间 启动自动对焦
     */
    void requestFocus(float scaleX, float scaleY);

    /**
     * 切换摄像头
     *
     * @param cameraId Api2的CameraId是String类型,null为自动切换
     */
    boolean switchCamera(Object cameraId);

    /**
     * 设置闪光灯模式
     *
     * @param flashMode
     */
    boolean setFlashMode(@Params.FlashMode int flashMode);


    /**
     * 拍照
     *
     * @param path
     * @param callback
     * @return
     */
    boolean takePicture(final String path, final PictureCallback callback);

    /**
     * 开始录制
     *
     * @param path
     * @param callback
     * @return
     */
    boolean startRecord(final String path, final VideoCallback callback);


    /**
     * 停止录制
     *
     * @return
     */
    boolean stopRecord();

    /**
     * 缩放
     *
     * @param isEnlarge 是否放大
     * @return
     */
    boolean zoom(boolean isEnlarge);


    /**
     * 获取当前的相机ID
     *
     * @return
     */
    Object getCameraId();

    /**
     * 获取当前摄像头信息
     *
     * @return
     */
    Infos getCameraInfos();


    /**
     * 获取当前预览大小(预览之后才会生成)
     *
     * @return
     */
    Size getPreviewSize();


    /**
     * 回收资源
     */
    void release();

    class Infos {

        //相机ID(Object是兼容作用)
        private final Object cameraId;
        //相机朝向
        private final int face;
        //图形传感器装配角度
        private final int sensorOrientation;

        public Infos(Object cameraId, @Params.Facing int face, int sensorOrientation) {
            this.cameraId = cameraId;
            this.face = face;
            this.sensorOrientation = sensorOrientation;
        }

        public int getFace() {
            return face;
        }

        public int getSensorOrientation() {
            return sensorOrientation;
        }

        public Object getCameraId() {
            return cameraId;
        }
    }

    /**
     * 为了兼容Camera1和Camera2
     */
    class Size {

        private final int width;
        private final int height;

        public Size(int width, int height) {
            if (width < 0 || height < 0)
                throw new RuntimeException("[非法的Size参数] " + toString());
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        @NonNull
        @Override
        public String toString() {
            return "[width:" + width + "] [height:" + height + "]";
        }
    }
}
