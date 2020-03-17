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
import com.mkleo.chat.bean.Picture;
import com.mkleo.chat.utils.ActivityRouter;
import com.mkleo.chat.utils.BitmapLoader;

/**
 * 图片内容适配器
 */
public class PictureMessageAdapter extends MessageContentAdpater<Picture> {

    @Override
    protected int setContentView() {
        return R.layout.message_content_picture;
    }

    @Override
    protected void onContentViewCreated(View view, final Message<Picture> message) {
        //内容
        final Picture picture = message.getContent();
        final int width = picture.getWidth();
        final int height = picture.getHeight();
        final String path = picture.getPath();
        if (width > 0 && height > 0) {
            //如果宽高都大于0,那么首先确定imageView大小
            float scale = BitmapLoader.getSingleton().getCompressScale(view.getContext(), width, height);
            float viewWidth = width / scale;
            float viewHeight = height / scale;
            view.setLayoutParams(new FrameLayout.LayoutParams((int) viewWidth, (int) viewHeight));
        }

        final ImageView ivPicture = view.findViewById(R.id.iv_picture);
        final ProgressBar pbPicture = view.findViewById(R.id.pb_picture);

        //加载该图片
        BitmapLoader.getSingleton().loadBitmap(view.getContext(), path, false, new BitmapLoader.Callback() {

            @Override
            public void onCompleted(final Bitmap bitmap) {
                if (null != ivPicture) {
                    //当图片加载成功
                    //图片监听
                    ivPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.ExtraKey.IS_VIDEO, false);
                            bundle.putString(Constants.ExtraKey.MEDIA_PATH, path);
                            ActivityRouter.jump(v.getContext(), "com.mkleo.chat.ui.DisplayActivity", bundle);
                        }
                    });
                    ivPicture.post(new Runnable() {
                        @Override
                        public void run() {
                            ivPicture.setImageBitmap(bitmap);
                        }
                    });
                }
            }

            @Override
            public void onFailed(String errMsg) {
                if (null != ivPicture) {
                    ivPicture.post(new Runnable() {
                        @Override
                        public void run() {
                            ivPicture.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                        }
                    });
                }
            }
        });

        switch (message.getState()) {
            case Message.State.PROCESSING:
                if (!message.getUser().isSelf()) {
                    ivPicture.setVisibility(View.GONE);
                    pbPicture.setVisibility(View.VISIBLE);
                }
                break;
            case Message.State.SUCCESS:
                if (!message.getUser().isSelf()) {
                    ivPicture.setVisibility(View.VISIBLE);
                    pbPicture.setVisibility(View.GONE);
                }
                break;
            case Message.State.FAILD:
                if (!message.getUser().isSelf()) {
                    ivPicture.setVisibility(View.VISIBLE);
                    pbPicture.setVisibility(View.GONE);
                    ivPicture.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                }
                break;

            case Message.State.INIT:
            case Message.State.LOST:
                ivPicture.setVisibility(View.VISIBLE);
                pbPicture.setVisibility(View.GONE);
                ivPicture.setBackgroundResource(R.mipmap.ic_load_picture_faild);
                break;
        }

    }
}
