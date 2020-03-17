package com.mkleo.chat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    /**
     * 将byte数组转换为bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte[] data) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }


    /**
     * 旋转bitmap
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float rotate) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (newBM.equals(bitmap)) {
            return newBM;
        }
        bitmap.recycle();
        return newBM;
    }


    /**
     * 生成文件
     *
     * @param data
     * @param path
     * @return
     */
    public static File buildFile(byte[] data, String path) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            // 判断SDcard状态
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // 错误提示
                return null;
            }

            // 检查SDcard空间
            File SDCardRoot = Environment.getExternalStorageDirectory();
            if (SDCardRoot.getFreeSpace() < 10000) {
                // 弹出对话框提示用户空间不够
                throw new Exception("存储空间不足");
            }

            // 在SDcard创建文件夹及文件
            File bitmapFile = new File(path);
            bitmapFile.getParentFile().mkdirs();// 创建文件夹

            FileOutputStream fstream = new FileOutputStream(bitmapFile);
            stream = new BufferedOutputStream(fstream);
            stream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }


    /**
     * 保存bitmap
     *
     * @param path
     * @param bitmap
     */
    public static boolean saveBitmapPNG(String path, Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException("请重新确认该Bitmap对象不为Null");

        File file = new File(path);

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 压缩bitmap
     *
     * @param bitmap 图片
     * @param scale  压缩比例
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int scale) {
        return compressBitmap(bitmap, scale);
    }


    /**
     * 压缩bitmap
     *
     * @param bitmap 图片
     * @param scale  压缩比例
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, float scale) {
        if (scale <= 0)
            return null;

        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap((int) (bitmap.getWidth() / scale),
                (int) (bitmap.getHeight() / scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, (int) (bitmap.getWidth() / scale),
                (int) (bitmap.getHeight() / scale));
        canvas.drawBitmap(bitmap, null, rect, null);
        return result;
    }

    /**
     * 压缩图片
     *
     * @param filePath 图片路径
     * @param val      采样率
     * @return
     */
    public static Bitmap compressBitmap(String filePath, int val) {
        // 数值越高，图片像素越低
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = val;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        return bitmap;
    }

    /**
     * 获取相应大小的bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap formatBitmap(Bitmap bitmap, int width, int height) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(bitmap, null, rect, null);
        return result;
    }


    /**
     * 获取相应大小的bitmap
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap formatBitmap(String path, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return formatBitmap(bitmap, width, height);
    }

    /**
     * 获取图片的旋转角
     *
     * @param path
     * @return
     */
    public static int getPictureOrientation(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int val = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (val) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;

                default:
                    return 0;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * 获取圆形bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getCirBitmap(Bitmap bitmap) {

        Bitmap cirBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cirBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float realX = Math.min(bitmap.getWidth() / 2, bitmap.getHeight() / 2);   // 比较宽和高  谁小用谁
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, realX, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));       // 核心方法
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return cirBitmap;
    }


    /**
     * 绘制圆角矩形 bitmap
     *
     * @param bitmap 图片
     * @param rValue 圆角值
     * @return
     */
    public static Bitmap getRoundRectangleBitmap(Bitmap bitmap, float rValue) {

        //创建一个新的和需要的bitmap一样大的空bitmap
        Bitmap rBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rectF, rValue, rValue, paint);                 //将区域画入画布
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  //代表绘制重合区域
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return rBitmap;
    }

    /**
     * 获取圆角矩形bitmap (固定值)
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundRectangleBitmap(Bitmap bitmap) {
        float rValue = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
        return getRoundRectangleBitmap(bitmap, rValue / 20.0f);
    }


    public static Bitmap drawVideoIcon(Bitmap bitmap) {

        Canvas canvas = new Canvas(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = width > height ? height / 4 : width / 4;
        int cx = width / 2;     //圆心X
        int cy = height / 2;    //圆心Y

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFFFFFF); //白色
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setStyle(Paint.Style.FILL);

        int r = radius / 2;  //内部边长
        int x1 = (int) (cx - Math.sin(Math.PI / 6) * r);
        int y1 = (int) (cy - (r + (r * Math.sin(Math.PI / 6))) / Math.cos(Math.PI / 6) * Math.sin(Math.PI / 6));

        int x2 = cx + r;
        int y2 = cy;

        int x3 = x1;
        int y3 = (int) (cy + (r + (r * Math.sin(Math.PI / 6))) / Math.cos(Math.PI / 6) * Math.sin(Math.PI / 6));


        //绘制三角形
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);

        return bitmap;
    }
}
