package com.mkleo.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;


public class FileUtil {


    /**
     * 删除
     *
     * @param path
     * @return
     */
    public static boolean delete(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }


    /**
     * 复制文件到目标目录
     *
     * @param source      源文件绝对路径
     * @param target      目标文件所在目录
     * @param isOverwirte 是否覆盖目标目录下的同名文件
     * @return boolean
     */
    public static boolean copy(String source, String target,
                               boolean isOverwirte) {
        boolean flag = false;

        File srcFile = new File(source);
        if (!srcFile.exists() || !srcFile.isFile()) { // 源文件不存在
            return false;
        }

        //获取待复制文件的文件名
        String fileName = srcFile.getName();
        String destPath = target + File.separator + fileName;
        File destFile = new File(destPath);
        if (destFile.getAbsolutePath().equals(srcFile.getAbsolutePath())) { // 源文件路径和目标文件路径重复
            return false;
        }
        if (destFile.exists() && !isOverwirte) {    // 目标目录下已有同名文件且不允许覆盖
            return false;
        }

        File destFileDir = new File(target);
        if (!destFileDir.exists() && !destFileDir.mkdirs()) { // 目录不存在并且创建目录失败直接返回
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fos.flush();
            fis.close();
            fos.close();

            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }


    /**
     * 类名: FileUtil
     * 描述: 剪切文件
     * 码农: Wang HengJin
     * 时间: 2018/4/2 10:08
     */
    public static boolean cut(String source, String target) {
        boolean flag = false;
        if (copy(source, target, true) && delete(source)) { // 复制和删除都成功
            flag = true;
        }
        return flag;
    }


    /**
     * 类名: FileUtil
     * 描述: 创建文件夹
     * 码农: Wang HengJin
     * 时间: 2018/4/14 11:37
     */
    public static boolean createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return true;
    }


    /**
     * 删除文件夹
     *
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            clearFolder(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File delPath = new File(filePath);
            delPath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空文件夹
     *
     * @param path
     * @return
     */
    public static boolean clearFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                boolean del = temp.delete();
            }
            if (temp.isDirectory()) {
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 获取文件MD5
     *
     * @param filePath
     * @return
     */
    public static String getMD5(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

}
