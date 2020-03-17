package com.mkleo.chat.bean;

/**
 * @Description: 文本内容
 * @author: WangHJin
 * @date: 2017/12/5 11:27
 */

public class Text implements IContent {

    private final String text;
    private long length = -1; //字数(长度) 可能用到


    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
