package com.mkleo.gles;

import android.opengl.EGLContext;


/**
 * des:脱离View的GL环境
 * by: Mk.leo
 * date: 2019/4/26
 */
public class GLOffscreen {

    protected final int mWidth;
    protected final int mHeight;
    private EGLContext mEGLContext;
    private GLThread mGLThread;

    public GLOffscreen(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setShared(EGLContext eglContext) {
        if (null != mGLThread) throw new GLException("请在Render初始化之前设置共享");
        this.mEGLContext = eglContext;
    }

    public void setRender(GLRender glRender) {
        this.setRender(glRender, GLThread.REFRESH_AUTO);
    }

    public void setRender(GLRender glRender, @GLThread.RefreshMode int refreshMode) {
        mGLThread = new GLThread();
        mGLThread.start();
        mGLThread.setRender(glRender);
        mGLThread.setRefreshMode(refreshMode);
        mGLThread.create(mWidth, mHeight, mEGLContext);
    }

    public EGLContext getEGLContext() {
        if (null == mGLThread) return null;
        return mGLThread.getEGLContext();
    }

    public void requestRender() {
        if (null != mGLThread)
            mGLThread.requestRender();
    }


    public void change(int width, int height) {
        if (null != mGLThread)
            mGLThread.change(width, height);
    }

    public void destroy() {
        if (null != mGLThread)
            mGLThread.quit();
        mEGLContext = null;
        mGLThread = null;
    }

}
