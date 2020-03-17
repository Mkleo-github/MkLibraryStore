package com.mkleo.tcp;

/**
 * @说明: 解析器
 * @作者: Wang HengJin
 * @日期: 2018/12/7 14:51 星期五
 */
public abstract class Receiver {

    protected abstract void onMessage(String text);

}
