package com.mkleo.librarys;

import android.app.Application;

/**
 * des:
 * by: Mk.leo
 * date: 2019/7/17
 */
public class MkApplication extends Application {

    public static final String PATH_GEN  = "/mnt/sdcard/AMkLibrarys";

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.setOnCrashListener(new CrashHandler.OnCrashListener() {
//            @Override
//            public void onCrash(String crashMsg) {
//                MkLog.print(crashMsg);
//            }
//        });
    }
}
