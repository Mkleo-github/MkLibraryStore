package com.mkleo.chat.widget.emoji;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.bean.Emoji;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHJin on 2017/8/26.
 */

public class EmojiPage extends LinearLayout {
    /* 单页 */
    private final int mCol;
    private final int mRow;
    private final EditText mInput;
    private final List<Emoji> mModels;
    private final Emoji.Format mFormat;
    //行布局
    private List<LinearLayout> mLayoutManager;

    public EmojiPage(Context context,
                     List<Emoji> models,
                     EditText input,
                     Emoji.Format format,
                     int row,
                     int col) {
        super(context);
        this.mModels = models;
        this.mInput = input;
        this.mFormat = format;
        this.mRow = row;
        this.mCol = col;
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        initCreate();
    }


    /* 初始化创建 将外框架搭建 */
    private void initCreate() {
        mLayoutManager = new ArrayList<>();

        LinearLayout rowLayout;
        EmojiButton button;
        LayoutParams params;

        for (int i = 0; i < mRow; i++) {
            rowLayout = new LinearLayout(getContext());
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            rowLayout.setOrientation(HORIZONTAL);
            rowLayout.setLayoutParams(params);
            this.addView(rowLayout);
            mLayoutManager.add(rowLayout);//加入管理
            addLineHorizontal(this);
        }

        /* 首先创建建好所有的button */
        int modelCount = mModels.size();
        int index = 0;
        for (int i = 0; i < mRow; i++) {//行
            rowLayout = mLayoutManager.get(i);//获取该行的布局
            for (int j = 0; j < mCol; j++) {//列
                button = new EmojiButton(getContext(), mInput, mFormat);
                if (index < modelCount) {
                    button.bindModel(mModels.get(index));
                    index++;
                }

                if (i == mRow - 1 && j == mCol - 1) {
                    //说明是最后一个按钮
                    button.bindModel(new Emoji(Constants.DELETE, R.mipmap.face_delete));
                }
                rowLayout.addView(button);
                addLineVertical(rowLayout);
            }
        }
    }

    /**
     * 添加分割线
     *
     * @param viewGroup
     */
    private void addLineHorizontal(ViewGroup viewGroup) {
//        TextView line = new TextView(context);
//        line.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
//        line.setBackgroundResource(R.color.colorTransDarkGrey);
//        viewGroup.addView(line);
    }

    private void addLineVertical(ViewGroup viewGroup) {
//        TextView line = new TextView(context);
//        line.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT));
//        line.setBackgroundResource(R.color.colorTransDarkGrey);
//        viewGroup.addView(line);
    }

}
