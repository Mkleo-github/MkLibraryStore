package com.mkleo.camera1;

public class Config {


    /* 相机ID */
    private Object cameraId;
    /* 预览宽度 */
    private int previewWidth;
    /* 预览高度 */
    private int previewHeight;
    /* 预览帧率 */
//    private int previewFps;
    /* 对焦模式 */
    private int focusMode;
    /* 闪光灯模式 */
    private int flashMode;
    /* 图片宽度 */
    private int pictureWidth;
    /* 图片高度 */
    private int pictureHeight;
    /* 图片格式 */
    private int pictureFormat;
    /* 视频宽度 */
    private int videoWidth;
    /* 视频高度 */
    private int videoHeight;
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
        this.previewWidth = builder.previewWidth;
        this.previewHeight = builder.previewHeight;
//        this.previewFps = builder.previewFps;
        this.focusMode = builder.focusMode;
        this.flashMode = builder.flashMode;
        this.pictureWidth = builder.pictureWidth;
        this.pictureHeight = builder.pictureHeight;
        this.pictureFormat = builder.pictureFormat;
        this.videoWidth = builder.videoWidth;
        this.videoHeight = builder.videoHeight;
        this.videoFps = builder.videoFps;
        this.audioSource = builder.audioSource;
        this.videoSource = builder.videoSource;
        this.outputFormat = builder.outputFormat;
        this.audioEncode = builder.audioEncode;
        this.videoEncode = builder.videoEncode;
        this.videoBitRate = builder.videoBitRate;
    }

    public Object getCameraId() {
        return cameraId;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
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

    public int getPictureWidth() {
        return pictureWidth;
    }

    public int getPictureHeight() {
        return pictureHeight;
    }

    public int getPictureFormat() {
        return pictureFormat;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
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

    public static class Builder {

        private Object cameraId = 0;
        private int previewWidth = 720;
        private int previewHeight = 1280;
        //        private int previewFps = 15;
        private int focusMode = Params.FocusMode.AUTO;
        private int flashMode = Params.FlashMode.OFF;
        private int pictureWidth = previewWidth;
        private int pictureHeight = previewHeight;
        private int pictureFormat = Params.PictureFormat.JPEG;
        private int videoWidth = previewWidth;
        private int videoHeight = previewHeight;
        private int videoFps = 10;
        private int audioSource = Params.AudioSource.MIC;
        private int videoSource = Params.VideoSource.CAMERA;
        private int outputFormat = Params.OutputFormat.MP4;
        private int audioEncode = Params.AudioEncode.ACC;
        private int videoEncode = Params.VideoEncode.H264;
        private int videoBitRate = videoWidth * videoHeight;

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

        public Builder setPictureHeight(int pictureHeight) {
            this.pictureHeight = pictureHeight;
            return this;
        }

        public Builder setPictureWidth(int pictureWidth) {
            this.pictureWidth = pictureWidth;
            return this;
        }

//        public Builder setPreviewFps(int previewFps) {
//            this.previewFps = previewFps;
//            return this;
//        }

        public Builder setPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
            return this;
        }

        public Builder setPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
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

        public Builder setVideoHeight(int videoHeight) {
            this.videoHeight = videoHeight;
            return this;
        }

        public Builder setVideoSource(@Params.VideoSource int videoSource) {
            this.videoSource = videoSource;
            return this;
        }

        public Builder setVideoWidth(int videoWidth) {
            this.videoWidth = videoWidth;
            return this;
        }


        public Config build() {
            return new Config(this);
        }

    }

}
