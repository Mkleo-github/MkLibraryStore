package com.mkleo.chat.widget.more;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHJin on 2017/8/26.
 */

public class MorePage extends LinearLayout {
    /* 单页 */

    private int mCol;
    private int mRow;
    private List<Feature> mModels;
    private List<LinearLayout> mLayoutManager;//行布局

    public MorePage(Context context,
                    List<Feature> models,
                    int row,
                    int col) {
        super(context);
        this.mModels = models;
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
        MoreButton button;

        for (int i = 0; i < mRow; i++) {
            rowLayout = new LinearLayout(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
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
                button = new MoreButton(getContext());
                if (index < modelCount) {
                    button.bindModel(mModels.get(index));
                    index++;
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
        TextView line = new TextView(viewGroup.getContext());
        line.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundResource(R.color.colorTransDarkGrey);
        viewGroup.addView(line);
    }

    private void addLineVertical(ViewGroup viewGroup) {
        TextView line = new TextView(viewGroup.getContext());
        line.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT));
        line.setBackgroundResource(R.color.colorTransDarkGrey);
        viewGroup.addView(line);
    }
}
