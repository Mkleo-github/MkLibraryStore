package com.mkleo.bases.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * 方向传感器
 */
public class DirectionSensor extends DirectionSensorListener {

    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mAngle;

    protected DirectionSensor(Context context) {
        this.mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 解除监听
     */
    public void unbind() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAngleChanged(int angle) {
        this.mAngle = angle;
    }

    public int getAngle() {
        return mAngle;
    }
}
