package com.mkleo.chat;

import android.os.Environment;


/**
 * 常量
 */
public class Constants {

    public static final String DELETE = "mkDel";       //删除表情

    /**
     * 格式
     */
    public static final class MEDIA_FORMAT {
        public static final String AUDIO_RECORD_FORMAT = ".amr";  //录音格式
    }


    /**
     * 默认路径
     */
    public static final class DefaultPath {
        public static final String PATH_GEN = Environment.getExternalStorageDirectory().getAbsolutePath(); // 根
        public static final String PATH_MK_PATH = PATH_GEN + "/MkChat";
        public static final String PATH_MK_MEDIA = PATH_MK_PATH + "/Media";
        public static final String PATH_AUDIO = PATH_MK_MEDIA + "/audio";
    }


    /**
     * 参数
     */
    public static final class ExtraKey {
        public static final String IS_VIDEO = "isVideo";
        public static final String MEDIA_PATH = "mediaPath";
    }

    /**
     * 录音状态
     */
    public enum RecordState {
        NORMAL,
        CACEL,
        FAIL
    }


}
