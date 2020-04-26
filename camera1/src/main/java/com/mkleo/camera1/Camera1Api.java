package com.mkleo.camera1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.mkleo.bases.sensor.DirectionSensor;
import com.mkleo.camera1.utils.Camera1Util;
import com.mkleo.camera1.utils.HandlerScheduler;
import com.mkleo.helper.BitmapUtil;
import com.mkleo.helper.MkLog;
import com.mkleo.helper.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 相机API 只提供能力
 */
class Camera1Api implements ICamera {

    /* 配置项 */
    private Config mConfig;

    private Context mContext;
    /* Api */
    private Camera mCamera;
    /* 线程调度器 */
    private HandlerScheduler mScheduler;
    /* 显示承载 */
    private Object mSurface;
    /* 当前摄像头ID */
    private int mCameraId;
    /* 设备旋转角度 */
    private int mDeviceAngle;
    /* 方向传感器 */
    private DirectionSensor mDirectionSensor;
    /* 相机参数 */
    private Camera.Parameters mParameters;
    /* 录制器 */
    private MediaRecorder mMediaRecorder;
    /* 视频录制的路径 */
    private String mVideoPath;
    /* 视频录制回调 */
    private VideoCallback mVideoCallback;
    /* 相机回调 */
    private Callback mCallback;
    /* 缩放级别 */
    private int mZoomLevel = 0;

