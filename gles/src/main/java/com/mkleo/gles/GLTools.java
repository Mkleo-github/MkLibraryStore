package com.mkleo.gles;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLTools {


    /* float为32个2进制位 每个字节为8个二进制位 所以float是4个字节 */
    public static final int FLOAT_BYTES = 4;
    /* 同上 */
    public static final int SHORT_BYTES = 2;

    /**
     * 获取RAW文件脚本
     *
     * @param context
     * @param rawRes
     * @return
     */
    public static String getScript(Context context, int rawRes) {
        InputStream inputStream = context.getResources().openRawResource(rawRes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }


    /**
     * @param shaderType
     * @param script
     * @return
     * @throws GLException
     */
    private static int loadShader(int shaderType, String script) throws GLException {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader <= 0) {
            throw new GLException("Load shader failed!");
        }
        //加载脚本
        GLES20.glShaderSource(shader, script);
        //编译脚本
        GLES20.glCompileShader(shader);
        int[] compile = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
        if (compile[0] != GLES20.GL_TRUE) {
            //表示编译失败
            GLES20.glDeleteShader(shader);
            throw new GLException("Compile shader failed!");
        }
        return shader;
    }

    public static int linkProgram(String vertexScript, String fragmentScript) throws GLException {

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexScript);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentScript);

        if (vertexShader != 0 && fragmentShader != 0) {
            int program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            //检测链接状态
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program);
                throw new GLException("Link program failed!");
            }

            return program;
        }

        throw new GLException("Shader execption! Link program failed!");
    }


    public static FloatBuffer toFloatBuffer(float[] floats, int position) {
        FloatBuffer floatBuffer = ByteBuffer
                //开辟对应容量的缓冲
                .allocateDirect(FLOAT_BYTES * floats.length)
                //设置字节顺序为本地操作系统顺序
                .order(ByteOrder.nativeOrder())
                //设置缓冲类型
                .asFloatBuffer();
        //将数组中的数据送入缓冲
        floatBuffer.put(floats);
        //设置缓冲起始位置
        floatBuffer.position(position);
        return floatBuffer;
    }

    public static ShortBuffer toShortBuffer(short[] shorts, int position) {
        ShortBuffer shortBuffer = ByteBuffer
                .allocateDirect(SHORT_BYTES * shorts.length)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        shortBuffer.put(shorts);
        shortBuffer.position(position);
        return shortBuffer;
    }

    private static final String TAG = "MkGL";
    private static boolean sLogEnable = true;

    static void logGL(String log) {
        if (sLogEnable) Log.d(TAG, log);
    }

    public static void setLogEnable(boolean enable){
        sLogEnable = enable;
    }

}
