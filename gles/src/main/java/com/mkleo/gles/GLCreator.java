package com.mkleo.gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.annotation.NonNull;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/23
 */
public class GLCreator {

    public static VBOCreator newVBO() {
        return new VBOCreator();
    }

    public static FBOCreator newFBO(int textureId) {
        return new FBOCreator(textureId);
    }

    public static TextureCreator newTexture() {
        return new TextureCreator();
    }


    /**
     * 顶点缓冲对象
     */
    public static class VBOCreator {

        private static class VBOData {
            private float[] data;
            private FloatBuffer dataBuffer;

            private VBOData(float[] data, FloatBuffer dataBuffer) {
                this.data = data;
                this.dataBuffer = dataBuffer;
            }
        }

        private List<VBOData> mVBODatas;

        private VBOCreator() {
            mVBODatas = new ArrayList<>();
        }

        public VBOCreator add(@NonNull float[] data, @NonNull FloatBuffer dataBuffer) {
            mVBODatas.add(new VBOData(data, dataBuffer));
            return this;
        }

        public int create() {
            //无法创建
            if (mVBODatas.size() == 0) return -1;
            //OpenGL vbo指针
            int glVbo;
            int[] vbos = new int[1];
            //创建VBO
            GLES20.glGenBuffers(1, vbos, 0);
            glVbo = vbos[0];
            //绑定VBO
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glVbo);
            //分配缓存大小
            //获取所有点数据个数
            int positions = 0;
            for (VBOData vboData : mVBODatas) {
                positions += vboData.data.length;
            }
            GLES20.glBufferData(
                    GLES20.GL_ARRAY_BUFFER,
                    //所有点所占用的字节数
                    positions * GLTools.FLOAT_BYTES,
                    //初始化数据
                    null,
                    GLES20.GL_STATIC_DRAW
            );
            //为顶点缓冲设置数据
            int offset = 0;
            for (VBOData vboData : mVBODatas) {
                GLES20.glBufferSubData(
                        GLES20.GL_ARRAY_BUFFER,
                        //偏移值
                        offset,
                        //数据长度
                        vboData.data.length * GLTools.FLOAT_BYTES,
                        vboData.dataBuffer
                );
                offset += vboData.data.length * GLTools.FLOAT_BYTES;
            }
            //解绑
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            return glVbo;
        }

    }


    /**
     * 帧缓冲对象
     */
    public static class FBOCreator {

        private int mTextureId;

        private FBOCreator(int textureId) {
            this.mTextureId = textureId;
        }

        public int create(int width, int height) {
            if (width <= 0 || height <= 0) throw new GLException("Unsupport size!");
            int glFbo;
            //创建FBO
            int[] fbos = new int[1];
            GLES20.glGenBuffers(1, fbos, 0);
            glFbo = fbos[0];
            //绑定FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFbo);
            //激活绑定的纹理(一定要在内存分配之前)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            //设置FBO分配内存大小
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA,
                    width,
                    height,
                    0,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    null
            );
            //把纹理绑定到FBO
            GLES20.glFramebufferTexture2D(
                    GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D,
                    mTextureId,
                    0
            );

            //检测是否创建FBO成功
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                GLTools.logGL("FBO创建失败!");
            } else {
                GLTools.logGL("FBO创建成功!");
            }
            //解绑纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            //解绑FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            return glFbo;
        }

    }

    /**
     * 纹理创建
     */
    public static class TextureCreator {
        private TextureCreator() {
        }

        /**
         * 生成纹理
         *
         * @param res 资源文件
         * @return
         */
        public int create(Context context, int res) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res);
            return create(bitmap);
        }


        /**
         * 创建bitmap贴图纹理
         *
         * @param bitmap
         * @return
         */
        public int create(Bitmap bitmap) {
            if (null == bitmap) throw new GLException("Bitmap为Null!");
            int[] textureIds = new int[1];
            GLES20.glGenTextures(1, textureIds, 0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
            //设置环绕
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //设置过滤
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //创建buffer(argb)
            IntBuffer bitmapBuffer = IntBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
            bitmap.copyPixelsToBuffer(bitmapBuffer);
            bitmapBuffer.flip();

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(),
                    bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmapBuffer);

//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            bitmap.recycle();
            return textureIds[0];
        }


        /**
         * 生成纹理
         *
         * @return
         */
        public int create() {
            int[] textureIds = new int[1];
            GLES20.glGenTextures(1, textureIds, 0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
            //设置环绕
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //设置过滤
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            return textureIds[0];
        }


        /**
         * 创建OES纹理
         *
         * @return
         */
        public int createOes() {
            int[] textureIds = new int[1];
            GLES20.glGenTextures(1, textureIds, 0);
            //绑定Oes纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
            //设置环绕
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //设置过滤
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //解绑
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            return textureIds[0];
        }
    }


}
