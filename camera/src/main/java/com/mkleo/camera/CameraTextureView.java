package com.mkleo.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.mkleo.camera.helper.CameraHelper;

/**
 * 承载显示
 */
public class CameraTextureView extends TextureView {

    private ICamera.Size mSize;

    public CameraTextureView(Context context) {
        super(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置大小,会重新更新布局
     *
     * @param size
     */
    public void setSize(ICamera.Size size) {
        mSize = size;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量的宽高
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (null == mSize) {
            //如果没有设置大小,使用测量大小
            setMeasuredDimension(width, height);
        } else {
            //获取屏幕的旋转角度
            int windowAngle = CameraHelper.getWindowRotation(getContext());
            //是否是竖屏
            boolean isPortrait = windowAngle == Params.Angle.ANGLE_0 || windowAngle == Params.Angle.ANGLE_180;
            //设置的宽高
            int setWidth = isPortrait ? Math.min(mSize.getWidth(), mSize.getHeight()) : Math.max(mSize.getWidth(), mSize.getHeight());
            int setHeight = isPortrait ? Math.max(mSize.getWidth(), mSize.getHeight()) : Math.min(mSize.getWidth(), mSize.getHeight());
            //否则设置比例
            float aspectRatio = (float) setWidth / setHeight;
            setMeasuredDimension(width, (int) (width / aspectRatio));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CameraHelper.log("[onLayout]:" + "[width:" + getWidth() + "] [height:" + getHeight() + "]");
    }
}
