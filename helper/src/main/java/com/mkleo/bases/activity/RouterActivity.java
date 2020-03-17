package com.mkleo.bases.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Map;

public abstract class RouterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //根布局
        RelativeLayout rootView = new RelativeLayout(this);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //滚动布局
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //添加滚动布局
        rootView.addView(scrollView);
        //
        LinearLayout lyLayout = new LinearLayout(this);
        lyLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lyLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(lyLayout);

        setContentView(rootView);

        TextView textView;

        Map<String, Class<? extends Activity>> routerMap = setTargetActivitys();
        for (Map.Entry<String, Class<? extends Activity>> entry : routerMap.entrySet()) {
            final String routerName = entry.getKey();
            final Class routerTarget = entry.getValue();

            textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setClickable(true);
            textView.setFocusable(true);
            textView.setText(routerName);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RouterActivity.this, routerTarget));
                }
            });
            lyLayout.addView(textView);

            //下划线
            textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            textView.setBackgroundColor(Color.BLACK);
            lyLayout.addView(textView);
        }

    }


    protected abstract Map<String, Class<? extends Activity>> setTargetActivitys();

}
