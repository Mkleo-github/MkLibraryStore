package com.mkleo.bases.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.mkleo.helper.MkLog;

/**
 * des:
 * by: Mk.leo
 * date: 2019/8/6
 */
public abstract class FlashView extends View {

    private final String TAG = this.getClass().getSimpleName();

    /* 刷新率(通过帧率换算得到) */
    private int mFlashRate;
    /* 请求刷新的次数 */
    private int mRequestPostCount = 0;
    /* 图形宽度 */
    protected int mWidth;
    /* 图形高度 */
    protected int mHeight;
    /* 是否准备完毕 */
    private boolean isReady = false;


    public FlashView(Context context) {
        this(context, null);
    }

    public FlashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFps(ViewPoster.sFps);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //把自己添加到容器
        ViewPoster.attachView(this);
        MkLog.print(TAG, "FlashView onAttachedToWindow");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算宽高
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        if (!isReady) {
            onPrepare();
            isReady = true;
        } else {
            onDrawing(canvas);
        }
    }

    @Override
    protected final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDetachView();
        //移除
        ViewPoster.detachView(this);
        MkLog.print(TAG, "FlashView onDetachedFromWindow");
    }

    /**
     * 做一些初始化工作
     */
    protected abstract void onPrepare();

    /**
     * 正在绘制
     *
     * @param canvas
     */
    protected abstract void onDrawing(Canvas canvas);

    /**
     * 销毁
     */
    protected abstract void onDetachView();

    /**
     * 设置帧率
     *
     * @param fps
     */
    public void setFps(int fps) {
        setFlashRate(fps);
    }

    /**
     * 设置刷新率
     *
     * @param fps
     */
    private void setFlashRate(int fps) {
        if (fps <= 0) {
            //如果帧率设置为0或者小于0将不刷新
            mFlashRate = -1;
            return;
        }
        if (fps >= ViewPoster.sFps) {
            fps = ViewPoster.sFps;
        }
        //余数
        int residue = ViewPoster.sFps % fps;
        if (residue != 0 && residue < fps / 2) {
            //余数不等于0,且当余数小于本身FPS的一半
            //需要增加倍率
            mFlashRate = ViewPoster.sFps / fps + 1;
        } else {
            mFlashRate = ViewPoster.sFps / fps;
        }
    }


    /**
     * 刷新
     */
    public synchronized void postView() {
        if (mFlashRate <= 0) return;
        mRequestPostCount++;
        if (mRequestPostCount == mFlashRate) {
            //刷新
            postInvalidate();
            mRequestPostCount = 0;
        }
    }

}
