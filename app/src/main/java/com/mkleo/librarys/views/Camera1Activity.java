package com.mkleo.librarys.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.mkleo.camera.Camera;
import com.mkleo.camera.Config;
import com.mkleo.camera.ICamera;
import com.mkleo.helper.MkLog;
import com.mkleo.librarys.MkApplication;
import com.mkleo.librarys.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera1Activity extends Activity {

    private SurfaceView mPreview;
    private Camera mCamera1;
    private boolean isRecording = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
        mPreview = findViewById(R.id.preview);

        mCamera1 = new Camera(this, new Config.Builder().build());

        mPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCamera1.startPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mCamera1.stopPreview();
                mCamera1.startPreview(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera1.stopPreview();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera1.release();
    }

    public void onClickCapture(View view) {
    }

    public void onClickSwitch(View view) {
    }

    public void onClickRecord(View view) {
        if (isRecording) {
            isRecording = false;
            mCamera1.stopRecord();
        } else {
            isRecording = true;
            String fileName = "Video_" + SimpleDateFormat.getTimeInstance().format(new Date());
            String path = MkApplication.PATH_GEN + "/Camera1/" + fileName + ".mp4";
            mCamera1.startRecord(path, new ICamera.VideoCallback() {
                @Override
                public void onStartRecord() {
                    MkLog.print("开始录制");
                }

                @Override
                public void onStopRecord(String path) {
                    MkLog.print("停止录制:" + path);
                }
            });
        }
    }
}
