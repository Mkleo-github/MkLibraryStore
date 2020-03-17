package com.mkleo.librarys;

import android.app.Activity;

import com.mkleo.bases.activity.RouterActivity;
import com.mkleo.librarys.livedata.LiveDataActivity;
import com.mkleo.librarys.permissions.AndroidPermissionsActivity;
import com.mkleo.librarys.views.AndroidViewsActivity;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends RouterActivity {

    @Override
    protected Map<String, Class<? extends Activity>> setTargetActivitys() {
        return new LinkedHashMap<String, Class<? extends Activity>>() {
            {
                put("Android权限", AndroidPermissionsActivity.class);
                put("Android Views", AndroidViewsActivity.class);
                put("LiveData", LiveDataActivity.class);
            }
        };
    }
}

