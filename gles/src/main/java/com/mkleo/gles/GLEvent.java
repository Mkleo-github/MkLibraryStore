package com.mkleo.gles;

import android.opengl.EGLContext;
import android.view.Surface;


/**
 * des:
 * by: Mk.leo
 * date: 2019/4/17
 */
public class GLEvent<T> {

    public static final int EGL_CREATE = 0;
    public static final int EGL_CHANGE = 1;
    public static final int EGL_DRAW = 2;
    public static final int EGL_DESTROY = 3;

    private int event;
    private T data;

    public GLEvent(int event) {
        this(event, null);
    }

    public GLEvent(int event, T data) {
        this.event = event;
        this.data = data;
    }

    public int getEvent() {
        return event;
    }

    public T getData() {
        return data;
    }


    /**
     * 共享EGL
     */
    public static class EGLWindow {

        private int width;
        private int height;
        private Surface surface;
        private EGLContext eglContext;

        /**
         * 离屏模式
         *
         * @param width
         * @param height
         * @param eglContext
         */
        public EGLWindow(int width, int height, EGLContext eglContext) {
            this.width = width;
            this.height = height;
            this.eglContext = eglContext;
        }

        /**
         * 窗口模式
         *
         * @param surface
         * @param eglContext
         */
        public EGLWindow(Surface surface, EGLContext eglContext) {
            this.surface = surface;
            this.eglContext = eglContext;
        }

        public EGLContext getEGLContext() {
            return eglContext;
        }

        public Surface getSurface() {
            return surface;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }


    /**
     * 窗口变化
     */
    public static class Size {

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
