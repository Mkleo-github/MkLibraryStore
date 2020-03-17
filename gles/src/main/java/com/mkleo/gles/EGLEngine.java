package com.mkleo.gles;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;


/**
 * des:EGL驱动引擎
 * by: Mk.leo
 * date: 2019/4/17
 */
class EGLEngine {


    private EGLDisplay mEGLDisplay;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLSurface mEGLSurface;

    void initEGL(int width, int height, EGLContext sharedContext) {
        initEGLDisplay();
        initEGLConfig();
        initEGLContext(sharedContext);
        initEGLSurface(width, height);
        makeCurrent();
    }

    void initEGL(Surface surface, EGLContext sharedContext) {
        initEGLDisplay();
        initEGLConfig();
        initEGLContext(sharedContext);
        initEGLSurface(surface);
        makeCurrent();
    }

    private void initEGLDisplay() {
        //获取默认显示设备
        mEGLDisplay = EGLHelper.getEGLDisplay();
    }

    private void initEGLConfig() {
        mEGLConfig = EGLHelper.getEGLConfig(mEGLDisplay);
    }

    private void initEGLContext(EGLContext sharedContext) {
        //创建EGLContext
        if (null != sharedContext) {
            //说明需要共享Context
            this.mEGLContext = EGLHelper.getEGLContext(mEGLDisplay, mEGLConfig, sharedContext);
        } else {
            this.mEGLContext = EGLHelper.getEGLContext(mEGLDisplay, mEGLConfig, EGL14.EGL_NO_CONTEXT);
        }
        EGLHelper.queryContext(mEGLDisplay, mEGLContext);
    }

    private void initEGLSurface(Surface surface) {
        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };
        //创建一个EGLSurface
        mEGLSurface = EGLHelper.getEGLSurface(surfaceAttribs, surface, mEGLDisplay, mEGLConfig);
    }

    private void initEGLSurface(int width, int height) {
        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };
        mEGLSurface = EGLHelper.getEGLSurface(surfaceAttribs, mEGLDisplay, mEGLConfig);
    }

    private void makeCurrent() {
        EGLHelper.makeCurrent(mEGLDisplay, mEGLContext, mEGLSurface);
    }


    /**
     * 交换显存数据
     *
     * @return
     */
    boolean swapBuffers() {
        return EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    EGLContext getEGLContext() {
        return mEGLContext;
    }

    void destroyEGL() {
        EGL14.eglMakeCurrent(mEGLDisplay,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);

        EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEGLSurface = null;

        EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
        mEGLContext = null;

        EGL14.eglTerminate(mEGLDisplay);
        mEGLDisplay = null;
    }
}
