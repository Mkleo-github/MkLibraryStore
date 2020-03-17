package com.mkleo.camera1;

import android.graphics.ImageFormat;
import android.media.MediaRecorder;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public @interface Params {

    /**
     * 对焦模式
     */
    @IntDef({FocusMode.AUTO, FocusMode.FIXED, FocusMode.VIDEO, FocusMode.PICTURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface FocusMode {
        //自动对焦
        int AUTO = 0;
        //固定对焦
        int FIXED = 1;
        //视频模式
        int VIDEO = 2;
        //图片模式
        int PICTURE = 3;
    }

    @IntDef({Facing.BACK, Facing.FRONT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Facing {
        //后置
        int BACK = 0;
        //前置
        int FRONT = 1;
    }

    /**
     * 角度
     */
    @IntDef({Angle.ANGLE_0, Angle.ANGLE_90, Angle.ANGLE_180, Angle.ANGLE_270,})
    @Retention(RetentionPolicy.SOURCE)
    @interface Angle {
        int ANGLE_0 = 0;
        int ANGLE_90 = 90;
        int ANGLE_180 = 180;
        int ANGLE_270 = 270;
    }


    /**
     * 闪光灯
     */
    @IntDef({FlashMode.AUTO, FlashMode.OFF, FlashMode.ON, FlashMode.RED_EYE, FlashMode.TORCH})
    @Retention(RetentionPolicy.SOURCE)
    @interface FlashMode {
        //关闭
        int OFF = 0;
        //自动
        int AUTO = 1;
        //开启
        int ON = 2;
        //红眼
        int RED_EYE = 3;
        //火炬(常亮)
        int TORCH = 4;
    }


    /**
     * 图片格式
     */
    @IntDef({PictureFormat.JPEG, PictureFormat.RGB_565})
    @Retention(RetentionPolicy.SOURCE)
    @interface PictureFormat {
        int JPEG = ImageFormat.JPEG;
        int RGB_565 = ImageFormat.RGB_565;
    }


    /**
     * 音频来源(不全部支持)
     */
    @IntDef({AudioSource.MIC, AudioSource.DEFAULT, AudioSource.VOICE_CALL})
    @Retention(RetentionPolicy.SOURCE)
    @interface AudioSource {
        /* 默认音频源 */
        int DEFAULT = MediaRecorder.AudioSource.DEFAULT;
        /* 主麦克风 */
        int MIC = MediaRecorder.AudioSource.MIC;
        /* 设定录音来源于同方向的相机麦克风相同，若相机无内置相机或无法识别，则使用预设的麦克风  */
        int CAMCORDER = MediaRecorder.AudioSource.CAMCORDER;
        /* 设定录音来源为语音拨出的语音与对方说话的声音 */
        int VOICE_CALL = MediaRecorder.AudioSource.VOICE_CALL;
        /* 摄像头旁边的麦克风 */
        int VOICE_COMMUNICATION = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        /* 下行声音 */
        int VOICE_DOWNLINK = MediaRecorder.AudioSource.VOICE_DOWNLINK;
        /* 上行声音 */
        int VOICE_UPLINK = MediaRecorder.AudioSource.VOICE_UPLINK;
        /* 语音识别 */
        int VOICE_RECOGNITION = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    }


    /**
     * 视频来源
     */
    @IntDef({VideoSource.DEFAULT, VideoSource.CAMERA, VideoSource.SURFACE})
    @Retention(RetentionPolicy.SOURCE)
    @interface VideoSource {
        //默认
        int DEFAULT = MediaRecorder.VideoSource.DEFAULT;
        //相机
        int CAMERA = MediaRecorder.VideoSource.CAMERA;
        //纹理(Api21)
        int SURFACE = MediaRecorder.VideoSource.SURFACE;
    }


    /**
     * 输出格式
     */
    @IntDef({OutputFormat.MP4, OutputFormat.THREE_GPP, OutputFormat.DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    @interface OutputFormat {
        int DEFAULT = MediaRecorder.OutputFormat.DEFAULT;
        /* 输出格式 .mp4 .m4a */
        int MP4 = MediaRecorder.OutputFormat.MPEG_4;
        /* .aac  API 16以上 */
        int AAC_ADTS = MediaRecorder.OutputFormat.AAC_ADTS;
        /* .3gp */
        int AMR_NB = MediaRecorder.OutputFormat.AMR_NB;
        /* .3gp */
        int AMR_WB = MediaRecorder.OutputFormat.AMR_WB;
        /* .ts  API 26以上 */
        int MPEG_2_TS = MediaRecorder.OutputFormat.MPEG_2_TS;
        /* .3gp API 16 放弃 */
        @Deprecated
        int RAW_AMR = MediaRecorder.OutputFormat.RAW_AMR;
        /* .3gp */
        int THREE_GPP = MediaRecorder.OutputFormat.THREE_GPP;
        /* .ogg .mkv API 21以上 */
        int WEBM = MediaRecorder.OutputFormat.WEBM;
    }

    /**
     * 音频编码
     */
    @IntDef({AudioEncode.DEFAULT, AudioEncode.ACC, AudioEncode.VORBIS})
    @Retention(RetentionPolicy.SOURCE)
    @interface AudioEncode {

        int DEFAULT = MediaRecorder.AudioEncoder.DEFAULT;
        /* AAC（AAC低复杂度（AAC-LC）音频编解码器） */
        int ACC = MediaRecorder.AudioEncoder.AAC;
        /* AAC_ELD（增强型低延迟AAC（AAC-ELD）音频编解码器） API 16以上 */
        int AAC_ELD = MediaRecorder.AudioEncoder.AAC_ELD;
        /* AMR_NB（AMR（窄带）音频编解码器） */
        int AMR_NB = MediaRecorder.AudioEncoder.AMR_NB;
        /* AMR_WB（AMR（宽带）音频编解码器） */
        int AMR_WB = MediaRecorder.AudioEncoder.AMR_WB;
        /* HE_AAC（高效率AAC（HE-AAC）音频编解码器） API 16以上 */
        int HE_AAC = MediaRecorder.AudioEncoder.HE_AAC;
        /* VORBIS（Ogg Vorbis音频编解码器） API 21以上 */
        int VORBIS = MediaRecorder.AudioEncoder.VORBIS;
    }


    /**
     * 视频编码
     */
    @IntDef({VideoEncode.DEFAULT, VideoEncode.H264, VideoEncode.H263, VideoEncode.VP8})
    @Retention(RetentionPolicy.SOURCE)
    @interface VideoEncode {

        int DEFAULT = MediaRecorder.VideoEncoder.DEFAULT;
        /* 也是用于网络视频传输，优点也和H263差不多；再是H264会比前两者更优秀一点，不过一般用在标清或者高清压缩比较多。  */
        int H264 = MediaRecorder.VideoEncoder.H264;
        /* H.263 多用于视频传输，其优点是压缩后体积小，占用带宽少； */
        int H263 = MediaRecorder.VideoEncoder.H263;
        /* 码率低代表它无需高码率即可有很好的视频效果，H264就更好了  */
        int MPEG_4_SP = MediaRecorder.VideoEncoder.MPEG_4_SP;
        /* 据说比H264优秀。 */
        int VP8 = MediaRecorder.VideoEncoder.VP8;
        /* 一种新的视频压缩标准。可以替代H.264/ AVC编码标准。它将在H.264标准2至4倍的复杂度基础上，将压缩效率提升一倍以上。 */
        int HEVC = MediaRecorder.VideoEncoder.HEVC;
    }


}
