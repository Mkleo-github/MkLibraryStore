package com.mkleo.glcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.mkleo.gles.GLCreator;
import com.mkleo.gles.GLRender;
import com.mkleo.gles.GLTools;

import java.nio.FloatBuffer;

/**
 * des:Oes纹理渲染(FBO离屏渲染)
 * by: Mk.leo
 * date: 2019/4/24
 */
public class GLOesRender implements GLRender, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private int mWidth;
    private int mHeight;

    private float[] mVertexCoord = GLCoords.VERTEX_COORD_DEF;
    private float[] mTextureCoord = GLCoords.TEXTURE_COORD_FBO;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    /* OpenGL 程序指针 */
    private int mGLProgram;
    /* OpenGL 顶点坐标指针 */
    private int mGLVertexCoord;
    /* OpenGL 纹理坐标指针 */
    private int mGLTextureCoord;
    /* OpenGL 变换矩阵指针 */
    private int mGLTransMatrix;
    /* OpenGL 2D纹理指针 */
    private int mGLOesTexture;
    /* OpenGL VBO指针 */
    private int mGLVbo;
    /* OpenGL FBO指针 */
    private int mGLFbo;
    /* FBO纹理ID */
    private int mGLFboTextureId;
    /* 相机OES纹理ID */
    private int mGLOesTextureId;
    /* OES纹理实例 */
    private SurfaceTexture mOesTexture;
    /* 变换矩阵数组 */
    private float[] mTransMatrix = new float[16];

    private OnRenderCallback mOnRenderCallback;


    public void setOnRenderCallback(OnRenderCallback onRenderCallback) {
        this.mOnRenderCallback = onRenderCallback;
    }

    public interface OnRenderCallback {
        void onCreateTexture(SurfaceTexture surfaceTexture);
    }

    public GLOesRender(Context context, int width, int height) {
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.init();
    }

    private void init() {
        //初始化变换矩阵
        Matrix.setIdentityM(mTransMatrix, 0);
        //分配内存
        mVertexBuffer = GLTools.toFloatBuffer(mVertexCoord, 0);
        mTextureBuffer = GLTools.toFloatBuffer(mTextureCoord, 0);
    }


    @Override
    public void onCreated() {

        String vertexScript = GLTools.getScript(mContext, R.raw.vertex_shader_tarnser);
        String fragmentScript = GLTools.getScript(mContext, R.raw.fragment_shader_oes);
        //完成链接程序
        mGLProgram = GLTools.linkProgram(vertexScript, fragmentScript);
        //获取顶点坐标系
        mGLVertexCoord = GLES20.glGetAttribLocation(mGLProgram, "vertexCoord");
        //获取纹理坐标系
        mGLTextureCoord = GLES20.glGetAttribLocation(mGLProgram, "textureCoord");
        //获取2D纹理
        mGLOesTexture = GLES20.glGetUniformLocation(mGLProgram, "oesTexture");
        //获取变换矩阵
        mGLTransMatrix = GLES20.glGetUniformLocation(mGLProgram, "transMatrix");

        //创建VBO
        mGLVbo = GLCreator.newVBO()
                .add(mVertexCoord, mVertexBuffer)
                .add(mTextureCoord, mTextureBuffer)
                .create();
        //创建FBO纹理
        mGLFboTextureId = GLCreator.newTexture().create();
        //创建FBO
        mGLFbo = GLCreator.newFBO(mGLFboTextureId)
                .create(mWidth, mHeight);
        //创建Oes纹理
        mGLOesTextureId = GLCreator.newTexture().createOes();
        //创建OES纹理实例
        mOesTexture = new SurfaceTexture(mGLOesTextureId);
        mOesTexture.setOnFrameAvailableListener(this);

        if (null != mOnRenderCallback)
            mOnRenderCallback.onCreateTexture(mOesTexture);
    }

    @Override
    public void onChanged(int width, int height) {
        //FBO纹理不随屏幕变化发生旋转
    }

    @Override
    public void onDraw() {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        //刷新texture
        mOesTexture.updateTexImage();
        //绑定后之后的操作不会显示在屏幕上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mGLFbo);
        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //激活程序
        GLES20.glUseProgram(mGLProgram);
        //激活变换矩阵
        GLES20.glUniformMatrix4fv(mGLTransMatrix, 1, false, mTransMatrix, 0);
        //绑定源纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLOesTextureId);
        //绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLVbo);

        GLES20.glEnableVertexAttribArray(mGLVertexCoord);
        GLES20.glVertexAttribPointer(mGLVertexCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                0
        );

        GLES20.glEnableVertexAttribArray(mGLTextureCoord);
        GLES20.glVertexAttribPointer(mGLTextureCoord,
                GLCoords.DIMENSION,
                GLES20.GL_FLOAT,
                false,
                GLCoords.DIMENSION * GLTools.FLOAT_BYTES,
                mVertexCoord.length * GLTools.FLOAT_BYTES
        );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
    }

    public void resetMatrix() {
        Matrix.setIdentityM(mTransMatrix, 0);
    }

    public void setAngle(float angle) {
        Matrix.rotateM(mTransMatrix, 0, angle, 0, 0, 1);
    }

    public void setMirror() {
        Matrix.rotateM(mTransMatrix, 0, 180, 0, 1, 0);
    }

    public SurfaceTexture getOesTexture() {
        return mOesTexture;
    }

    public int getGLFboTextureId() {
        return mGLFboTextureId;
    }
}
