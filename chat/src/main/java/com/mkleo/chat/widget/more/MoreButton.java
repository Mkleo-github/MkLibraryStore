package com.mkleo.chat.widget.more;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Feature;

/**
 * Created by WangHJin on 2017/8/26.
 */

public class MoreButton extends LinearLayout implements View.OnClickListener {

    private Feature mModel;
    private static final int DEF_TEXT_SIZE = 5;//dip , 由于sp感觉不太一样所以使用dip

    public interface ClickCallback {
        void onClick(String name);
    }

    public MoreButton(Context context) {
        super(context);
        LayoutParams mLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutParams.weight = 1;
        this.setBackgroundResource(R.drawable.selector_btn_more);
        this.setGravity(Gravity.CENTER);
        this.setLayoutParams(mLayoutParams);
    }

    public void bindModel(Feature model) {
        this.mModel = model;
        init();
    }

    private LinearLayout mContainer;
    private ImageView mImageView;
    private TextView mTextView;
    private LayoutParams mContainerParams, mImageViewParams, mTextViewParams;
    private final static float SCALE = 0.618f;

    private void init() {
        this.setOnClickListener(this);
        mContainer = new LinearLayout(getContext());
        mImageView = new ImageView(getContext());
        mTextView = new TextView(getContext());
        this.addView(mContainer);
        mContainer.addView(mImageView);
        mContainer.addView(mTextView);
        mContainer.setClickable(false);
        mImageView.setClickable(false);
        mTextView.setClickable(false);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;
        loadLayout();
    }


    private void loadLayout() {
        if (mModel == null) return;
        float width = getWidth();
        float height = getHeight();
        //对布局进行修改
        //容器
        mContainerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContainerParams.gravity = Gravity.CENTER;
        mContainer.setGravity(Gravity.CENTER);
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mContainer.setLayoutParams(mContainerParams);

        //图片
        float imgWidth;
        if (width > height) imgWidth = height * SCALE;
        else imgWidth = width * SCALE;
        mImageViewParams = new LayoutParams((int) imgWidth, (int) imgWidth);
        mImageViewParams.gravity = Gravity.CENTER;
        mImageView.setBackgroundResource(mModel.getImage());
        mImageView.setLayoutParams(mImageViewParams);

        //文字
        mTextViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextViewParams.gravity = Gravity.CENTER;
        mTextViewParams.topMargin = 5;
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.BLACK);
        mTextView.setLayoutParams(mTextViewParams);
        mTextView.setText(mModel.getName());
    }


    @Override
    public void onClick(View v) {
        if (mModel == null || mModel.getCallback() == null) return;
        mModel.getCallback().onClick(mModel.getName());
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
