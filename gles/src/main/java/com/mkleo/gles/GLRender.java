package com.mkleo.gles;

/**
 * des:
 * by: Mk.leo
 * date: 2019/4/17
 */
public interface GLRender {

    void onCreated();

    void onChanged(int width, int height);

    void onDraw();

}
