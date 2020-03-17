package com.mkleo.chat.bean;

/**
 * 功能
 */
public class Feature {

    public interface Callback {
        void onClick(String name);
    }

    private final String name;
    private final int image;
    private final Callback callback;

    public Feature(String name, int image, Callback callback) {
        this.name = name;
        this.image = image;
        this.callback = callback;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Callback getCallback() {
        return callback;
    }
}
