package com.mkleo.bases.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * 方向传感器监听
 */
abstract class DirectionSensorListener implements SensorEventListener {

    private static final int POSITION_X = 0;    //X轴值的下标
    private static final int POSITION_Y = 1;    //Y轴值的下标
    private static final int POSITION_Z = 2;    //Z轴值的下标

    private static final int UNKNOW = -99;      //未知方向
    private int mNoteAngle = UNKNOW;      //记录方向


    @Override
    public void onSensorChanged(SensorEvent event) {
        final float[] values = event.values;
        int resultAngle = UNKNOW;       //默认值为未知
        float x = -values[POSITION_X];  //X轴的值
        float y = -values[POSITION_Y];  //Y轴的值
        float z = -values[POSITION_Z];  //Z轴的值
        float magnitude = x * x + y * y;//??
        if (magnitude * 4 >= z * z) {
            // 屏幕旋转时
            float OneEightyOverPi = 57.29577957855f;
            float angle = (float) Math.atan2(-y, x) * OneEightyOverPi;
            resultAngle = 90 - (int) Math.round(angle);
            // normalize to 0 - 359 range
            while (resultAngle >= 360) {
                resultAngle -= 360;
            }
            while (resultAngle < 0) {
                resultAngle += 360;
            }
        }

        if (resultAngle == mNoteAngle)
            return;
        mNoteAngle = resultAngle;
        onAngleChanged(resultAngle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public abstract void onAngleChanged(int angle);
}
