package com.mkleo.gles;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLException;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/26
 */
public class EGLHelper {

    public static final int OPENGL_ES_VERSION_2 = 2;
    public static final int TYPE_PBUFFER = 1 << 2;
    public static final int TYPE_WINDOW = 2 << 2;

    public static EGLDisplay getEGLDisplay() throws GLException {
        // 获取显示设备(默认的显示设备)
        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        // 初始化
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new GLException(EGL14.eglGetError(), "EGLDisplay初始化失败");
        }
        return eglDisplay;
    }

    public static EGLConfig getEGLConfig(int[] configAttribs, EGLDisplay eglDisplay) throws GLException {
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0,
                configs.length, numConfigs, 0)) {
            throw new GLException(EGL14.eglGetError(), "EGLConfig初始化失败");
        }
        return configs[0];
    }

    public static EGLConfig getEGLConfig(EGLDisplay eglDisplay) throws GLException {
        // 获取FrameBuffer格式和能力
        int[] configAttribs = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        return getEGLConfig(configAttribs, eglDisplay);
    }

    public static EGLContext getEGLContext(int version, EGLDisplay eglDisplay, EGLConfig eglConfig, EGLContext eglContext)
            throws GLException {
        // 创建OpenGL上下文(可以先不设置EGLSurface，但EGLContext必须创建，
        // 因为后面调用GLES方法基本都要依赖于EGLContext)
        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, version,
                EGL14.EGL_NONE
        };
        EGLContext result = EGL14.EGL_NO_CONTEXT;
        result = EGL14.eglCreateContext(eglDisplay, eglConfig, eglContext,
                contextAttribs, 0);
        if (result == EGL14.EGL_NO_CONTEXT) {
            throw new GLException(EGL14.eglGetError(), "EGLContext初始化失败");
        }

        return result;
    }

    public static EGLContext getEGLContext(EGLDisplay eglDisplay, EGLConfig eglConfig, EGLContext eglContext) throws GLException {
        return getEGLContext(OPENGL_ES_VERSION_2, eglDisplay, eglConfig, eglContext);
    }


    public static EGLContext getEGLContext(EGLDisplay eglDisplay, EGLConfig eglConfig) throws GLException {
        return getEGLContext(OPENGL_ES_VERSION_2, eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT);
    }

    public static <T> EGLSurface getEGLSurface(int type, int[] attributes,
                                               T surface,
                                               EGLDisplay eglDisplay,
                                               EGLConfig eglConfig) {
        EGLSurface eglSurface = null;
        switch (type) {
            case TYPE_PBUFFER: {
                eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig,
                        attributes, 0);
                break;
            }
            case TYPE_WINDOW: {
                eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig,
                        surface, attributes, 0);
                break;
            }
            default:
                break;
        }

        return eglSurface;
    }

    public static EGLSurface getEGLSurface(int[] attributes,
                                           EGLDisplay eglDisplay,
                                           EGLConfig eglConfig) {
        return getEGLSurface(TYPE_PBUFFER, attributes, null, eglDisplay, eglConfig);
    }

    public static <T> EGLSurface getEGLSurface(int[] attributes,
                                               T surface,
                                               EGLDisplay eglDisplay,
                                               EGLConfig eglConfig) {
        return getEGLSurface(TYPE_WINDOW, attributes, surface, eglDisplay, eglConfig);
    }

    public static void queryContext(EGLDisplay eglDisplay, EGLContext eglContext) {
        int[] values = new int[1];
        EGL14.eglQueryContext(eglDisplay, eglContext,
                EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0);
    }

    public static void makeCurrent(EGLDisplay eglDisplay, EGLContext eglContext) {
        makeCurrent(eglDisplay, eglContext, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE);
    }

    public static void makeCurrent(EGLDisplay eglDisplay, EGLContext eglContext,
                                   EGLSurface eglSurface) {
        makeCurrent(eglDisplay, eglContext, eglSurface, eglSurface);
    }

    /**
     * 激活EGL环境
     *
     * @param eglDisplay
     * @param eglContext
     * @param drawSurface
     * @param readSurface
     */
    public static void makeCurrent(EGLDisplay eglDisplay, EGLContext eglContext,
                                   EGLSurface drawSurface, EGLSurface readSurface) {
        boolean isSuccess = EGL14.eglMakeCurrent(eglDisplay, drawSurface, readSurface, eglContext);
        if (!isSuccess) throw new GLException(GLES20.glGetError(), "MakeCurrent失败!");
    }
}
