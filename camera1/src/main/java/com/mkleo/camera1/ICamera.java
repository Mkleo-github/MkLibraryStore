package com.mkleo.camera1;

/**
 * 相机的抽象
 */
public interface ICamera {

    interface Callback {
        /**
         * 开始预览
         */
        void onStartPreview();

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
     * @param surface (SurfaceHolder/SurfaceTexture)
     */
    void startPreview(Object surface);

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

        private final Object cameraId;
        private final int face;
        private final int setupAngle;

        public Infos(Object cameraId, @Params.Facing int face, int setupAngle) {
            this.cameraId = cameraId;
            this.face = face;
            this.setupAngle = setupAngle;
        }

        public int getFace() {
            return face;
        }

        public int getSetupAngle() {
            return setupAngle;
        }

        public Object getCameraId() {
            return cameraId;
        }
    }

    class Size {

        private final int width;
        private final int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }
}
