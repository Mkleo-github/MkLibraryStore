package com.mkleo.chat.bean;

import java.io.File;

public class Record implements IContent {
    private final String path;      //存储路径
    private final String name;
    private final int length;       //文件时长
    private final int size;         //文件大小

    public Record(String path, int length) {
        this(path, new File(path).getName(), length, 0);
    }

    public Record(String path, String name, int length, int size) {
        this.path = path;
        this.name = name;
        this.length = length;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public int getLength() {
        return length;
    }
}
