package com.mkleo.gles;

import android.content.Context;
import android.opengl.EGLContext;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * des:
 * by: Mk.leo
 * date: 2019/4/12
 */
public class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static class LifeCycle {
        private static final int INITIALIZE = 0;
        private static final int CREATED = 1;
        private static final int CHANGED = 2;
        private static final int DESTROY = 3;
    }

    private int mLifeCycle = LifeCycle.INITIALIZE;
    private Surface mSurface;
    private EGLContext mEGLContext;
    private GLThread mGLThread;
    private GLRender mGLRender;


    public GLSurfaceView(Context context) {
        this(context, null);
    }

    public GLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        mGLThread = new GLThread();
        mGLThread.start();
    }

    public EGLContext getEGLContext() {
        return null == mGLThread ? null : mGLThread.getEGLContext();
    }

    /**
     * 设置共享
     *
     * @param surface    共享的surface
     * @param eglContext 共享的eglContext
     */
    public void setShared(Surface surface, EGLContext eglContext) {
        if (null != mGLRender) throw new GLException("请在Render初始化之前设置共享");
        if (null != surface) this.mSurface = surface;
        this.mEGLContext = eglContext;
    }

    public void setRender(GLRender glRender) {
        this.setRender(glRender, GLThread.REFRESH_AUTO);
    }

    public synchronized void setRender(GLRender glRender, @GLThread.RefreshMode int refreshMode) {
        if (this.mGLRender != null) throw new GLException("GLRender多次初始化");
        this.mGLRender = glRender;
        mGLThread.setRender(glRender);
        mGLThread.setRefreshMode(refreshMode);
        //如果当前生命周期是初始化状态或者销毁状态不执行以下创建操作
        if (mLifeCycle == LifeCycle.INITIALIZE
                || mLifeCycle == LifeCycle.DESTROY) {
            return;
        }
        mGLThread.create(mSurface, mEGLContext);
        //如果当前状态已经发生了变化调用change
        if (mLifeCycle == LifeCycle.CHANGED)
            mGLThread.change(getWidth(), getHeight());
    }

    public void requestRender() {
        if (null != mGLThread)
            mGLThread.requestRender();
    }


    @Override
    public synchronized void surfaceCreated(SurfaceHolder holder) {
        GLTools.logGL("Surface创建");
        mLifeCycle = LifeCycle.CREATED;
        if (null == mSurface)
            mSurface = holder.getSurface();
        //在setRender之前不会调用
        if (null != mGLThread && null != mGLRender)
            mGLThread.create(mSurface, mEGLContext);
    }

    @Override
    public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        GLTools.logGL("Surface发生改变:" + width + " " + height);
        mLifeCycle = LifeCycle.CHANGED;
        //在setRender之前不会调用
        if (null != mGLThread && null != mGLRender)
            mGLThread.change(width, height);
    }

    @Override
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        GLTools.logGL("Surface销毁");
        mLifeCycle = LifeCycle.DESTROY;
        if (null != mGLThread)
            mGLThread.destroyEGL();
        mSurface = null;
    }

    public void destroy() {
        if (null != mGLThread)
            mGLThread.quit();
        mEGLContext = null;
        mGLThread = null;
    }


}
