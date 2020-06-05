package com.mkleo.camera;

import android.os.Build;

public class Config {

    private int version;
    /* 相机ID */
    private Object cameraId;
    //预览大小
    private ICamera.Size previewSize;
    //图片大小
    private ICamera.Size pictureSize;
    //视频大小
    private ICamera.Size videoSize;
    /* 预览帧率 */
//    private int previewFps;
    /* 对焦模式 */
    private int focusMode;
    /* 闪光灯模式 */
    private int flashMode;
    /* 图片格式 */
    private int pictureFormat;
    /* 视频帧率 */
    private int videoFps;
    /* 音频来源  */
    private int audioSource;
    /* 视频来源 */
    private int videoSource;
    /* 输出格式 */
    private int outputFormat;
    /* 音频编码 */
    private int audioEncode;
    /* 视频编码 */
    private int videoEncode;
    /* 视频比特率 */
    private int videoBitRate;

    private Config(Builder builder) {
        this.cameraId = builder.cameraId;
        this.previewSize = builder.previewSize;
        this.pictureSize = builder.pictureSize;
        this.videoSize = builder.videoSize;
//        this.previewFps = builder.previewFps;
        this.focusMode = builder.focusMode;
        this.flashMode = builder.flashMode;
        this.pictureFormat = builder.pictureFormat;
        this.videoFps = builder.videoFps;
        this.audioSource = builder.audioSource;
        this.videoSource = builder.videoSource;
        this.outputFormat = builder.outputFormat;
        this.audioEncode = builder.audioEncode;
        this.videoEncode = builder.videoEncode;
        this.videoBitRate = builder.videoBitRate;
        this.version = builder.version;
    }

    public int getVersion() {
        return version;
    }

    public Object getCameraId() {
        return cameraId;
    }

    public ICamera.Size getPreviewSize() {
        return previewSize;
    }

    //    public int getPreviewFps() {
//        return previewFps;
//    }

    public int getFocusMode() {
        return focusMode;
    }

    public int getFlashMode() {
        return flashMode;
    }

    public ICamera.Size getPictureSize() {
        return pictureSize;
    }

    public ICamera.Size getVideoSize() {
        return videoSize;
    }

    public int getPictureFormat() {
        return pictureFormat;
    }

    public int getVideoFps() {
        return videoFps;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public int getVideoSource() {
        return videoSource;
    }

    public int getOutputFormat() {
        return outputFormat;
    }

    public int getAudioEncode() {
        return audioEncode;
    }

    public int getVideoEncode() {
        return videoEncode;
    }

    public int getVideoBitRate() {
        return videoBitRate;
    }

    /**
     * 设置支持的像素大小
     *
     * @param previewSize
     * @param pictureSize
     * @param videoSize
     */
    void setSupportSizes(ICamera.Size previewSize,
                         ICamera.Size pictureSize,
                         ICamera.Size videoSize) {
        this.previewSize = previewSize;
        this.pictureSize = pictureSize;
        this.videoSize  = videoSize;
    }

    public static class Builder {

        //默认使用Camera1
        private int version = Params.Version.CAMERA_1;
        private Object cameraId = 0;
        //预览大小
        private ICamera.Size previewSize = new ICamera.Size(720, 1280);
        private ICamera.Size pictureSize = new ICamera.Size(720, 1280);
        private ICamera.Size videoSize = new ICamera.Size(720, 1280);
        //        private int previewFps = 15;
        private int focusMode = Params.FocusMode.AUTO;
        private int flashMode = Params.FlashMode.OFF;
        private int pictureFormat = Params.PictureFormat.JPEG;
        private int videoFps = 10;
        private int audioSource = Params.AudioSource.MIC;
        private int videoSource = Params.VideoSource.CAMERA;
        private int outputFormat = Params.OutputFormat.MP4;
        private int audioEncode = Params.AudioEncode.ACC;
        private int videoEncode = Params.VideoEncode.H264;
        private int videoBitRate = 720 * 1280;

        public Builder setVersion(@Params.Version int version) {
            if (version == Params.Version.CAMERA_2
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //必须版本大于5.0
                this.version = version;
            }
            return this;
        }

        public Builder setCameraId(int cameraId) {
            this.cameraId = cameraId;
            return this;
        }

        public Builder setAudioEncode(@Params.AudioEncode int audioEncode) {
            this.audioEncode = audioEncode;
            return this;
        }

        public Builder setAudioSource(@Params.AudioSource int audioSource) {
            this.audioSource = audioSource;
            return this;
        }

        public Builder setFlashMode(@Params.FlashMode int flashMode) {
            this.flashMode = flashMode;
            return this;
        }

        public Builder setFocusMode(@Params.FocusMode int focusMode) {
            this.focusMode = focusMode;
            return this;
        }

        public Builder setOutputFormat(@Params.OutputFormat int outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }


        public Builder setPictureFormat(@Params.PictureFormat int pictureFormat) {
            this.pictureFormat = pictureFormat;
            return this;
        }

        public Builder setPictureSize(ICamera.Size pictureSize) {
            this.pictureSize = pictureSize;
            return this;
        }

        //        public Builder setPreviewFps(int previewFps) {
//            this.previewFps = previewFps;
//            return this;
//        }


        public Builder setPreviewSize(ICamera.Size previewSize) {
            this.previewSize = previewSize;
            return this;
        }

        public Builder setVideoSize(ICamera.Size videoSize) {
            this.videoSize = videoSize;
            return this;
        }

        public Builder setVideoBitRate(int videoBitRate) {
            this.videoBitRate = videoBitRate;
            return this;
        }

        public Builder setVideoEncode(@Params.VideoEncode int videoEncode) {
            this.videoEncode = videoEncode;
            return this;
        }

        public Builder setVideoFps(int videoFps) {
            this.videoFps = videoFps;
            return this;
        }

        public Builder setVideoSource(@Params.VideoSource int videoSource) {
            this.videoSource = videoSource;
            return this;
        }


        public Config build() {
            return new Config(this);
        }

    }

}
