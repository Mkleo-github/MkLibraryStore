package com.mkleo.camera.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.WindowManager;

import com.mkleo.camera.ICamera;
import com.mkleo.camera.Params;
import com.mkleo.helper.MkLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 协助一些相机的创建
 */
public class CameraHelper {

    private static final String TAG = "Camera.log";


    private CameraHelper() {
        throw new RuntimeException("[请勿实例化]");
    }

    //需要的权限
    public static final String[] NEED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 判断相机是否可用
     *
     * @param context
     * @return
     */
    public static boolean isCameraAvailable(Context context) {
        if (Camera.getNumberOfCameras() <= 0) {
            log("[该设备没有摄像头]");
            return false;
        }
        for (String permission : NEED_PERMISSIONS) {
            if ((ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED)) {
                log("[必要权限委授予]");
                return false;
            }
        }
        Camera camera = null;
        try {
            camera = Camera.open();
            camera.setParameters(camera.getParameters());
        } catch (Exception e) {
            log("[摄像头被占用]");
            if (null != camera) camera.release();
            return false;
        }
        return true;
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
     * 通过旋转角度计算设备方向
     *
     * @param angle 通过方向传感器获得的精确角度
     * @return
     */
    public static int getDeviceRotation(int angle) {
        if (angle >= 0 && angle < 45 || angle >= 315 && angle <= 360) {
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
     * 获取当前摄像头参数
     *
     * @param cameraId
     * @return
     */
    public static Camera.Parameters getParameters(int cameraId) {
        Camera camera = Camera.open(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        camera.release();
        return parameters;
    }

    /**
     * 获取支持的像素大小
     *
     * @param size
     * @param supportSizes
     * @return
     */
    public static ICamera.Size getSupportSize(final ICamera.Size size, final List<ICamera.Size> supportSizes) {
        Collections.sort(supportSizes, new CameraSizeSorter(false));
        for (ICamera.Size supportSize : supportSizes) {
            if (supportSize.getWidth() * supportSize.getHeight() <= size.getWidth() * size.getHeight()) {
                return supportSize;
            }
        }
        return new ICamera.Size(0, 0);
    }

    /**
     * 统一Size
     *
     * @param sizes
     * @return
     */
    public static List<ICamera.Size> formatSizes(List<Camera.Size> sizes) {
        List<ICamera.Size> result = new ArrayList<>();
        for (Camera.Size size : sizes) {
            result.add(new ICamera.Size(size.width, size.height));
        }
        return result;
    }

    /**
     * 日志
     *
     * @param log
     */
    public static void log(String log) {
        MkLog.print(TAG, log);
    }

    public static void logSize(String tag, ICamera.Size size) {
        log("[" + tag + "] [width:" + size.getWidth() + "] [height:" + size.getHeight() + "]");
    }
}
