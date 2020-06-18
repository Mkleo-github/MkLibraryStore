package com.mkleo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

import com.mkleo.camera.helper.CameraHelper;

/**
 * 相机创建器
 */
public class CameraCreator {

    private CameraCreator() {
        throw new RuntimeException("[请勿实例化]");
    }

    /**
     * 创建
     *
     * @param context
     * @param config
     * @return
     */
    public static MkCamera create(Context context, Config config) {
        if (!CameraHelper.isCameraAvailable(context)) {
            Toast.makeText(context, "当前相机无法使用,请检查权限和相机状态", Toast.LENGTH_SHORT).show();
            return null;
        }
        Camera.Parameters parameters = CameraHelper.getParameters((Integer) config.getCameraId());
        ICamera.Size previewSize = CameraHelper.getSupportSize(config.getPreviewSize(),
                CameraHelper.formatSizes(parameters.getSupportedPreviewSizes()));
        ICamera.Size pictureSize = CameraHelper.getSupportSize(config.getPictureSize(),
                CameraHelper.formatSizes(parameters.getSupportedPictureSizes()));
        ICamera.Size videoSize = CameraHelper.getSupportSize(config.getVideoSize(),
                CameraHelper.formatSizes(parameters.getSupportedVideoSizes()));
        //重新将支持的像素大小设置
        config.setSupportSizes(previewSize, pictureSize, videoSize);
        CameraHelper.logSize("设置预览大小", previewSize);
        CameraHelper.logSize("设置图片大小", pictureSize);
        CameraHelper.logSize("设置视频大小", videoSize);
        return new MkCamera(context, config);
    }
}
