package com.mkleo.downloader.db;

import android.content.Context;

public class DaoFactory {

    private static DownloadDao sDownloadDao;

    public static void init(Context context) {
        if (null == sDownloadDao)
            sDownloadDao = new DownloadDao(context.getApplicationContext());
    }

    public static DownloadDao getDownloadDao() {
        return sDownloadDao;
    }

}
