package com.mkleo.encoder.base;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * 混合器
 */
public interface IMuxer {

    /**
     * 添加轨道
     *
     * @param format
     * @return
     */
    int addTrack(MediaFormat format);

    /**
     * 移除轨道
     */
    void removeTrack();

    /**
     * 写入采样数据
     *
     * @param track
     * @param buffer
     * @param bufferInfo
     */
    void writeSampleData(int track, ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo);

}
