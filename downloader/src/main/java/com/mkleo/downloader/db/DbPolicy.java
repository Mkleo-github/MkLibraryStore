package com.mkleo.downloader.db;

public class DbPolicy {

    /* 数据库名称 */
    public static final String DB_NAME = "mk_downloader.db";
    /* 数据库版本 */
    public static final int DB_VERSION = 1;
    /* 任务表 */
    public static final String TB_TASK = "tb_task";
    /* 任务关联的包表 */
    public static final String TB_PACKETS = "tb_packets";


    //通过存储路径,来确定任务的唯一性
    public static final String PATH = "path";
    //下载地址
    public static final String URL = "url";
    //文件总大小
    public static final String SIZE = "size";
    //md5校验码
    public static final String MD5 = "md5";

    //包号
    public static final String NUMBER = "number";
    //开始位置
    public static final String BEGIN = "begin";
    //结束位置
    public static final String END = "end";
    //状态
    public static final String STATUS = "status";

    //创建任务表
    public static final String SQL_CREATE_TB_TASK = new StringBuffer()
            .append("create table ").append(TB_TASK)
            .append(" (")
            .append(PATH).append(" text,")
            .append(URL).append(" text,")
            .append(SIZE).append(" integer,")
            .append(MD5).append(" text")
            .append(")")
            .toString();

    public static final String SQL_CREATE_TB_PACKETS = new StringBuffer()
            .append("create table ").append(TB_PACKETS)
            .append(" (")
            .append(PATH).append(" text,")
            .append(NUMBER).append(" integer,")
            .append(BEGIN).append(" integer,")
            .append(END).append(" integer,")
            .append(STATUS).append(" integer")
            .append(")")
            .toString();

}
