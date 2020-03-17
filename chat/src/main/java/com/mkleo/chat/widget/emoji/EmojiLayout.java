package com.mkleo.chat.widget.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mkleo.chat.bean.Emoji;
import com.mkleo.chat.utils.EmojiUtil;
import com.mkleo.chat.widget.dotview.DotView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHJin on 2017/8/25.
 */


public class EmojiLayout extends LinearLayout implements ViewPager.OnPageChangeListener {

    /**
     * 表情布局
     */
    private ViewGroup mParent;
    private EditText mInput;
    private int mRow;//行数
    private int mCol;//列数

    private DotView mDotView;
    private ViewPager mPager;
    private List<Emoji> mModels;
    private List<EmojiPage> mPages;
    private Emoji.Format mFormat;

    private EmojiLayout(Context context,
                        ViewGroup viewGroup,
                        EditText input,
                        int row,
                        int col) {
        super(context);
        this.mParent = viewGroup;
        this.mInput = input;
        this.mRow = row;
        this.mCol = col;
    }


    public EmojiLayout setFormat(Emoji.Format format) {
        EmojiUtil.init(format);
        this.mFormat = format;
        return this;
    }

    /**
     * 创建布局
     */
    public void create(List<Emoji> models) {
        if (models.size() == 0) return;//一个按钮都没有就不加入
        //创建图片缓存
        EmojiUtil.buildTable(getContext(), models);

        mModels = new ArrayList<>();
        mPages = new ArrayList<>();
        mModels.addAll(models);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        mParent.addView(this);

        int pageBtnCount = mCol * mRow - 1;//单页button个数,留出最后一个给删除按钮
        int allModelCount = mModels.size();
        int pageCount;
        if (allModelCount % pageBtnCount == 0) pageCount = allModelCount / pageBtnCount;
        else pageCount = allModelCount / pageBtnCount + 1;

        //创建一个viewpager
        mPager = new ViewPager(getContext());
        LayoutParams pagerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        pagerParams.weight = 1;
        mPager.setLayoutParams(pagerParams);
        this.addView(mPager);

        mDotView = new DotView(getContext(), pageCount);
        this.addView(mDotView);

        //获取页数后创建页面
        EmojiPage page;
        for (int i = 0; i < pageCount; i++) {
            List<Emoji> pageModels = new ArrayList<>();
            if (i == pageCount - 1) {//最后一页
                for (int j = 0; j < allModelCount % pageBtnCount; j++) {
                    pageModels.add(mModels.get(pageBtnCount * i + j));
                }
            } else {
                for (int j = 0; j < pageBtnCount; j++) {
                    pageModels.add(mModels.get(pageBtnCount * i + j));
                }
            }
            page = new EmojiPage(getContext(), pageModels, mInput, mFormat, mRow, mCol);//创建单页
            mPages.add(page);
        }
        //最后设置adapter
        mPager.setAdapter(new EmojiPagerAdpater(mPages));
        mPager.addOnPageChangeListener(this);
//        mPager.postInvalidate();//刷新
    }


    /**
     * Builder只用来做准备工作
     */
    public static class Builder {

        private final Context mContext;
        private ViewGroup mParent;
        private EditText mInput;
        private int mRow = 4; // 行(默认4行)
        private int mCol = 7; // 列(默认7列)

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * 设置父控件
         *
         * @param viewGroup
         * @return
         */
        public Builder setViewGroup(ViewGroup viewGroup) {
            this.mParent = viewGroup;
            return this;
        }

        /**
         * 绑定输入框
         *
         * @param input
         * @return
         */
        public Builder bindInput(EditText input) {
            this.mInput = input;
            return this;
        }


        public Builder setRow(int row) {
            this.mRow = row;
            return this;
        }

        public Builder setCol(int col) {
            this.mCol = col;
            return this;
        }

        public EmojiLayout build() {
            return new EmojiLayout(mContext, mParent, mInput, mRow, mCol);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mDotView != null) mDotView.select(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
