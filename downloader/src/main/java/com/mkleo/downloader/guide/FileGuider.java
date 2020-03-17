package com.mkleo.downloader.guide;

import com.mkleo.downloader.Config;
import com.mkleo.downloader.DownloadError;
import com.mkleo.downloader.DownloadPolicy;
import com.mkleo.downloader.db.DaoFactory;
import com.mkleo.downloader.utils.Helper;

import java.io.File;

/**
 * 对本地文件进行校验
 */
public class FileGuider implements IGuider<Boolean> {

    private Config mConfig;

    FileGuider(Config config) {
        mConfig = config;
    }

    @Override
    public void guide(Callback<Boolean> callback) {
        try {
            //本地数据是否可用
            boolean isDataAvailable = false;
            //判断文件是否存在
            File file = new File(mConfig.getPath());
            if (file.exists()) {
                Helper.log("本地文件存在,开始校验文件");
                //判断MD5是否一致,不一致删除文件
                String md5Local = DaoFactory.getDownloadDao().getMd5(mConfig.getPath());
                String md5Remote = mConfig.getMd5();
                Helper.log("MD5校验 本地:" + md5Local);
                Helper.log("MD5校验 远程:" + md5Remote);
                if (null != md5Local
                        && md5Local.equals(md5Remote)) {
                    //校验成功
                    Helper.log("MD5校验成功,检测数据库分包数据");
                    int packetCounts = DaoFactory.getDownloadDao().getPacketCounts(mConfig.getPath());
                    Helper.log("本地分包总数:" + packetCounts);
                    if (packetCounts > 0) {
                        //说明已经分包完成
                        isDataAvailable = true;
                        Helper.log("本地数据库数据可用");
                    } else {
                        //分包异常本地文件存在,开始校验文件
                        Helper.log("分包异常,删除文件以及数据库数据");
                        file.delete();
                        DaoFactory.getDownloadDao().delete(mConfig.getPath());
                    }
                } else {
                    //校验失败
                    Helper.log("MD5校验失败,清除文件与数据记录");
                    file.delete();
                    DaoFactory.getDownloadDao().delete(mConfig.getPath());
                }
            } else {
                //文件不存在,删除数据库记录
                Helper.log("本地文件不存在,清除记录");
                DaoFactory.getDownloadDao().delete(mConfig.getPath());
            }

            if (null != callback)
                callback.onSuccess(isDataAvailable);
        } catch (Exception e) {
            if (null != callback)
                callback.onError(new DownloadError(DownloadPolicy.Error.GUIDE_ERROR, e.toString()));
        }


    }
}
