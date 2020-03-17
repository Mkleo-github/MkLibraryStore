package com.mkleo.downloader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mkleo.downloader.model.DownloadInfo;
import com.mkleo.downloader.model.DownloadPacket;

import java.util.Vector;


public class DownloadDao {

    private DbHelper mDbHelper;

    DownloadDao(Context context) {
        mDbHelper = new DbHelper(context.getApplicationContext());
    }


    /**
     * 插入下载数据
     *
     * @param info
     */
    public synchronized void insert(DownloadInfo info) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = new StringBuffer()
                .append("insert into ").append(DbPolicy.TB_TASK)
                .append(" (")
                .append(DbPolicy.PATH).append(",")
                .append(DbPolicy.URL).append(",")
                .append(DbPolicy.SIZE).append(",")
                .append(DbPolicy.MD5).append(")")
                .append(" values(?,?,?,?)")
                .toString();
        db.execSQL(sql, new Object[]{
                info.getPath(),
                info.getUrl(),
                info.getSize(),
                info.getMd5()
        });
        db.close();
    }

    /**
     * 插入包数据
     *
     * @param packet
     */
    public synchronized void insert(DownloadPacket packet) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = new StringBuffer()
                .append("insert into ").append(DbPolicy.TB_PACKETS)
                .append(" (")
                .append(DbPolicy.PATH).append(",")
                .append(DbPolicy.NUMBER).append(",")
                .append(DbPolicy.BEGIN).append(",")
                .append(DbPolicy.END).append(",")
                .append(DbPolicy.STATUS).append(")")
                .append(" values(?,?,?,?,?)")
                .toString();

        db.execSQL(sql, new Object[]{
                packet.getPath(),
                packet.getNumber(),
                packet.getBegin(),
                packet.getEnd(),
                packet.getStatus()
        });
        db.close();
    }


    /**
     * 更新包状态
     *
     * @param packet
     */
    public synchronized void update(DownloadPacket packet) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = new StringBuffer()
                .append("update ").append(DbPolicy.TB_PACKETS)
                .append(" set ").append(DbPolicy.STATUS).append(" = ?")
                .append(" where ").append(DbPolicy.NUMBER).append(" = ?")
                .append(" and ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        db.execSQL(sql, new Object[]{
                packet.getStatus(),
                packet.getNumber(),
                packet.getPath()
        });
        db.close();
    }


    /**
     * 获取md5码
     *
     * @param path
     * @return
     */
    public synchronized String getMd5(String path) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = new StringBuffer()
                .append("select (").append(DbPolicy.MD5).append(") ")
                .append("from ").append(DbPolicy.TB_TASK)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{
                path
        });
        cursor.moveToFirst();
        String md5 = null;
        if (cursor.getCount() > 0) {
            md5 = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return md5;
    }


    /**
     * 获取下载任务信息
     *
     * @param path
     * @return
     */
    public synchronized DownloadInfo getDownloadInfo(String path) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = new StringBuffer()
                .append("select * from ").append(DbPolicy.TB_TASK)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{
                path
        });
        cursor.moveToFirst();
        String selectUrl = cursor.getString(cursor.getColumnIndex(DbPolicy.URL));
        String selectPath = cursor.getString(cursor.getColumnIndex(DbPolicy.PATH));
        String selectmd5 = cursor.getString(cursor.getColumnIndex(DbPolicy.MD5));
        int selectSize = cursor.getInt(cursor.getColumnIndex(DbPolicy.SIZE));

        DownloadInfo info = new DownloadInfo(
                selectUrl, selectPath, selectSize, selectmd5
        );
        cursor.close();
        db.close();
        return info;
    }

    /**
     * 获取包信息
     *
     * @param path
     * @return
     */
    public synchronized Vector<DownloadPacket> getDownloadPackets(String path) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Vector<DownloadPacket> packets = new Vector<>();
        String sql = new StringBuffer()
                .append("select * from ").append(DbPolicy.TB_PACKETS)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{
                path
        });
        cursor.moveToFirst();
        DownloadPacket packet;
        while (cursor.moveToNext()) {
            packet = new DownloadPacket(
                    cursor.getString(cursor.getColumnIndex(DbPolicy.PATH)),
                    cursor.getInt(cursor.getColumnIndex(DbPolicy.NUMBER)),
                    cursor.getLong(cursor.getColumnIndex(DbPolicy.BEGIN)),
                    cursor.getLong(cursor.getColumnIndex(DbPolicy.END)),
                    cursor.getInt(cursor.getColumnIndex(DbPolicy.STATUS))
            );
            packets.add(packet);
        }
        cursor.close();
        db.close();
        return packets;
    }


    /**
     * 获取包总数
     *
     * @param path
     * @return
     */
    public synchronized int getPacketCounts(String path) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = new StringBuffer()
                .append("select count(*) from ").append(DbPolicy.TB_PACKETS)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        Cursor cursor = db.rawQuery(sql, new String[]{
                path
        });
        cursor.moveToFirst();
        int counts = 0;
        if (cursor.getCount() > 0) {
            counts = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return counts;
    }

    /**
     * 通过主键删除(合并删除)
     *
     * @param path
     */
    public synchronized void delete(String path) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //删除任务记录
        String sql = new StringBuffer()
                .append("delete from ").append(DbPolicy.TB_TASK)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        db.execSQL(sql, new Object[]{
                path
        });
        //删除包记录
        sql = new StringBuffer()
                .append("delete from ").append(DbPolicy.TB_PACKETS)
                .append(" where ").append(DbPolicy.PATH).append(" = ?")
                .toString();
        db.execSQL(sql, new Object[]{
                path
        });

        db.close();
    }


}
