package com.mkleo.camera;

import android.content.Context;

import com.mkleo.bases.sensor.DirectionSensor;
import com.mkleo.helper.HandlerScheduler;

public abstract class BaseCameraApi implements ICamera {

    /* 上下文 */
    protected Context mContext;
    /* 配置项 */
    protected Config mConfig;
    /* 设备旋转角度 */
    protected int mDeviceAngle;
    /* 方向传感器 */
    protected DirectionSensor mDirectionSensor;
    /* 线程调度器 */
    protected HandlerScheduler mScheduler;

    protected BaseCameraApi(Context context, Config config) {
        this.mContext = context.getApplicationContext();
        this.mConfig = config;
        this.prepare();
    }

    /**
     * 准备工作
     */
    private void prepare() {
        //初始化线程调度器
        mScheduler = new HandlerScheduler(getClass().getSimpleName());
        //初始化方向传感器
        mDirectionSensor = new DirectionSensor(mContext) {
            @Override
            public void onAngleChanged(int angle) {
                mDeviceAngle = angle;
            }
        };
    }
}
