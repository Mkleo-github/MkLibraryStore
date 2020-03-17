package com.mkleo.chat.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.Video;
import com.mkleo.chat.utils.ActivityRouter;
import com.mkleo.chat.utils.BitmapLoader;

/**
 * @说明: 视频处理
 * @作者: Wang HengJin
 * @日期: 2018/4/10 10:01 星期二
 */
public class VideoMessageAdapter extends MessageContentAdpater<Video> {

    @Override
    protected int setContentView() {
        return R.layout.message_content_video;
    }

    @Override
    protected void onContentViewCreated(final View view, Message<Video> message) {
        //内容
        final Video video = message.getContent();
        final int width = video.getWidth();
        final int height = video.getHeight();
        final String path = video.getPath();

        if (width > 0 && height > 0) {
            //如果宽高都大于0,那么首先确定imageView大小
            float scale = BitmapLoader.getSingleton().getCompressScale(view.getContext(), width, height);
            float viewWidth = width / scale;
            float viewHeight = height / scale;
            view.setLayoutParams(new FrameLayout.LayoutParams((int) viewWidth, (int) viewHeight));
        }

        final ImageView ivVideo = view.findViewById(R.id.iv_video);
        final ProgressBar pbVideo = view.findViewById(R.id.pb_video);
        //加载该图片
        BitmapLoader.getSingleton().loadBitmap(view.getContext(), path, true,
                new BitmapLoader.Callback() {

                    @Override
                    public void onCompleted(final Bitmap bitmap) {
                        if (null != ivVideo) {
                            ivVideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.ExtraKey.IS_VIDEO, true);
                                    bundle.putString(Constants.ExtraKey.MEDIA_PATH, path);
                                    ActivityRouter.jump(view.getContext(), "com.mkleo.chat.ui.DisplayActivity", bundle);
                                }
                            });
                            ivVideo.post(new Runnable() {
                                @Override
                                public void run() {
                                    ivVideo.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        if (null != ivVideo) {
                            ivVideo.post(new Runnable() {
                                @Override
                                public void run() {
                                    ivVideo.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                                }
                            });
                        }
                    }
                });

        switch (message.getState()) {
            case Message.State.PROCESSING:
                if (!message.getUser().isSelf()) {
                    ivVideo.setVisibility(View.GONE);
                    pbVideo.setVisibility(View.VISIBLE);
                }
                break;
            case Message.State.SUCCESS:
                if (!message.getUser().isSelf()) {
                    ivVideo.setVisibility(View.VISIBLE);
                    pbVideo.setVisibility(View.GONE);
                }
                break;
            case Message.State.FAILD:
                if (!message.getUser().isSelf()) {
                    ivVideo.setVisibility(View.VISIBLE);
                    pbVideo.setVisibility(View.GONE);
                    ivVideo.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                }
                break;

            case Message.State.INIT:
            case Message.State.LOST:
                ivVideo.setVisibility(View.VISIBLE);
                pbVideo.setVisibility(View.GONE);
                ivVideo.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                break;
        }


    }

}
