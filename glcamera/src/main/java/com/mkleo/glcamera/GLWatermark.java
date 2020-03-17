package com.mkleo.glcamera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.mkleo.gles.GLWindowMapping;
import com.mkleo.helper.GraphUtil;

import java.util.Arrays;

/**
 * des:水印
 * by: Mk.leo
 * date: 2019/5/13
 */
public abstract class GLWatermark implements Cloneable {

    /* OpenGL 窗口映射 */
    private GLWindowMapping mGLWindowMapping;
    /* 偏移量 */
    protected int mOffset;
    /* 宽高 */
    protected int mWindowWidth;
    protected int mWindowHeight;
    /* 是否已经扩容 */
    private boolean isExpansion = false;


    public GLWatermark(int windowWidth, int windowHeight) {
        this.mWindowWidth = windowWidth;
        this.mWindowHeight = windowHeight;
        this.mGLWindowMapping = new GLWindowMapping(windowWidth, windowHeight);
    }


    /**
     * 扩容
     *
     * @param vertexCoords
     */
    public synchronized float[] expansion(float[] vertexCoords) {
        if (isExpansion) throw new RuntimeException("只能扩容一次!");
        isExpansion = true;
        mOffset = vertexCoords.length;
        vertexCoords = Arrays.copyOf(vertexCoords, mOffset + 4 * 2);
        GLWindowMapping.MappingArea watermarkArea = glGenWatermarkArea();
        float[] watermarkCoords = mGLWindowMapping.glMappingVertexCoords(watermarkArea);
        for (int i = mOffset; i < vertexCoords.length; i++) {
            vertexCoords[i] = watermarkCoords[i - mOffset];
        }
        return vertexCoords;
    }

    /**
     * 水印区域
     *
     * @return
     */
    protected abstract GLWindowMapping.MappingArea glGenWatermarkArea();

    public abstract void glGenTexture();

    public abstract void glDraw(int glVertexCoord, int glTextureCoord,int vertexLength);


    public Bitmap glGenTextBitmap(
            GLWindowMapping.MappingArea drawArea,
            String text,
            int textColor, int backgroundColor, int padding) {

        int width = (int) drawArea.getWidth();
        int height = (int) drawArea.getHeight();
        //文字宽度
        int textSize = height / 2 - padding * 2;
        //创建画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        GraphUtil.drawTextCenter(text, 0, 0, width, height, paint, canvas);
        return bitmap;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
