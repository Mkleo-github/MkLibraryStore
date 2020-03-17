package com.mkleo.librarys.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.mkleo.bases.view.FlashView;
import com.mkleo.helper.MkLog;

/**
 * des:
 * by: Mk.leo
 * date: 2019/8/7
 */
public class FlashViewAcitivity extends Activity {

    private MyView mMyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyView = new MyView(this);
        mMyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mMyView.setFps(1);
        setContentView(mMyView);
    }

    private class MyView extends FlashView {

        public MyView(Context context) {
            super(context);
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onPrepare() {
            MkLog.print("Width:" + mWidth + " Height:" + mHeight);
        }

        @Override
        protected void onDrawing(Canvas canvas) {
            MkLog.print("onDrawing");
        }

        @Override
        protected void onDetachView() {

        }
    }
}
