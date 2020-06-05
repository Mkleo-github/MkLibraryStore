package com.mkleo.librarys.views;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;

import com.mkleo.camera.CameraCreator;
import com.mkleo.camera.CameraTextureView;
import com.mkleo.camera.Config;
import com.mkleo.camera.ICamera;
import com.mkleo.camera.MkCamera;
import com.mkleo.helper.MkLog;
import com.mkleo.librarys.R;

public class Camera1Activity extends Activity {

    private CameraTextureView mPreview;
    private MkCamera mCamera1;
    private boolean isRecording = false;
    private ICamera.Size mPreviewSize = new ICamera.Size(540, 480);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
        mPreview = findViewById(R.id.preview);
        mCamera1 = CameraCreator.create(this, new Config.Builder()
                .setPreviewSize(mPreviewSize)
                .build());
        mPreview.setSize(mCamera1.getPreviewSize());
        mPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mCamera1.startPreview(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                mCamera1.stopPreview();
                mCamera1.startPreview(surface);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mCamera1.stopPreview();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickCapture(View view) {
    }

    public void onClickSwitch(View view) {
    }

    public void onClickRecord(View view) {
//        if (isRecording) {
//            isRecording = false;
//            mCamera1.stopRecord();
//        } else {
//            isRecording = true;
//            String fileName = "Video_" + SimpleDateFormat.getTimeInstance().format(new Date());
//            String path = MkApplication.PATH_GEN + "/Camera1/" + fileName + ".mp4";
//            mCamera1.startRecord(path, new ICamera.VideoCallback() {
//                @Override
//                public void onStartRecord() {
//                    MkLog.print("开始录制");
//                }
//
//                @Override
//                public void onStopRecord(String path) {
//                    MkLog.print("停止录制:" + path);
//                }
//            });
//        }
    }

    private void logSize(String tag, ICamera.Size size) {
        log("[" + tag + "]:" + "[width:" + size.getWidth() + "]  [height:" + size.getHeight() + "]");
    }

    private void log(String log) {
        MkLog.print(log);
    }

}
