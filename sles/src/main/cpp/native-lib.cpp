#include <jni.h>
#include <string>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

//OpenSL ES 引擎接口
SLObjectItf slEngineItf = NULL;
//OpenSL ES 引擎实体
SLEngineItf slEngine = NULL;

//音频录制接口
SLObjectItf slRecordItf = NULL;
//音频录制实体
SLRecordItf slRecord = NULL;


extern "C"
JNIEXPORT void JNICALL
Java_com_mkleo_sles_MainActivity_startRecord(JNIEnv *env, jobject thiz, jstring path) {

    //初始化
    slCreateEngine(&slEngineItf, 0, NULL, 0, NULL, NULL);
    //实现
    (*slEngineItf)->Realize(slEngineItf, SL_BOOLEAN_FALSE);
    //获得引擎实体
    (*slEngineItf)->GetInterface(slEngineItf, SL_IID_ENGINE, &slEngine);


    //获取音频输入设备如:麦克风
    SLDataLocator_IODevice locatorIoDevice = {
            SL_DATALOCATOR_IODEVICE,        //
            SL_IODEVICE_AUDIOINPUT,         //
            SL_DEFAULTDEVICEID_AUDIOINPUT,  //
            NULL
    };
    //音频录制配置
    SLDataSource recrodOpt = {
            &locatorIoDevice,
            NULL
    };


    SLDataLocator_AndroidSimpleBufferQueue locatorAndroidSimpleBufferQueue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
            2,

    };

    SLDataFormat_PCM formatPcm = {
            SL_DATAFORMAT_PCM,
            2,
            SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN     //对齐,小段对齐 (小段在前)
    };
    //PCM属性
    SLDataSink audioSink = {
            &locatorAndroidSimpleBufferQueue,
            &formatPcm
    };

    const int size = 1;

    SLInterfaceID id[size] = {
            SL_IID_ANDROIDSIMPLEBUFFERQUEUE
    };

    SLboolean required[size] = {
            SL_BOOLEAN_TRUE
    };

    (*slEngine)->CreateAudioRecorder(
            slEngine,
            &slRecordItf,
            &recrodOpt,
            &audioSink,
            size,
            id,
            required
    );

}

extern "C"
JNIEXPORT void JNICALL
Java_com_mkleo_sles_MainActivity_stopRecord(JNIEnv *env, jobject thiz) {
    // TODO: implement stopRecord()
}