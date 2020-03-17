package com.mkleo.gles;

import android.opengl.EGL14;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/12
 */
public class GLException extends RuntimeException {

    public GLException(String message) {
        super(GLException.glError(message));
    }

    private static String glError(String msg) {
        int eglErrorCode = EGL14.eglGetError();
        return new StringBuilder()
                .append("EGLError:").append(eglErrorCode).append("\r\n")
                .append(msg)
                .toString();
    }

}
