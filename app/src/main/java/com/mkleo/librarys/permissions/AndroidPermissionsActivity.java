package com.mkleo.librarys.permissions;

import android.app.Activity;

import com.mkleo.bases.activity.RouterActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/28
 */
public class AndroidPermissionsActivity extends RouterActivity {


    @Override
    protected Map<String, Class<? extends Activity>> setTargetActivitys() {
        return new LinkedHashMap<String, Class<? extends Activity>>() {
            {
                put("EasyPermission", EasyPermissionActivity.class);
                put("RxPermission", RxPermissionActivity.class);
            }
        };
    }
}
