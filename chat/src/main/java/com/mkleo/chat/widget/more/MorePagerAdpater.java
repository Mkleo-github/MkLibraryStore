package com.mkleo.chat.widget.more;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by WangHJin on 2017/8/26.
 */

public class MorePagerAdpater extends PagerAdapter {

    private final List<MorePage> mPages;

    public MorePagerAdpater(List<MorePage> pages) {
        this.mPages = pages;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mPages.get(position));
        return mPages.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mPages.get(position));//删除页卡
    }
}
