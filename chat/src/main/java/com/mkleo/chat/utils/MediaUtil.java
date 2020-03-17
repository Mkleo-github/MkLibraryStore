package com.mkleo.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

public class MediaUtil {


    /**
     * 获取视频文件的第一贞
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoFirstFrame(String path) {
        Bitmap bitmap;
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        bitmap = media.getFrameAtTime();
        media.release(); // 释放资源
        return bitmap;
    }


    private static MediaPlayer sMediaPlayer;

    /**
     * 获取媒体文件时长
     *
     * @param context
     * @param path
     * @return
     */
    public static int getMediaLength(Context context, String path) {
        int length;
        sMediaPlayer = MediaPlayer.create(context, Uri.parse(path));
        if (sMediaPlayer != null) {
            length = sMediaPlayer.getDuration() / 1000;
            sMediaPlayer.release();
            return length;
        }
        return 0;
    }


    /**
     * 将媒体文件广播给系统
     *
     * @param context
     * @param path
     */
    public static void broadcastSystem(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        intent.setData(contentUri);
        context.sendBroadcast(intent);
    }




}
