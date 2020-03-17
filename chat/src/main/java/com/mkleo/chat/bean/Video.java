package com.mkleo.chat.bean;

import java.io.File;

/**
 * @Description: 视频内容
 * @author: WangHJin
 * @date: 2017/12/5 11:27
 */

public class Video implements IContent {

    private final String path;
    private final String name;
    private final int size;
    private final int width;
    private final int height;

    public Video(String path) {
        this(path, 0, 0, 0);
    }

    public Video(String path, int width, int height, int size) {
        this.path = path;
        this.name = new File(path).getName();
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
