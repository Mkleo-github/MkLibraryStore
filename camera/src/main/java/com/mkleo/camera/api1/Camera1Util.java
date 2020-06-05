package com.mkleo.camera.api1;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.view.Surface;
import android.view.WindowManager;

import com.mkleo.camera.ICamera;
import com.mkleo.camera.Params;
import com.mkleo.camera.helper.CameraHelper;
import com.mkleo.helper.MkLog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Camera1Util {

    private Camera1Util() {
        throw new RuntimeException("[请勿实例化]");
    }

    /**
     * 检测是否支持相机
     *
     * @return
     */
    public static boolean isSupportCamera() {
        return Camera.getNumberOfCameras() > 0;
    }

    /**
     * 检测该ID的相机是否支持
     *
     * @param id
     * @return
     */
    public static boolean isSupportCamera(int id) {
        return id >= 0 && id < Camera.getNumberOfCameras();
    }

    /**
     * 是否是前置摄像头
     *
     * @param id
     * @return
     */
    public static boolean isFrontCamera(int id) {
        if (!isSupportCamera(id)) return false;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        return info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }



    /**
     * 转换为区域
     *
     * @param scaleX x比例
     * @param scaleY y比例
     * @return
     */
    public static Camera.Area toArea(float scaleX, float scaleY) {
        int areaX = (int) (2000 * scaleX - 1000);
        int areaY = (int) (2000 * scaleY - 1000);
        //生成矩阵区域
        Rect rect = new Rect(
                Math.max(areaX - 100, -1000),
                Math.max(areaY - 100, -1000),
                Math.min(areaX + 100, 1000),
                Math.min(areaY + 100, 1000)
        );
        return new Camera.Area(rect, 1000);
    }

    /**
     * 获取预览角度 (操作的目的就是把摄像头捕捉画面旋转到屏幕相同方向)
     *
     * @param context
     * @param cameraId
     * @return
     */
    public static int getPreviewAngle(@NonNull Context context, int cameraId) {
        if (!isSupportCamera(cameraId)) return 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        //获取当前屏幕的旋转角度
        int windowAngle =CameraHelper.getWindowRotation(context);
        //获取设备的装配角度
        int setupAngle = info.orientation;

        int previewAngle = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            //后置摄像头
            previewAngle = (setupAngle - windowAngle + 360) % 360;
        } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头
            previewAngle = (setupAngle + windowAngle) % 360;
            //镜像
            previewAngle = (360 - previewAngle) % 360;
        }
        return previewAngle;
    }


    /**
     * 获取旋转角度
     *
     * @param angle
     * @param cameraId
     * @return
     */
    public static int getRotationAngle(int angle, int cameraId) {
        if (!isSupportCamera(cameraId)) return 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        //获取设备的装配角度
        int setupAngle = info.orientation;
        //获取当前设备角度
        int deviceRotation = CameraHelper.getDeviceRotation(angle);
        int pictureAngle = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            pictureAngle = (setupAngle - deviceRotation + 360) % 360;
        } else {
            //前置摄像头
            pictureAngle = (setupAngle + deviceRotation) % 360;
        }
        return pictureAngle;
    }

    /**
     * 获取下一个摄像头id
     *
     * @param cameraId
     * @return
     */
    public static int getNextCamera(int cameraId) {
        //相机个数
        int cameraNumber = Camera.getNumberOfCameras();
        if (cameraId < cameraNumber - 1) {
            return cameraId + 1;
        } else {
            return 0;
        }
    }


    /**
     * 相机装备是否是竖向
     *
     * @param cameraId
     * @return
     */
    public static boolean isCameraSetupVertical(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int orientation = info.orientation;
        log("摄像头[" + cameraId + "] 装配角度 [" + orientation + "]");
        switch (orientation) {
            case Params.Angle.ANGLE_0:
            case Params.Angle.ANGLE_180:
                return true;
            case Params.Angle.ANGLE_90:
            case Params.Angle.ANGLE_270:
                return false;
            default:
                return true;
        }
    }


    /**
     * 获取对焦模式
     *
     * @param focusMode
     * @return
     */
    public static String getFocusMode(@Params.FocusMode int focusMode) {
        switch (focusMode) {
            case Params.FocusMode.AUTO:
                return Camera.Parameters.FOCUS_MODE_AUTO;
            case Params.FocusMode.FIXED:
                return Camera.Parameters.FOCUS_MODE_FIXED;
            case Params.FocusMode.PICTURE:
                return Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
            case Params.FocusMode.VIDEO:
                return Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
            default:
                return Camera.Parameters.FOCUS_MODE_AUTO;
        }
    }

    /**
     * 获取闪光灯模式
     *
     * @param flashMode
     * @return
     */
    public static String getFlashMode(@Params.FlashMode int flashMode) {

        switch (flashMode) {
            case Params.FlashMode.OFF:
                return Camera.Parameters.FLASH_MODE_OFF;
            case Params.FlashMode.AUTO:
                return Camera.Parameters.FLASH_MODE_AUTO;
            case Params.FlashMode.ON:
                return Camera.Parameters.FLASH_MODE_ON;
            case Params.FlashMode.RED_EYE:
                return Camera.Parameters.FLASH_MODE_RED_EYE;
            case Params.FlashMode.TORCH:
                return Camera.Parameters.FLASH_MODE_TORCH;
            default:
                return Camera.Parameters.FLASH_MODE_OFF;
        }
    }


    static void log(String log) {
        MkLog.print("[Camera1]:" + log);
    }

}
