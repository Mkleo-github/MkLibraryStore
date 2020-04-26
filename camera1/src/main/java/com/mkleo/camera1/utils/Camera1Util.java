package com.mkleo.camera1.utils;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.mkleo.camera1.Params;
import com.mkleo.helper.MkLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Camera1Util {

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
     * 通过旋转角度计算设备方向
     *
     * @param angle 通过方向传感器获得的精确角度
     * @return
     */
    public static int getDeviceRotation(int angle) {
        if (angle >= 0 && angle < 45
                || angle >= 315 && angle <= 360) {
            //竖着拍的
            return Params.Angle.ANGLE_0;
        } else if (angle >= 45 && angle < 135) {
            //横着拍的(屏幕右转)
            return Params.Angle.ANGLE_270;
        } else if (angle >= 135 && angle < 225) {
            //倒着拍摄
            return Params.Angle.ANGLE_180;
        } else if (angle >= 225 && angle < 315) {
            //横着拍的(屏幕左转)
            return Params.Angle.ANGLE_90;
        } else {
            return Params.Angle.ANGLE_0;
        }
    }

    /**
     * 获取当前window的角度
     *
     * @param context
     * @return
     */
    public static int getWindowRotation(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int windowRotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = Params.Angle.ANGLE_0;
        switch (windowRotation) {
            case Surface.ROTATION_0:
                degrees = Params.Angle.ANGLE_0;
                break;
            case Surface.ROTATION_90:
                degrees = Params.Angle.ANGLE_90;
                break;
            case Surface.ROTATION_180:
                degrees = Params.Angle.ANGLE_180;
                break;
            case Surface.ROTATION_270:
                degrees = Params.Angle.ANGLE_270;
                break;
        }
        return degrees;
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
        int windowAngle = getWindowRotation(context);
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
        int deviceRotation = getDeviceRotation(angle);
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


    private static final int FRONT = -1;
    private static final int BACK = 1;

    /**
     * 排序Size
     *
     * @param isAscending 是否是升序
     * @param sizes
     * @return
     */
    public static List<Camera.Size> sortSize(final boolean isAscending, List<Camera.Size> sizes) {

        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size size1, Camera.Size size2) {
                if (size1.height > size2.height) {
                    //如果是升序就放在后面
                    return isAscending ? BACK : FRONT;
                } else if (size1.height == size2.height) {
                    //比较宽度
                    if (size1.width > size2.width) {
                        return isAscending ? BACK : FRONT;
                    } else {
                        return isAscending ? FRONT : BACK;
                    }
                } else {
                    return isAscending ? FRONT : BACK;
                }
            }
        });

        return sizes;
    }


    /**
     * 获取最接近的size
     *
     * @param sizes
     * @param width
     * @param height
     * @return
     */
    public static Camera.Size getNearSize(List<Camera.Size> sizes, int width, int height) {
        //将Size降序排列
        sortSize(false, sizes);
        Camera.Size nearSize = null;
        for (Camera.Size size : sizes) {
            if (size.height <= width && size.width <= height) {
                nearSize = size;
                break;
            }
        }
        if (null == nearSize) {
            //获取最小的size
            nearSize = sizes.get(sizes.size() - 1);
        }
        return nearSize;
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

    public static void logSizes(List<Camera.Size> sizes) {
        log("[logSize]");
        for (Camera.Size size : sizes) {
            log("[w:" + size.width + "] [h:" + size.height + "]");
        }
    }


    public static void log(String log) {
        MkLog.print("[Camera1]:" + log);
    }

}
