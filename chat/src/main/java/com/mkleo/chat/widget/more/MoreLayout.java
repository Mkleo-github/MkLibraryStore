package com.mkleo.chat.widget.more;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mkleo.chat.bean.Feature;
import com.mkleo.chat.widget.dotview.DotView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHJin on 2017/8/25.
 */

public class MoreLayout extends LinearLayout implements ViewPager.OnPageChangeListener {


    private ViewGroup mParent;
    private int mRow;//行数
    private int mCol;//列数


    private ViewPager mPager;
    private DotView mDotView;
    private List<Feature> mModels;
    private List<MorePage> mPages;


    private MoreLayout(Context context,
                       ViewGroup group,
                       int row,
                       int col) {
        super(context);
        this.mParent = group;
        this.mRow = row;
        this.mCol = col;

    }

    public void create(List<Feature> models) {

        if (models.size() == 0) return;//一个按钮都没有就不加入
        mModels = new ArrayList<>();
        mPages = new ArrayList<>();
        mModels.addAll(models);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        mParent.addView(this);

        //计算某些参数
        int pageBtnCount = mCol * mRow;//单页button个数
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
        List<Feature> pageModels;
        MorePage page;
        for (int i = 0; i < pageCount; i++) {
            pageModels = new ArrayList<>();
            if (i == pageCount - 1) {//最后一页
                for (int j = 0; j < allModelCount % pageBtnCount; j++) {
                    pageModels.add(mModels.get(pageBtnCount * i + j));
                }
            } else {
                for (int j = 0; j < pageBtnCount; j++) {
                    pageModels.add(mModels.get(pageBtnCount * i + j));
                }
            }
            page = new MorePage(getContext(), pageModels, mRow, mCol);//创建单页
            mPages.add(page);
        }
        //最后设置adapter
        mPager.setAdapter(new MorePagerAdpater(mPages));
        mPager.addOnPageChangeListener(this);
//        mPager.postInvalidate();//刷新

    }


    public static class Builder {
        private Context mContext;
        private ViewGroup mParent;
        private int mRow = 2; //行
        private int mCol = 4; //列


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

        public Builder setRow(int row) {
            this.mRow = row;
            return this;
        }

        public Builder setCol(int col) {
            this.mCol = col;
            return this;
        }

        public MoreLayout build() {
            return new MoreLayout(mContext, mParent, mRow, mCol);
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
