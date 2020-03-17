package com.mkleo.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.mkleo.encoder.base.BaseEncoder;
import com.mkleo.encoder.base.IMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class VideoEncoder extends BaseEncoder {

    public static class Config {
        int width = 720;
        int height = 1280;
        //比特率 argb
        int bitRate = width * height * 4;
        int fps = 20;
        int iFrameRate = 1;

        public Config setWidth(int width) {
            this.width = width;
            return this;
        }

        public Config setHeight(int height) {
            this.height = height;
            return this;
        }

        public Config setBitRate(int bitRate) {
            this.bitRate = bitRate;
            return this;
        }

        public Config setFps(int fps) {
            this.fps = fps;
            return this;
        }

        public Config setiFrameRate(int iFrameRate) {
            this.iFrameRate = iFrameRate;
            return this;
        }
    }

    private Config mConfig;
    //H264
    private static final String H264 = "video/avc";
//                = MediaFormat.MIMETYPE_VIDEO_AVC;
    //混合器
    private IMuxer mMuxer;
    //视屏轨道
    private int mVideoTrack;
    //视频源
    private Surface mVideoSurface;


    public VideoEncoder(@NonNull Config config, IMuxer muxer) {
        this.mConfig = config;
        this.mMuxer = muxer;
    }

    @Override
    protected MediaFormat onCreateMediaFormat() {
        MediaFormat videoFormat = MediaFormat.createVideoFormat(
                H264,
                mConfig.width,
                mConfig.height
        );
        //设置视频输入颜色格式，这里选择使用Surface作为输入，可以忽略颜色格式的问题，并且不需要直接操作输入缓冲区。
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //比特率 width * height * 4(argb)
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mConfig.bitRate);
        //帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mConfig.fps);
        //关键帧间隔
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mConfig.iFrameRate);
        return videoFormat;
    }

    @Override
    protected MediaCodec onCreateMediaCodec(MediaFormat format) throws IOException {
        MediaCodec videoCodec = MediaCodec.createEncoderByType(H264);
        videoCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mVideoSurface = videoCodec.createInputSurface();
        return videoCodec;
    }

    @Override
    protected void onOutputFormatChanged() {
        super.onOutputFormatChanged();
        mVideoTrack = mMuxer.addTrack(getMediaCodec().getOutputFormat());
    }

    @Override
    protected void onFrameCoding(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        outputBuffer.position(bufferInfo.offset);
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
        //写入采样数据
        mMuxer.writeSampleData(mVideoTrack, outputBuffer, bufferInfo);
    }

    @Override
    public void stop() {
        mMuxer.removeTrack();
        mMuxer = null;
        super.stop();
    }

    public Surface getVideoSurface() {
        return mVideoSurface;
    }
}
