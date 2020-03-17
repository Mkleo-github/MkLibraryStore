package com.mkleo.chat.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bm.library.PhotoView;
import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.utils.BitmapLoader;
import com.mkleo.chat.utils.ToastUtil;

import java.io.File;

/**
 * 图片视频展示的界面
 */
public final class DisplayActivity extends AppCompatActivity {

    private boolean isVideo;
    private String mPath;
    private PhotoView mPictureDisplay;
    private Bitmap mLoadedBitmap;
    private VideoView mVideoDisplay;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Bundle bundle = getIntent().getExtras();
        isVideo = bundle.getBoolean(Constants.ExtraKey.IS_VIDEO, false);
        mPath = bundle.getString(Constants.ExtraKey.MEDIA_PATH);

        if (null == mPath || !new File(mPath).exists()) {
            ToastUtil.show(getBaseContext(), "文件打开错误!");
            finish();
            return;
        }

        mPictureDisplay = findViewById(R.id.picture_display);
        mVideoDisplay = findViewById(R.id.video_display);
        ivBack = findViewById(R.id.iv_back);

        if (isVideo) {
            mPictureDisplay.setVisibility(View.INVISIBLE);
            mVideoDisplay.setVisibility(View.VISIBLE);
            //播放视频
            Uri uri = Uri.parse(mPath);
            mVideoDisplay.setMediaController(new MediaController(this));
            mVideoDisplay.setVideoURI(uri);
            mVideoDisplay.start();
            mVideoDisplay.requestFocus();

        } else {
            mPictureDisplay.setVisibility(View.VISIBLE);
            mVideoDisplay.setVisibility(View.INVISIBLE);
            mLoadedBitmap = BitmapLoader.getSingleton().loadSoureBitmap(mPath);
            mPictureDisplay.setImageBitmap(mLoadedBitmap);
            // 启用图片缩放功能
            mPictureDisplay.enable();
            // 获取/设置 最大缩放倍数
            mPictureDisplay.setMaxScale(3);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (null != mLoadedBitmap) {
            mLoadedBitmap.recycle();
            mLoadedBitmap = null;
        }
    }
}
