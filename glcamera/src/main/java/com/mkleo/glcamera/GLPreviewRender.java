package com.mkleo.glcamera;

import android.content.Context;
import android.opengl.GLES20;

import com.mkleo.gles.GLCreator;
import com.mkleo.gles.GLRender;
import com.mkleo.gles.GLTools;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/24
 */
public class GLPreviewRender implements GLRender {

    private Context mContext;
    /* 共享纹理 */
    private int mSharedTextureId;
    /* 顶点坐标 */
    private float[] mVertexCoord = GLCoords.VERTEX_COORD_DEF;
    /* 纹理坐标 */
    private float[] mTextureCoord = GLCoords.TEXTURE_COORD_ANDROID;
    /* 顶点缓冲 */
    private FloatBuffer mVertexBuffer;
    /* 纹理缓冲 */
    private FloatBuffer mTextureBuffer;
    /* OpenGL 程序指针 */
    private int mGLProgram;
    /* OpenGL 顶点坐标指针 */
    private int mGLVertexCoord;
    /* OpenGL 纹理坐标指针 */
    private int mGLTextureCoord;
    /* OpenGL 2D纹理指针 */
    private int mGL2DTexture;
    /* OpenGL VBO显存指针 */
    private int mGLVbo;

    private int mWidth;
    private int mHeight;

    //存储需要预览的水印
    private List<GLWatermark> mGLWatermarkList;

    public GLPreviewRender(Context context) {
        this(context, null);
    }

    public GLPreviewRender(Context context, List<GLWatermark> glWatermarks) {
        this.mContext = context;
        mGLWatermarkList = new ArrayList<>();
        if (null != glWatermarks)
            mGLWatermarkList.addAll(glWatermarks);
        this.init();
    }

    private void init() {
        //拓展当前定点坐标(包含了多个纹理)
        for (GLWatermark glWatermark : mGLWatermarkList) {
            mVertexCoord = glWatermark.expansion(mVertexCoord);
        }
        mVertexBuffer = GLTools.toFloatBuffer(mVertexCoord, 0);
        mTextureBuffer = GLTools.toFloatBuffer(mTextureCoord, 0);
    }

    public void setSharedTextureId(int sharedTextureId) {
        this.mSharedTextureId = sharedTextureId;
    }

    @Override
    public void onCreated() {

        //开启透明
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        String vertexScript = GLTools.getScript(mContext, R.raw.vertex_shader);
        String fragmentScript = GLTools.getScript(mContext, R.raw.fragment_shader);
        //完成链接程序
        mGLProgram = GLTools.linkProgram(vertexScript, fragmentScript);
        //获取顶点坐标系
        mGLVertexCoord = GLES20.glGetAttribLocation(mGLProgram, "vertexCoord");
        //获取纹理坐标系
        mGLTextureCoord = GLES20.glGetAttribLocation(mGLProgram, "textureCoord");
        //获取2D纹理
        mGL2DTexture = GLES20.glGetUniformLocation(mGLProgram, "texture");

        mGLVbo = GLCreator.newVBO()
                .add(mVertexCoord, mVertexBuffer)
                .add(mTextureCoord, mTextureBuffer)
                .create();

    }

    @Override
    public void onChanged(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void onDraw() {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mGLProgram);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mSharedTextureId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLVbo);

        GLES20.glEnableVertexAttribArray(mGLVertexCoord);
        GLES20.glVertexAttribPointer(
                mGLVertexCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                0);

        GLES20.glEnableVertexAttribArray(mGLTextureCoord);
        GLES20.glVertexAttribPointer(
                mGLTextureCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                mVertexCoord.length * GLTools.FLOAT_BYTES
        );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        for (GLWatermark glWatermark : mGLWatermarkList) {
            //生成纹理
            glWatermark.glGenTexture();
            //绘制纹理
            glWatermark.glDraw(mGLVertexCoord, mGLTextureCoord, mVertexCoord.length);
        }
        //解绑资源
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

}
