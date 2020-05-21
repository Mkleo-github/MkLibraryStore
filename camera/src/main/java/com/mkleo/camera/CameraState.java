package com.mkleo.camera;


abstract class CameraState implements ICamera {

    ICamera mApi;

    CameraState(ICamera api) {
        this.mApi = api;
    }
}
