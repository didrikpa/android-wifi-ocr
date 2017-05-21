package com.development.didrikpa.wifipasswordscanner.camera;


import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

public class CustomCameraActivity extends Activity {

    public CameraManager manager;

    @Override
    public void onCreate(Bundle savedInstsanceState){
        super.onCreate(savedInstsanceState);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    }






}
