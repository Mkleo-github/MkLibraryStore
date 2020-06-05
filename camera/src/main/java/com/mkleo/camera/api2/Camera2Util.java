package com.mkleo.camera.api2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.mkleo.camera.Params;
import com.mkleo.helper.MkLog;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class Camera2Util {

    private Camera2Util() {
        throw new RuntimeException("[请勿实例化]");
    }

    /**
     * 是否支持相机
     *
     * @param manager
     * @return
     */
    public static boolean isSupportCamera(CameraManager manager) throws CameraAccessException {
        return manager.getCameraIdList().length > 0;
    }

    /**
     * 相机是否可用
     *
     * @param manager
     * @param id
     * @return
     * @throws CameraAccessException
     */
    public static boolean isCameraAvailable(CameraManager manager, String id) throws CameraAccessException {
        for (String cameraId : manager.getCameraIdList()) {
            if (cameraId.equals(id))
                return true;
        }
        return false;
    }

    /**
     * 获取摄像头方向
     *
     * @param characteristics
     * @return
     */
    public static int getFacing(@NonNull CameraCharacteristics characteristics) {
        //前置/后置
        Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (null == facing) return -1;
        if (CameraCharacteristics.LENS_FACING_FRONT == facing) {
            return Params.Facing.FRONT;
        } else {
            return Params.Facing.BACK;
        }
    }

    /**
     * 获取摄像头(图形传感器)装配方向
     *
     * @param characteristics
     * @return
     */
    public static int getSensorOrientation(@NonNull CameraCharacteristics characteristics) {
        Integer orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        if (null == orientation) return -1;
        return orientation;
    }


    static void log(String log) {
        MkLog.print("[Camera2]:" + log);
    }
}
