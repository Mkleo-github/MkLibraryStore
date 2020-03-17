package com.mkleo.chat.utils;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片缓存
 */
class BitmapCache {

    //缓存大小
    private static final int BUFFER_SIZE = 20 * 1024 * 1024;
    private int mCacheSize = 0;
    private final Object mLock = new Object();
    private Map<String, Bitmap> mBitmapCache = new HashMap<>();

    private BitmapCache() {
    }

    private static class Provider {
        static BitmapCache INSTANCE = new BitmapCache();
    }

    public static BitmapCache getSingleton() {
        return Provider.INSTANCE;
    }


    void addBitmap(String id, Bitmap bitmap) {
        synchronized (mLock) {
            //获取bitmap大小
            int bitmapSize = bitmap.getByteCount();
            if (mCacheSize + bitmapSize > BUFFER_SIZE) { //说明已经超出缓存大小
                //清空所有缓存
                this.clearAllCache();
                mCacheSize = 0;
            }
            //将文件加入缓存
            mBitmapCache.put(id, bitmap);
            mCacheSize += bitmapSize;
        }
    }

    Bitmap getCachaBitmap(String id) {
        synchronized (mLock) {
            return mBitmapCache.get(id);
        }
    }

    private void clearAllCache() {
        synchronized (mLock) {
            mBitmapCache.clear();
        }
    }
}
