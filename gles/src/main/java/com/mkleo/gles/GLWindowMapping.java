package com.mkleo.gles;

import android.opengl.GLES20;

import java.util.Arrays;

/**
 * des:OpenGL 窗口映射
 * by: Mk.leo
 * date: 2019/5/12
 */
public class GLWindowMapping {

    private final int mWindowWidth;
    private final int mWindowHeight;


    public GLWindowMapping(int windowWidth, int windowHeight) {
        if (windowWidth == 0 || windowHeight == 0)
            throw new RuntimeException("无法创建映射空间坐标");
        this.mWindowHeight = windowHeight;
        this.mWindowWidth = windowWidth;
    }

    /**
     * 默认的映射方法,采用GLES20.GL_TRIANGLE_STRIP绘制方式
     *
     * @param mappingArea
     * @return
     */
    public float[] glMappingVertexCoords(MappingArea mappingArea) {
        return this.glMappingVertexCoords(GLES20.GL_TRIANGLE_STRIP, mappingArea);
    }


    /**
     * 映射为顶点坐标系
     *
     * @param mode
     * @param mappingArea
     * @return
     */
    public float[] glMappingVertexCoords(@GL.DrawMode int mode, MappingArea mappingArea) {
        //顺时针
        MappingArea.Position p1 = mappingArea.getLeftTop();
        MappingArea.Position p2 = mappingArea.getRightTop();
        MappingArea.Position p3 = mappingArea.getRightBottom();
        MappingArea.Position p4 = mappingArea.getLeftBottom();

        float[] vertexCoords;

        if (mode == GLES20.GL_TRIANGLE_STRIP) {
            vertexCoords = new float[]{
                    glVertexCoordX(p1.x), glVertexCoordY(p1.y),
                    glVertexCoordX(p2.x), glVertexCoordY(p2.y),
                    glVertexCoordX(p4.x), glVertexCoordY(p4.y),
                    glVertexCoordX(p3.x), glVertexCoordY(p3.y),
            };
        } else {
            vertexCoords = new float[]{
                    glVertexCoordX(p1.x), glVertexCoordY(p1.y),
                    glVertexCoordX(p2.x), glVertexCoordY(p2.y),
                    glVertexCoordX(p3.x), glVertexCoordY(p3.y),
                    glVertexCoordX(p4.x), glVertexCoordY(p4.y),
            };
        }
        GLTools.logGL("转换为OpenGL归一化顶点坐标:" + Arrays.toString(vertexCoords));
        return vertexCoords;
    }

    /**
     * 转换成GL归一化顶点坐标X
     *
     * @param x
     * @return
     */
    private float glVertexCoordX(float x) {
        //中心X坐标(对应GL坐标系X 0点)
        x = Math.min(x, mWindowWidth);
        final float midX = mWindowWidth / 2f;
        return (x - midX) / midX;
    }

    /**
     * 转换成GL归一化顶点坐标Y
     *
     * @param y
     * @return
     */
    private float glVertexCoordY(float y) {
        //中心Y坐标(对应GL坐标系Y 0点)
        y = Math.min(y, mWindowHeight);
        final float midY = mWindowHeight / 2f;
        return (midY - y) / midY;
    }


    /**
     * 映射为纹理坐标
     *
     * @param mappingArea
     * @return
     */
    public float[] glMappingTextureCoords(MappingArea mappingArea) {
        return this.glMappingTextureCoords(GLES20.GL_TRIANGLE_STRIP, mappingArea);
    }

    /**
     * 映射为纹理坐标
     *
     * @param mode
     * @param mappingArea
     * @return
     */
    public float[] glMappingTextureCoords(@GL.DrawMode int mode, MappingArea mappingArea) {
        //顺时针
        MappingArea.Position p1 = mappingArea.getLeftTop();
        MappingArea.Position p2 = mappingArea.getRightTop();
        MappingArea.Position p3 = mappingArea.getRightBottom();
        MappingArea.Position p4 = mappingArea.getLeftBottom();

        float[] textureCoords;

        if (mode == GLES20.GL_TRIANGLE_STRIP) {
            textureCoords = new float[]{
                    glTextureCoordX(p1.x), glTextureCoordY(p1.y),
                    glTextureCoordX(p2.x), glTextureCoordY(p2.y),
                    glTextureCoordX(p4.x), glTextureCoordY(p4.y),
                    glTextureCoordX(p3.x), glTextureCoordY(p3.y),
            };
        } else {
            textureCoords = new float[]{
                    glTextureCoordX(p1.x), glTextureCoordY(p1.y),
                    glTextureCoordX(p2.x), glTextureCoordY(p2.y),
                    glTextureCoordX(p3.x), glTextureCoordY(p3.y),
                    glTextureCoordX(p4.x), glTextureCoordY(p4.y),
            };
        }
        GLTools.logGL("转换为OpenGL归一化纹理坐标:" + Arrays.toString(textureCoords));
        return textureCoords;
    }

    /**
     * 获得纹理坐标x
     *
     * @param x
     * @return
     */
    private float glTextureCoordX(float x) {
        return Math.min(x, mWindowWidth) / mWindowWidth;
    }

    /**
     * 获得纹理坐标y
     *
     * @param y
     * @return
     */
    private float glTextureCoordY(float y) {
        return Math.min(y, mWindowHeight) / mWindowHeight;
    }


    /**
     * 映射区域
     */
    public static class MappingArea {

        private float left;
        private float top;
        private float right;
        private float bottom;
        private float width;
        private float height;


        public MappingArea(float left, float top, float width, float height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
            this.right = left + width;
            this.bottom = top + height;
        }

        public float getLeft() {
            return left;
        }

        public float getTop() {
            return top;
        }

        public float getBottom() {
            return bottom;
        }

        public float getRight() {
            return right;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        private Position getLeftTop() {
            return new Position(left, top);
        }

        private Position getRightTop() {
            return new Position(right, top);
        }

        private Position getLeftBottom() {
            return new Position(left, bottom);
        }

        private Position getRightBottom() {
            return new Position(right, bottom);
        }


        private static class Position {
            private final float x;
            private final float y;

            private Position(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }

    }


}
