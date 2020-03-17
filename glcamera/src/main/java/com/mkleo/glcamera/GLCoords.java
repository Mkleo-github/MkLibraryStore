package com.mkleo.glcamera;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/23
 */
public class GLCoords {

    /* 目前的坐标都是二维的,每个点由两个维度(X,Y)表示 */
    public static final int DIMENSION = 2;

    /* 所有的坐标顺序按照默认顶点坐标为准 采用GLES20.GL_TRIANGLE_STRIP */
    /* 默认 顶点坐标 */
    public static final float[] VERTEX_COORD_DEF = {
//            -1f, -1f,
//            1f, -1f,
//            -1f, 1f,
//            1f, 1f,

            -1f, 1f,
            1f, 1f,
            -1f, -1f,
            1f, -1f,
    };

    /**
     * 安卓显示屏纹理坐标系
     * 0 → 1
     * ↓
     * 1
     */
    public static final float[] TEXTURE_COORD_ANDROID = {
//            0f, 1f,
//            1f, 1f,
//            0f, 0f,
//            1f, 0f,

            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f,
    };


    /**
     * FBO 纹理坐标
     * 1
     * ↑
     * 0 → 1
     */
    public static final float[] TEXTURE_COORD_FBO = {
//            0f, 0f,
//            1f, 0f,
//            0f, 1f,
//            1f, 1f,

            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

}
