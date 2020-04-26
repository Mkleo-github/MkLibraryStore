package com.mkleo.glcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.mkleo.camera1.Camera1;
import com.mkleo.camera1.Config;
import com.mkleo.camera1.ICamera;
import com.mkleo.camera1.Params;
import com.mkleo.gles.GLOffscreen;
import com.mkleo.gles.GLSurfaceView;
import com.mkleo.helper.MkLog;
import com.mkleo.helper.WindowUtil;

import java.util.List;

/**
 * des:OpenGL 相机操作(暂时只支持竖屏)
 * by: Mk.leo
 * date: 2019/5/10
 */
public abstract class GLCameraBaseView extends GLSurfaceView {

    /* 相机API */
    private Camera1 mCamera1;
    /* 用于显示的Render */
    private GLPreviewRender mGLPreviewRender;
    /* GL的离屏线程 */
    private GLOffscreen mGLOffscreen;
    /* 相机OES纹理渲染(FBO离屏模式) */
    private GLOesRender mGLOesRender;
    /* 是否已经准备完毕 */
    private boolean isPrepare = false;
    /* 视频录制 */
    private GLVideoRecorder mGLVideoRecorder;
    /* 相机监听 */
    private OnGLRecordCallback mOnGLRecordCallback;
    private int mWidth;
    private int mHeight;
    //是否正在录制
    private boolean isRecording = false;


    public GLCameraBaseView(Context context) {
        this(context, null);
    }

    public GLCameraBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLCameraBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract List<GLWatermark> setupWatermarks(int windowWidth, int windowHeight);

    public void setOnGLRecordCallback(OnGLRecordCallback onGLRecordCallback) {
        this.mOnGLRecordCallback = onGLRecordCallback;
    }

    /**
     * 初始化相机和离屏环境
     */
    private synchronized void init() {

        mWidth = WindowUtil.getWindowWidth(getContext());
        mHeight = WindowUtil.getWindowHeight(getContext());

        Config config = new Config.Builder()
                .setFocusMode(Params.FocusMode.VIDEO)
                .setFlashMode(Params.FlashMode.OFF)
                .setPreviewWidth(mWidth)
                .setPreviewHeight(mHeight)
                .setPictureWidth(mWidth)
                .setPictureHeight(mHeight)
                .build();
        mCamera1 = new Camera1(getContext(), config);
        mCamera1.setCallback(new ICamera.Callback() {
            @Override
            public void onStartPreview(ICamera.Size previewSize) {
                if (!isPrepare) {
                    //设置屏幕旋转
                    setAngle(getContext());
                    //创建屏幕显示渲染
                    mGLPreviewRender = new GLPreviewRender(getContext(), setupWatermarks(mWidth, mHeight));
                    mGLPreviewRender.setSharedTextureId(mGLOesRender.getGLFboTextureId());
                    //设置共享
                    setShared(null, mGLOffscreen.getEGLContext());
                    setRender(mGLPreviewRender);
                    isPrepare = true;
                }
            }

            @Override
            public void onStopPreview() {

            }
        });
        //创建视频录制
        mGLVideoRecorder = new GLVideoRecorder();
        //创建离屏GL环境
        mGLOffscreen = new GLOffscreen(mWidth, mHeight);
        //创建oes纹理渲染
        mGLOesRender = new GLOesRender(getContext(), mWidth, mHeight);
        mGLOesRender.setOnRenderCallback(new GLOesRender.OnRenderCallback() {
            @Override
            public void onCreateTexture(SurfaceTexture surfaceTexture) {
                //相机绑定纹理并预览
                mCamera1.startPreview(surfaceTexture);
            }
        });
        mGLOffscreen.setRender(mGLOesRender);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCamera1)
                    mCamera1.requestFocus(-1, -1);
            }
        });
    }

    /**
     * 拍照
     *
     * @param path
     */
    public void capture(String path, ICamera.PictureCallback callback) {
        if (null != mCamera1)
            mCamera1.takePicture(path, callback);
    }

    /**
     * 闪光灯控制
     *
     * @param mode
     */
    public void setFlashMode(@Params.FlashMode int mode) {
        if (null != mCamera1)
            mCamera1.setFlashMode(mode);
    }

    /**
     * 切换摄像头
     *
     * @param cameraId
     */
    public void switchCamera(Object cameraId) {
        if (null != mCamera1) {
            mCamera1.switchCamera(cameraId);
            setAngle(getContext());
        }
    }


    /**
     * 获取当前摄像头
     *
     * @return
     */
    public Object getCameraId() {
        if (null == mCamera1) return -1;
        return mCamera1.getCameraId();
    }


    /**
     * 开始录制
     *
     * @param config 参数配置
     * @return
     */
    public boolean startRecord(final GLVideoRecorder.Config config) {
        if (!isPrepare || isRecording) return false;
        isRecording = true;
        //由于录制的画面和显示的一致所以使用同一种render
        GLPreviewRender glRecordRender = new GLPreviewRender(getContext(), setupWatermarks(mWidth, mHeight));
        //共享Oes离屏纹理
        glRecordRender.setSharedTextureId(mGLOesRender.getGLFboTextureId());
        mGLVideoRecorder.startRecord(config, mGLOffscreen.getEGLContext(), glRecordRender, new GLVideoRecorder.Callback() {
            @Override
            public void onStartRecord() {
                if (null != mOnGLRecordCallback)
                    mOnGLRecordCallback.onStartRecord();
            }

            @Override
            public void onStopRecord(String path) {
                if (null != mOnGLRecordCallback)
                    mOnGLRecordCallback.onStopRecord(path);
                isRecording = true;
            }
        });
        return true;
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (isPrepare)
            mGLVideoRecorder.stopRecord();
    }

    /**
     * 设置旋转角(通过View加载的方向确定)
     *
     * @param context
     */
    public void setAngle(Context context) {
        if (null == mGLOesRender) return;
        //相机信息
        ICamera.Infos infos = mCamera1.getCameraInfos();
        //重置矩阵
        mGLOesRender.resetMatrix();
        //屏幕方向
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int previewAngle = getPreviewAngle(
                getWindowAngle(rotation),
                infos.getSetupAngle(),
                infos.getFace()
        );
        mGLOesRender.setAngle(previewAngle);
    }


    private int getWindowAngle(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 90;
            default:
                return 0;
        }
    }


    /**
     * 获取预览角度,但是Oes纹理的坐标和设备坐标是倒得需要加180度
     *
     * @param windowAngle
     * @param setupAngle
     * @param facing
     * @return
     */
    private int getPreviewAngle(
            int windowAngle,
            int setupAngle,
            @Params.Facing int facing) {

        log("[window:" + windowAngle + "] [setup:" + setupAngle + "]");

        int previewAngle;
        if (facing == Params.Facing.BACK) {
            //后置
            previewAngle = (setupAngle - windowAngle + 360) % 360;
        } else {
            //前置
            previewAngle = (setupAngle + windowAngle) % 360;
        }
        return previewAngle + 180;
    }

    @Override
    public void destroy() {
        super.destroy();

        if (null != mGLOffscreen) {
            mGLOffscreen.destroy();
            mGLOffscreen = null;
        }
        if (null != mCamera1) {
            mCamera1.release();
            mCamera1 = null;
        }
    }


    private void log(String log) {
        MkLog.print("[OpenGL Camera]", log);
    }
}
