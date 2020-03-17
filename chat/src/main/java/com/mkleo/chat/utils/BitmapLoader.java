package com.mkleo.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapLoader {

    //缩放比例
    private static final int SCALE_VALUE = 4;

    private BitmapLoader() {
    }

    private static class Provider {
        static final BitmapLoader INSTANCE = new BitmapLoader();
    }

    public static BitmapLoader getSingleton() {
        return Provider.INSTANCE;
    }

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    /**
     * 加载bitmap
     *
     * @param context
     * @param path
     * @param isVideo
     * @param callback
     */
    public void loadBitmap(Context context, String path, boolean isVideo, Callback callback) {
        Bitmap bitmap = BitmapCache.getSingleton().getCachaBitmap(path);
        if (null != bitmap) {
            callback.onCompleted(bitmap);
        } else {
            mExecutor.execute(new Loader(context, path, isVideo, callback));
        }
    }

    /**
     * 加载原图
     *
     * @param path
     * @return
     */
    public Bitmap loadSoureBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 获取压缩的比例
     *
     * @param context
     * @param width
     * @param height
     * @return
     */
    public float getCompressScale(Context context, int width, int height) {
        if (null != context && width > 0 && height > 0) {
            final int windowWidth = WindowUtil.getWindowWidth(context);
            final int windowHeight = WindowUtil.getWindowHeight(context);
            //是否是竖屏
            boolean isPortrait = width < height;
            float scale = 1.0f;
            if (isPortrait) {
                //最大宽度
                int maxWidth = windowWidth / SCALE_VALUE;
                //如果图片本身的大小大于最大宽度
                if (width > maxWidth) {
                    //压缩比例
                    scale = width / (float) maxWidth;
                }
            } else {
                //最大宽度
                int maxWidth = windowHeight / SCALE_VALUE;
                if (width > maxWidth) {
                    //压缩比例
                    scale = width / (float) maxWidth;
                }
            }
            return scale;
        } else {
            //不压缩
            return 1.0f;
        }
    }


    private static class Loader extends Thread {

        private final Context mContext;
        private final String mPath;         //文件路径
        private final boolean isVideo;      //是否是视频
        private final Callback mCallback;


        private Loader(Context context, String path, boolean isVideo, Callback callback) {
            this.mContext = context;
            this.mPath = path;
            this.isVideo = isVideo;
            this.mCallback = callback;
        }

        @Override
        public void run() {

            try {
                Bitmap bitmap = null;
                if (isVideo) {
                    //获取第一帧
                    bitmap = MediaUtil.getVideoFirstFrame(mPath);
                } else {
                    bitmap = BitmapFactory.decodeFile(mPath); //获取图片
                }

                int pictureWidth = bitmap.getWidth();           //图片宽度
                int pictureHeight = bitmap.getHeight();         //图片高度
                //获取压缩比例
                float scale = BitmapLoader.getSingleton().getCompressScale(mContext, pictureWidth, pictureHeight);
                bitmap = BitmapUtil.compressBitmap(bitmap, scale);   //压缩
                int rotate = BitmapUtil.getPictureOrientation(mPath);  //获取旋转角
                bitmap = BitmapUtil.rotateBitmap(bitmap, rotate);      //旋转图片
                bitmap = BitmapUtil.getRoundRectangleBitmap(bitmap);   //绘制圆角
                if (isVideo) {
                    BitmapUtil.drawVideoIcon(bitmap);
                }
                //添加入缓存
                BitmapCache.getSingleton().addBitmap(mPath, bitmap);
                if (mCallback != null) {
                    mCallback.onCompleted(bitmap);
                }
            } catch (Exception e) {
                if (mCallback != null) {
                    mCallback.onFailed(e.toString());
                }
            }
        }
    }

    public interface Callback {

        void onCompleted(Bitmap bitmap);

        void onFailed(String errMsg);
    }
}
