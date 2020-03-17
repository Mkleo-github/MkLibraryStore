package com.mkleo.chat.widget.dotview;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkleo.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHJin on 2017/9/2.
 */

public class DotView extends LinearLayout {
    /* 用于viewpagger的提示 */

    /* 单位都是dip */
    public static final int DEF_HEIGHT = 30;//默认高度
    public static final int DEF_DOT_SIZE = 6;
    public static final int MARGIN = 5;
    private static final int DOT_NOMAL = R.mipmap.icon_point_nomal;
    private static final int DOT_SELECT = R.mipmap.icon_point_select;


    private final Context context;
    private final int pageCount;//页数
    private int index = 0;//下标
    private List<ImageView> dots;

    public DotView(Context context, int pageCount) {
        super(context);
        this.context = context;
        this.pageCount = pageCount;
        if (pageCount == 0 || pageCount == 1) {//只有一页该布局不会显示
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            this.setLayoutParams(layoutParams);
            return;
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, DEF_HEIGHT));
        this.setGravity(Gravity.CENTER);
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        init();
    }

    private void init() {

        dots = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            ImageView dot = new ImageView(context);
            LayoutParams dotParams = new LayoutParams(dip2px(context, DEF_DOT_SIZE), dip2px(context, DEF_DOT_SIZE));
            dotParams.gravity = Gravity.CENTER_VERTICAL;
            dotParams.setMargins(dip2px(context, MARGIN), 0, dip2px(context, MARGIN), 0);
            dot.setLayoutParams(dotParams);
            if (i == 0) dot.setBackgroundResource(DOT_SELECT);
            else dot.setBackgroundResource(DOT_NOMAL);
            dots.add(dot);
            this.addView(dot);
        }
    }

    public void select(int toIndex) {
        if (toIndex < 0 || toIndex > pageCount) return;
        ImageView currentDot = dots.get(index);
        ImageView toDot = dots.get(toIndex);
        playChange(currentDot, toDot);
        index = toIndex;
    }


    private Animation nomalAnim;
    private final long duration = 100;
    private final float SCALE = 0.0f;

    private void playChange(final ImageView currentDot, final ImageView toDot) {
        currentDot.setVisibility(INVISIBLE);//刷新状态
        currentDot.setVisibility(VISIBLE);
        nomalAnim = new ScaleAnimation(1f, SCALE, 1f, SCALE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        nomalAnim.setDuration(duration);
        nomalAnim.setAnimationListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                toSelectAnim(toDot);
                currentDot.setBackgroundResource(DOT_NOMAL);
                nomalAnim = new ScaleAnimation(SCALE, 1f, SCALE, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                nomalAnim.setDuration(duration);
                currentDot.setAnimation(nomalAnim);
                nomalAnim.start();

            }
        });
        currentDot.setAnimation(nomalAnim);
        nomalAnim.start();
    }

    private Animation selectAnim;

    private void toSelectAnim(final ImageView dot) {
        dot.setVisibility(INVISIBLE);
        dot.setVisibility(VISIBLE);
        selectAnim = new ScaleAnimation(1f, SCALE, 1f, SCALE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        selectAnim.setDuration(duration);
        selectAnim.setAnimationListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                dot.setBackgroundResource(DOT_SELECT);
                selectAnim = new ScaleAnimation(SCALE, 1f, SCALE, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                selectAnim.setDuration(duration);
                dot.setAnimation(selectAnim);
                selectAnim.start();
            }
        });
        dot.setAnimation(selectAnim);
        selectAnim.start();
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private abstract class SimpleAnimListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
