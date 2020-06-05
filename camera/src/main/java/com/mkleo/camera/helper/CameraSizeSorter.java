package com.mkleo.camera.helper;

import com.mkleo.camera.ICamera;

import java.util.Comparator;

public class CameraSizeSorter implements Comparator<ICamera.Size> {

    //是否升序
    private boolean isAscendingOrder;

    public CameraSizeSorter() {
        this(true);
    }

    public CameraSizeSorter(boolean isAscendingOrder) {
        this.isAscendingOrder = isAscendingOrder;
    }

    @Override
    public int compare(ICamera.Size size1, ICamera.Size size2) {
        if (isAscendingOrder) {
            return size1.getWidth() * size1.getHeight() -
                    size2.getWidth() * size2.getHeight();
        } else {
            return size2.getWidth() * size2.getHeight() -
                    size1.getWidth() * size1.getHeight();
        }
    }
}
