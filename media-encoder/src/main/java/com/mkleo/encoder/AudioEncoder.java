package com.mkleo.encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.annotation.NonNull;

import com.mkleo.encoder.base.BaseEncoder;
import com.mkleo.encoder.base.IMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class AudioEncoder extends BaseEncoder {

    public static class Config {
        //采样率
        int sampleRate = 44100;
        //通道数
        int channelCount = 2;
        //比特率 fm96k
        int bitRate = 96 * 1000;

        public Config setBitRate(int bitRate) {
            this.bitRate = bitRate;
            return this;
        }

        public Config setChannelCount(int channelCount) {
            this.channelCount = channelCount;
            return this;
        }

        public Config setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }
    }


    private static final String AAC
            = "audio/mp4a-latm";
    //            = MediaFormat.MIMETYPE_AUDIO_AAC;
//    private static final int MAX_INPUT_SIZE = 4096;

    private Config mConfig;
    private IMuxer mMuxer;
    private int mAudioTrack;
    private boolean isPrepare = false;

    public AudioEncoder(@NonNull Config config, IMuxer muxer) {
        this.mConfig = config;
        this.mMuxer = muxer;
    }

    @Override
    protected MediaFormat onCreateMediaFormat() {
        MediaFormat audioFormat = MediaFormat.createAudioFormat(AAC, mConfig.sampleRate, mConfig.channelCount);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, mConfig.bitRate);
        int maxInputSize = AudioRecord.getMinBufferSize(
                mConfig.sampleRate,
                mConfig.channelCount,
                AudioFormat.ENCODING_PCM_16BIT
        ) * 2;
        //设置输入的最大值,如果buffer大小大于改值将会导致溢出
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxInputSize);
        return audioFormat;
    }

    @Override
    protected MediaCodec onCreateMediaCodec(MediaFormat mediaFormat) throws IOException {
        MediaCodec audioCodec = MediaCodec.createEncoderByType(AAC);
        audioCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        return audioCodec;
    }

    @Override
    protected void onOutputFormatChanged() {
        super.onOutputFormatChanged();
        mAudioTrack = mMuxer.addTrack(getMediaCodec().getOutputFormat());
        isPrepare = true;
    }

    @Override
    protected void onFrameCoding(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        outputBuffer.position(bufferInfo.offset);
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
        int pcmSize = bufferInfo.size + 7;
        byte[] pcmBuffer = new byte[pcmSize];
        outputBuffer.position(bufferInfo.offset);
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
        //添加adts头
        int accSampleRate = getADTS_SampleRate(mConfig.sampleRate);
        addADTS_Header(pcmBuffer, pcmSize, accSampleRate);
        outputBuffer.get(pcmBuffer, 7, bufferInfo.size);
        outputBuffer.position(bufferInfo.offset);
        //写入采样数据
        mMuxer.writeSampleData(mAudioTrack, outputBuffer, bufferInfo);
    }

    @Override
    public void stop() {
        super.stop();
        mMuxer.removeTrack();
        mMuxer = null;
    }

    /**
     * 推入PCM数据
     *
     * @param data
     * @param size
     */
    public void putPcmData(final byte[] data, final int size) {
        synchronized (getSyncLock()) {
            if (isEncoding() && isPrepare) {
                int inputIndex = getMediaCodec().dequeueInputBuffer(0);
                if (inputIndex >= 0) {
                    ByteBuffer byteBuffer = getMediaCodec().getInputBuffers()[inputIndex];
                    byteBuffer.clear();
                    byteBuffer.put(data);
                    getMediaCodec().queueInputBuffer(inputIndex, 0, size, 0, 0);
                }
            }
        }
    }

    /**
     * 添加ADTS头
     *
     * @param data
     * @param length
     * @param accSampleRate
     */
    private void addADTS_Header(byte[] data, int length, int accSampleRate) {
        int profile = 2; // AAC LC
        int freqIdx = accSampleRate; // samplerate
        int chanCfg = 2; // CPE

        data[0] = (byte) 0xFF; // 0xFFF(12bit) 这里只取了8位，所以还差4位放到下一个里面
        data[1] = (byte) 0xF9; // 第一个t位放F
        data[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        data[3] = (byte) (((chanCfg & 3) << 6) + (length >> 11));
        data[4] = (byte) ((length & 0x7FF) >> 3);
        data[5] = (byte) (((length & 7) << 5) + 0x1F);
        data[6] = (byte) 0xFC;
    }

    /**
     * 获取ADTS采样率标记
     *
     * @param sampleRate
     * @return
     */
    private int getADTS_SampleRate(int sampleRate) {
        int rate = 4;
        switch (sampleRate) {
            case 96000:
                rate = 0;
                break;
            case 88200:
                rate = 1;
                break;
            case 64000:
                rate = 2;
                break;
            case 48000:
                rate = 3;
                break;
            case 44100:
                rate = 4;
                break;
            case 32000:
                rate = 5;
                break;
            case 24000:
                rate = 6;
                break;
            case 22050:
                rate = 7;
                break;
            case 16000:
                rate = 8;
                break;
            case 12000:
                rate = 9;
                break;
            case 11025:
                rate = 10;
                break;
            case 8000:
                rate = 11;
                break;
            case 7350:
                rate = 12;
                break;
        }
        return rate;
    }


}
