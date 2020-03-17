package com.mkleo.chat.widget.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class ScreenDialog extends Dialog {

    private View mContentView;

    /* 占满全屏的dialog */
    public ScreenDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // <!--关键点1-- >
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // <!--关键点2-- >
        mContentView = setContentView();
        super.setContentView(mContentView);
        //  <!--关键点3-->
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        //  <!-- 关键点4 -->
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        onDialgoCreate(savedInstanceState);
    }

    protected View getContentView() {
        return mContentView;
    }

    /* 设置布局 */
    protected abstract View setContentView();

    protected abstract void onDialgoCreate(Bundle savedInstanceState);
}
