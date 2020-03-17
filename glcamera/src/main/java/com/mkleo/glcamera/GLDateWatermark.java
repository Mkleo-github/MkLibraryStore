package com.mkleo.glcamera;

import android.opengl.GLES20;

import com.mkleo.gles.GLCreator;
import com.mkleo.gles.GLTools;
import com.mkleo.gles.GLWindowMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/14
 */
public class GLDateWatermark extends GLWatermark {

    private GLWindowMapping.MappingArea mGLWatermarkArea;
    private int mGLWatermarkTextureId = -1;
    private long mNotesTime = -1;

    public GLDateWatermark(int windowWidth, int windowHeight) {
        super(windowWidth, windowHeight);
    }

    @Override
    protected GLWindowMapping.MappingArea glGenWatermarkArea() {
        mGLWatermarkArea = new GLWindowMapping.MappingArea(0, 0,
                320, 60);
        return mGLWatermarkArea;
    }

    /**
     * 更新纹理,会在线程中不断的刷新
     */
    @Override
    public void glGenTexture() {
        if (mNotesTime == -1 || System.currentTimeMillis() - mNotesTime > 999) {
            if (mGLWatermarkTextureId != -1)
                GLES20.glDeleteTextures(1, new int[]{mGLWatermarkTextureId}, 0);
            mNotesTime = System.currentTimeMillis();
            String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date());
            mGLWatermarkTextureId = GLCreator.newTexture().create(
                    glGenTextBitmap(
                            mGLWatermarkArea,
                            date,
                            0xffff0000,
                            0x00000000,
                            2
                    )
            );
        }
    }

    @Override
    public void glDraw(int glVertexCoord, int glTextureCoord, int vertexLength) {
        //绘制水印
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLWatermarkTextureId);

        GLES20.glEnableVertexAttribArray(glVertexCoord);
        GLES20.glVertexAttribPointer(
                glVertexCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                mOffset * GLTools.FLOAT_BYTES
        );

        GLES20.glEnableVertexAttribArray(glTextureCoord);
        GLES20.glVertexAttribPointer(
                glTextureCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                vertexLength * GLTools.FLOAT_BYTES
        );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
