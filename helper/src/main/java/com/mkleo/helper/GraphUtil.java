package com.mkleo.helper;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GraphUtil {

    /**
     * 类名: GraphUtil
     * 描述: 获取文字宽度
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:11
     */
    public static int getTextWidth(String text, Paint paint) {
        int textWidth = (int) paint.measureText(text);
        return textWidth;
    }


    /**
     * 类名: GraphUtil
     * 描述: 获取基准线(用于绘制文字剧中)
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:13
     */
    public static float getBaseLine(Paint paint, float baseLineY) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = baseLineY - (baseLineY - fontHeight) / 2 - fontMetrics.bottom;
        return textBaseY;
    }


    /**
     * 类名: GraphUtil
     * 描述: 获取最大字体
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:13
     */
    public static int getMaxTextSize(String text, int maxWidth, int maxHeight, int pValue, Paint paint) {// 处理字体大小(横向)
        int tSize = maxHeight - pValue;// 字体大小不可能大于Y
        paint.setTextSize(tSize);
        int tWidth = getTextWidth(text, paint);
        while (tWidth > maxWidth - pValue) {
            tSize -= 2;
            paint.setTextSize(tSize);
            tWidth = getTextWidth(text, paint);
        }
        return tSize;
    }


    /**
     * 类名: GraphUtil
     * 描述: 文字剧中绘制
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:14
     */
    public static void drawTextCenter(String text, int x, int y, int width, int height, Paint paint, Canvas canvas) {
        int tw = getTextWidth(text, paint);
        int labWidth = width;
        int baseLineY = 2 * y + height;
        canvas.drawText(text, x + (labWidth - tw) / 2, getBaseLine(paint, baseLineY), paint);
    }

    /**
     * 类名: GraphUtil
     * 描述: 文字垂直剧中
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:15
     */
    public void drawTextCenterVertical(String text, int x, int y, int height, Paint paint, Canvas canvas) {
        int baseLineY = 2 * y + height;
        canvas.drawText(text, x, getBaseLine(paint, baseLineY), paint);
    }


    /**
     * 类名: GraphUtil
     * 描述: 绘制虚线
     * 码农: Wang HengJin
     * 时间: 2018/3/17 14:15
     *
     * @param startX     --X起始位置
     * @param endX       --X终止位置
     * @param y          --Y起始位置
     * @param itemWidth  --虚线单个线条宽度
     * @param spaceWidth --间隔宽度
     * @param color      --颜色
     **/

    public static void drawDottedLine(int startX, int endX, int y, int itemWidth, int spaceWidth, int color, Paint paint, Canvas canvas) {//绘制虚线

        int LineWidht = endX - startX;
        int itemCount = LineWidht / (itemWidth + spaceWidth);//虚线个数
        paint.setColor(color);
        // 开始绘制虚线
        for (int i = 0; i <= itemCount; i++) {
            canvas.drawLine((spaceWidth + itemWidth) * i, y, (spaceWidth + itemWidth) * i + itemWidth, y, paint);
        }

    }
}
