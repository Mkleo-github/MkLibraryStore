package com.mkleo.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {


    DbHelper(Context context) {
        super(context, DbPolicy.DB_NAME, null, DbPolicy.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(DbPolicy.SQL_CREATE_TB_TASK);
        db.execSQL(DbPolicy.SQL_CREATE_TB_PACKETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
