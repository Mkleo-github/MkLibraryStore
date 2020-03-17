package com.mkleo.chat.widget.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.bean.Emoji;
import com.mkleo.chat.utils.EmojiUtil;

/**
 * Created by WangHJin on 2017/8/26.
 */

public class EmojiButton extends LinearLayout implements View.OnClickListener {


    private Emoji mModel;
    private static final int MARGIN = 5;
    private static final int MAX_SIZE = 30;//dip
    private ImageView mEmoji;
    private final EditText mInput;
    private final Emoji.Format mFormat;


    public EmojiButton(Context context, EditText input, Emoji.Format format) {
        super(context);
        this.mInput = input;
        this.mFormat = format;

        LayoutParams mLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutParams.weight = 1;
        this.setBackgroundResource(R.drawable.selector_btn_more);
        this.setGravity(Gravity.CENTER);
        this.setLayoutParams(mLayoutParams);
    }

    public void bindModel(Emoji model) {
        this.mModel = model;
        init();
    }

    private void init() {
        this.setClickable(true);
        this.setOnClickListener(this);
        mEmoji = new ImageView(getContext());
        mEmoji.setClickable(false);
        this.addView(mEmoji);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;
        loadLayout();
    }

    private void loadLayout() {

        if (mModel == null) return;
        int size;
        if (getWidth() > getHeight()) size = getHeight() - MARGIN;
        else size = getWidth() - MARGIN;
        int relMaxSize = EmojiUtil.dip2px(getContext(), MAX_SIZE);
        if (size > relMaxSize) size = relMaxSize;
        mEmoji.setLayoutParams(new LayoutParams(size, size));
        if (mModel.getEmojiName().equals(Constants.DELETE)) {
            mEmoji.setBackgroundResource(R.mipmap.face_delete);
        } else {
            Bitmap emoji = EmojiUtil.getEmojis().get(mModel.getEmojiName());
            mEmoji.setBackgroundDrawable(toDrawable(emoji));
        }
    }


    private Drawable toDrawable(Bitmap bitmap) {
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onClick(View v) {
        String formatName = EmojiUtil.formatName(mFormat, mModel.getEmojiName());
        if (formatName.equals(EmojiUtil.formatName(mFormat, Constants.DELETE))) {//说明是删除按钮
            EmojiUtil.delete(mInput, mFormat);
        } else {
            EmojiUtil.click(mInput, formatName);
        }
    }


}
