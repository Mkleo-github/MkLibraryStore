package com.mkleo.gles;

import android.opengl.EGLContext;
import android.support.annotation.IntDef;
import android.view.Surface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * des:
 * by: Mk.leo
 * date: 2019/4/17
 */
public class GLThread extends Thread {

    public static final int REFRESH_AUTO = 0;
    public static final int REFRESH_REQUEST = 1;

    @IntDef({
            REFRESH_AUTO,
            REFRESH_REQUEST
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RefreshMode {
    }

    private int mRefreshMode = REFRESH_AUTO;

    private GLRender mGLRender;

    private EGLEngine mEGLEngine;

    private ArrayBlockingQueue<GLEvent> mEventQueue = new ArrayBlockingQueue<>(100);


    private final long mRefreshInterval;

    private boolean isEglReady = false;

    private volatile boolean isQuit = false;

    public GLThread(int fps) {
        this.mRefreshInterval = 1000 / fps;
    }

    public GLThread() {
        this(30);
    }

    public void setRender(GLRender glRender) {
        this.mGLRender = glRender;
    }

    public void setRefreshMode(@RefreshMode int refreshMode) {
        this.mRefreshMode = refreshMode;
    }

    public void create(int width, int height, EGLContext sharedContext) {
        if (!isEglReady)
            post(new GLEvent<>(GLEvent.EGL_CREATE, new GLEvent.EGLWindow(width, height, sharedContext)));
    }

    public void create(Surface surface, EGLContext sharedContext) {
        if (!isEglReady)
            post(new GLEvent<>(GLEvent.EGL_CREATE, new GLEvent.EGLWindow(surface, sharedContext)));
    }

    public void change(int width, int height) {
        post(new GLEvent<>(GLEvent.EGL_CHANGE, new GLEvent.Size(width, height)));
    }

    public void requestRender() {
        if (isEglReady && mRefreshMode == REFRESH_REQUEST) {
            post(new GLEvent(GLEvent.EGL_DRAW));
        }
    }

    public void destroyEGL() {
        if (isEglReady)
            post(new GLEvent<>(GLEvent.EGL_DESTROY, false));
    }

    public void quit() {
        post(new GLEvent<>(GLEvent.EGL_DESTROY, true));
    }

    private void post(GLEvent event) {
        mEventQueue.offer(event);
    }

    public EGLContext getEGLContext() {
        return mEGLEngine == null ? null : mEGLEngine.getEGLContext();
    }


    @Override
    public void run() {
        super.run();

        while (!isQuit) {
            try {
                GLEvent glEvent = mEventQueue.take();
                switch (glEvent.getEvent()) {

                    case GLEvent.EGL_CREATE:
                        logGL("GLThread创建");
                        //获取共享内容
                        GLEvent.EGLWindow eglWindow = (GLEvent.EGLWindow) glEvent.getData();
                        mEGLEngine = new EGLEngine();
                        if (null == eglWindow.getSurface()) {
                            mEGLEngine.initEGL(eglWindow.getWidth(), eglWindow.getHeight(), eglWindow.getEGLContext());
                        } else {
                            mEGLEngine.initEGL(eglWindow.getSurface(), eglWindow.getEGLContext());
                        }

                        if (null != mGLRender) mGLRender.onCreated();
                        isEglReady = true;
                        //当创建完成EGL环境后,开始绘制
                        post(new GLEvent(GLEvent.EGL_DRAW));

                        break;

                    case GLEvent.EGL_CHANGE:
                        if (null != mGLRender) {
                            GLEvent.Size size = (GLEvent.Size) glEvent.getData();
                            mGLRender.onChanged(size.getWidth(), size.getHeight());
                        }
                        break;

                    case GLEvent.EGL_DRAW:

                        if (!isEglReady) break;

                        if (null != mGLRender)
                            mGLRender.onDraw();
                        //交换显存
                        if (null != mEGLEngine)
                            mEGLEngine.swapBuffers();

                        if (mRefreshMode == REFRESH_AUTO) {
                            //自动模式,自动开启下一次刷新
                            sleep(mRefreshInterval);
                            post(new GLEvent(GLEvent.EGL_DRAW));
                        }

                        break;

                    case GLEvent.EGL_DESTROY:

                        if (null != mEGLEngine) {
                            logGL("GLThread销毁EGL环境");
                            mEGLEngine.destroyEGL();
                            mEGLEngine = null;
                            isEglReady = false;
                        }
                        isQuit = (boolean) glEvent.getData();

                        break;


                    default:
                        break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logGL("GLThread退出循环");
    }


    private void logGL(String log) {
        GLTools.logGL(log + "  [Thread:" + getId() + "]");
    }
}
