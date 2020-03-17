package com.mkleo.librarys.views;

import android.app.Activity;

import com.mkleo.bases.activity.RouterActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/28
 */
public class AndroidViewsActivity extends RouterActivity {


    @Override
    protected Map<String, Class<? extends Activity>> setTargetActivitys() {
        return new LinkedHashMap<String, Class<? extends Activity>>() {
            {
                put("RecyclerView", RecyclerViewActivity.class);
                put("Camera1", Camera1Activity.class);
                put("FalshView", FlashViewAcitivity.class);
            }
        };
    }
}