    Camera1Api(Context context, Config config) {
        this.mContext = context;
        this.mConfig = config;
        this.mCameraId = (int) config.getCameraId();
        prepare();
    }


    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void startPreview(final Object surface) {
        if (null == mScheduler) return;
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!checkSurface(surface))
                        throw new RuntimeException("Surface类型应为 SurfaceHolder或者SurfaceTexture");
                    mSurface = surface;
                    //开启相机,
                    open();
                    //开始预览
                    if (mSurface instanceof SurfaceHolder) {
                        mCamera.setPreviewDisplay((SurfaceHolder) mSurface);
                    } else {
                        mCamera.setPreviewTexture((SurfaceTexture) mSurface);
                    }
                    mCamera.startPreview();
                    log("开始预览");
                    if (null != mCallback) {
                        Camera.Size previewSize = mParameters.getPreviewSize();
                        boolean isSetupVertical = Camera1Util.isCameraSetupVertical(mCameraId);
                        mCallback.onStartPreview(
                                new Size(
                                        isSetupVertical ? previewSize.width : previewSize.height,
                                        isSetupVertical ? previewSize.height : previewSize.width
                                ));
                    }
                } catch (IOException ignored) {
                }
            }
        });
    }

    @Override
    public void stopPreview() {
        if (null == mScheduler) return;
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                log("停止预览");
                close();
                if (null != mCallback)
                    mCallback.onStopPreview();
            }
        });
    }

    @Override
    public void requestFocus(final float scaleX, final float scaleY) {
        if (isUnavailable()) return;
        log("请求对焦 [x:" + scaleX + "] [y:" + scaleX + "]");
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                if (scaleX < 0 || scaleY < 0) {
                    //采取自动对焦
                    mCamera.cancelAutoFocus();
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            log("对焦完成 [" + success + "]");
                        }
                    });
                } else {
                    Camera.Area focusArea = Camera1Util.toArea(scaleX, scaleY);
                    //记录当前用户设置的对焦模式
                    final String setFocusMode = mParameters.getFocusMode();
                    //测光区域
                    List<Camera.Area> meteringAreas = new ArrayList<>();
                    //对焦区域
                    List<Camera.Area> focusAreas = new ArrayList<>();
                    if (mParameters.getMaxNumMeteringAreas() > 0) {
                        meteringAreas.add(focusArea);
                        focusAreas.add(focusArea);
                    }
                    // 设置对焦模式
                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    // 设置对焦区域
                    mParameters.setFocusAreas(focusAreas);
                    // 设置测光区域
                    mParameters.setMeteringAreas(meteringAreas);

                    try {
                        // 每次对焦前，需要先取消对焦
                        mCamera.cancelAutoFocus();
                        // 设置相机参数
                        mCamera.setParameters(mParameters);
                        // 开启对焦
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                //恢复对焦模式
                                mParameters.setFocusMode(setFocusMode);
                                mCamera.setParameters(mParameters);
                                log("对焦完成 [" + success + "]");
                            }
                        });
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    @Override
    public boolean switchCamera(Object cameraId) {
        if (isUnavailable()) return false;
        if (null == cameraId) {
            //自动切换
            cameraId = Camera1Util.getNextCamera(mCameraId);
        }
        if (!Camera1Util.isSupportCamera((int) cameraId)) return false;
        mCameraId = (int) cameraId;
        stopPreview();
        startPreview(mSurface);
        return true;
    }

    @Override
    public boolean setFlashMode(int flashMode) {
        if (isUnavailable()) return false;
        if (!isSupportFlashMode(flashMode)) return false;
        mParameters.setFlashMode(Camera1Util.getFlashMode(flashMode));
        mCamera.setParameters(mParameters);
        return true;
    }

    @Override
    public boolean takePicture(final String path, final PictureCallback callback) {
        if (isUnavailable()) return false;
        log("拍照 [" + path + "]");
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                mCamera.cancelAutoFocus();
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        mCamera.cancelAutoFocus();
                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                //生成图片
                                File file = new File(path);
                                file.getParentFile().mkdirs();
                                Bitmap picture = BitmapUtil.getBitmap(data);
                                //旋转
                                picture = BitmapUtil.rotate(picture, Camera1Util.getRotationAngle(mDeviceAngle, mCameraId));
                                if (Camera1Util.isFrontCamera(mCameraId)) {
                                    //如果是前置摄像头,需要做镜像处理
                                    picture = BitmapUtil.flip(picture, -1, 1);
                                }
                                //保存图片
                                BitmapUtil.save(path, picture);
                                //通知系统更新
                                SystemUtil.notifySystemUpdateMedia(mContext, path);
                                if (!picture.isRecycled())
                                    picture.recycle();
                                mScheduler.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (null != callback)
                                            callback.onPictureTaken(path);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean startRecord(final String path, final VideoCallback callback) {
        if (isUnavailable()) return false;
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                log("开始录制 [" + path + "]");
                mVideoPath = path;
                mVideoCallback = callback;

                mCamera.unlock();
                //实例化
                mMediaRecorder = new MediaRecorder();
                //改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
                mMediaRecorder.setOrientationHint(Camera1Util.getRotationAngle(mDeviceAngle, mCameraId));
                mMediaRecorder.setCamera(mCamera);
                //设置从麦克风采集声音
                mMediaRecorder.setAudioSource(mConfig.getAudioSource());
                //设置从摄像头采集图像
                mMediaRecorder.setVideoSource(mConfig.getVideoSource());
                //设置视频的输出格式
                mMediaRecorder.setOutputFormat(mConfig.getOutputFormat());
                //设置音频的编码格式
                mMediaRecorder.setAudioEncoder(mConfig.getAudioEncode());
                //设置视频的编码格式
                mMediaRecorder.setVideoEncoder(mConfig.getVideoEncode());
                if (mConfig.getVideoBitRate() > 0) {
                    //设置视频编码的比特率
                    mMediaRecorder.setVideoEncodingBitRate(mConfig.getVideoBitRate());
                }
                if (mConfig.getVideoWidth() > 0 && mConfig.getVideoHeight() > 0) {
                    Camera.Size videoSize = Camera1Util.getNearSize(
                            mParameters.getSupportedVideoSizes(),
                            mConfig.getVideoWidth(), mConfig.getVideoHeight()
                    );
                    //设置视频大小
                    mMediaRecorder.setVideoSize(videoSize.width, videoSize.height);
                }
                if (mConfig.getVideoFps() > 0) {
                    //设置帧率
                    mMediaRecorder.setVideoFrameRate(mConfig.getVideoFps());
                }
                if (mSurface instanceof SurfaceHolder) {
                    mMediaRecorder.setPreviewDisplay(((SurfaceHolder) mSurface).getSurface());
                } else if (mSurface instanceof SurfaceTexture) {
                    mMediaRecorder.setPreviewDisplay(new Surface((SurfaceTexture) mSurface));
                }
                File file = new File(mVideoPath);
                if (file.exists()) file.delete();
                file.getParentFile().mkdirs();
                //设置视频存储路径
                mMediaRecorder.setOutputFile(mVideoPath);

                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    if (null != mVideoCallback)
                        mVideoCallback.onStartRecord();
                } catch (IOException e) {
                    throw new RuntimeException(e.toString());
                }

            }
        });
        return true;
    }

    @Override
    public boolean stopRecord() {
        if (isUnavailable()) return false;
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                log("停止录制 [" + mVideoPath + "]");
                if (null != mMediaRecorder) {
                    //回收
                    try {
                        //不设置为Null会导致RuntimeExecotion
                        mMediaRecorder.setOnErrorListener(null);
                        mMediaRecorder.setOnInfoListener(null);
                        mMediaRecorder.setPreviewDisplay(null);
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                    } catch (Exception ignored) {
                    }
                    mMediaRecorder = null;
                    //通知系统更新媒体文件
                    SystemUtil.notifySystemUpdateMedia(mContext, mVideoPath);
                    mScheduler.mainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mVideoCallback) {
                                mVideoCallback.onStopRecord(mVideoPath);
                                mVideoCallback = null;
                            }
                        }
                    });
                }
            }
        });

        return true;
    }

    @Override
    public boolean zoom(final boolean isEnlarge) {
        if (isUnavailable()) return false;
        mScheduler.ioThread(new Runnable() {
            @Override
            public void run() {
                if (!mParameters.isZoomSupported() ||
                        !mParameters.isSmoothZoomSupported()) return;
                int maxZoom = mParameters.getMaxZoom();
                if (isEnlarge) {
                    //增加一级
                    mZoomLevel++;
                    if (mZoomLevel > maxZoom) mZoomLevel = maxZoom;
                } else {
                    //降低一级
                    mZoomLevel--;
                    if (mZoomLevel < 0) mZoomLevel = 0;
                }
                mParameters.setZoom(mZoomLevel);
                mCamera.setParameters(mParameters);
                log("缩放 [" + mZoomLevel + "]");
            }
        });
        return true;
    }

    @Override
    public Object getCameraId() {
        return mCameraId;
    }

    @Override
    public Infos getCameraInfos() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        return new Infos(mCameraId, info.facing, info.orientation);
    }

    @Override
    public Size getPreviewSize() {
        if (null != mParameters) {
            Camera.Size previewSize = mParameters.getPreviewSize();
            return new Size(
                    Math.min(previewSize.width, previewSize.height),
                    Math.max(previewSize.width, previewSize.height)
            );
        }
        return new Size(0, 0);
    }

    @Override
    public void release() {
        stopPreview();
        destroy();
    }

    Object getSurface() {
        return mSurface;
    }


    /**
     * 检测surface
     *
     * @param surface
     * @return
     */
    private boolean checkSurface(Object surface) {
        if (null == surface) return false;
        return surface instanceof SurfaceHolder
                || surface instanceof SurfaceTexture;
    }

    /**
     * 准备工作
     */
    private void prepare() {
        //初始化线程调度器
        mScheduler = new HandlerScheduler(getClass().getSimpleName());
        mScheduler.prepare();
        //初始化方向传感器
        mDirectionSensor = new DirectionSensor(mContext) {
            @Override
            public void onAngleChanged(int angle) {
                mDeviceAngle = angle;
            }
        };
    }


    /**
     * 打开相机
     */
    private void open() {
        //检测是否支持相机设备
        if (!Camera1Util.isSupportCamera())
            throw new RuntimeException("该设备不支持相机!");
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            throw new RuntimeException("请赋予相机权限!");
        //打开摄像头
        mCamera = Camera.open(mCameraId);
        int previewAngle = Camera1Util.getPreviewAngle(mContext, mCameraId);
        log("设置预览角度 [" + previewAngle + "]");
        mCamera.setDisplayOrientation(previewAngle);
        mParameters = mCamera.getParameters();
        //设置预览大小
        if (mConfig.getPreviewWidth() > 0 && mConfig.getPreviewHeight() > 0) {
            Camera.Size previewSize = Camera1Util.getNearSize(
                    mParameters.getSupportedPreviewSizes(),
                    mConfig.getPreviewWidth(), mConfig.getPreviewHeight()
            );
            log("配置的预览大小 [w:" + mConfig.getPreviewWidth() + "] [h:" + mConfig.getPreviewHeight() + "]");
            log("设置的预览大小 [w:" + previewSize.width + "] [h:" + previewSize.height + "]");
            mParameters.setPreviewSize(previewSize.width, previewSize.height);
        }
        //只有第一次打开才会设置
        //设置对焦模式
        if (isSupportFocusMode(mConfig.getFocusMode())) {
            mParameters.setFocusMode(
                    Camera1Util.getFocusMode(mConfig.getFocusMode())
            );
        }
        //设置闪光灯模式
        if (isSupportFlashMode(mConfig.getFlashMode())) {
            mParameters.setFlashMode(
                    Camera1Util.getFlashMode(mConfig.getFlashMode())
            );
        }
        //设置图片格式
        mParameters.setPictureFormat(mConfig.getPictureFormat());
        if (mConfig.getPictureWidth() > 0 && mConfig.getPreviewHeight() > 0) {
            //设置输出图片大小
            Camera.Size pictureSize = Camera1Util.getNearSize(
                    mParameters.getSupportedPictureSizes(),
                    mConfig.getPictureWidth(), mConfig.getPictureHeight()
            );
            mParameters.setPictureSize(pictureSize.width, pictureSize.height);
        }
        //设置参数
        mCamera.setParameters(mParameters);
    }

    /**
     * 关闭相机
     */
    private void close() {
        try {
            if (null != mCamera) {
                log("关闭相机");
                mZoomLevel = 0;
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                mCamera.release();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 是否不可用
     *
     * @return
     */
    private boolean isUnavailable() {
        return null == mCamera || null == mScheduler;
    }

    /**
     * 闪光灯模式是否支持
     *
     * @param flashMode
     * @return
     */
    private boolean isSupportFlashMode(@Params.FlashMode int flashMode) {
        if (null == mParameters) return false;
        String setFlashMode = Camera1Util.getFlashMode(flashMode);
        List<String> supportFlashModes = mParameters.getSupportedFlashModes();
        if (null != supportFlashModes) {
            return supportFlashModes.contains(setFlashMode);
        }
        return false;
    }


    /**
     * 对焦模式是否支持
     *
     * @param focusMode
     * @return
     */
    private boolean isSupportFocusMode(@Params.FocusMode int focusMode) {
        if (null == mParameters) return false;
        String setFocusMode = Camera1Util.getFocusMode(focusMode);
        List<String> supportFocusModes = mParameters.getSupportedFocusModes();
        if (null != supportFocusModes) {
            return supportFocusModes.contains(setFocusMode);
        }
        return false;
    }


    private void destroy() {
        if (null != mScheduler) {
            mScheduler.ioThread(new Runnable() {
                @Override
                public void run() {
                    if (null != mDirectionSensor) {
                        mDirectionSensor.unbind();
                        mDirectionSensor = null;
                    }
                    mCallback = null;
                    mSurface = null;
                    mConfig = null;
                    mContext = null;
                    //清除线程
                    if (null != mScheduler) {
                        mScheduler.destroy();
                        mScheduler = null;
                    }
                }
            });
        }
    }

    private void log(String log) {
        Camera1Util.log(log);
    }
}
