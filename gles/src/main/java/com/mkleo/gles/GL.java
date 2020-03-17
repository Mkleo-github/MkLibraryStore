package com.mkleo.gles;

import android.opengl.GLES20;
import android.support.annotation.IntDef;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/12
 */
public @interface GL {


    @IntDef({
            /* 每一个顶点在屏幕上都是单独的点 */
            GLES20.GL_POINTS,
            /* 每一对顶点都定义了一条线段 */
            GLES20.GL_LINES,
            /* 线带,连续依次绘制的线段 */
            GLES20.GL_LINE_STRIP,
            /* 连续一次绘制线段并首尾相连 */
            GLES20.GL_LINE_LOOP,
            /* 每三个顶点对应一个三角形 */
            GLES20.GL_TRIANGLES,
            /* 共用一个顶点绘制三角形(前一个三角形的最后一个点和后一个三角形的第一个点共用) */
            GLES20.GL_TRIANGLE_STRIP,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DrawMode {

    }

}
