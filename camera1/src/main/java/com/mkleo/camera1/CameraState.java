package com.mkleo.camera1;


abstract class CameraState implements ICamera {

    ICamera mApi;

    CameraState(ICamera api) {
        this.mApi = api;
    }
}
