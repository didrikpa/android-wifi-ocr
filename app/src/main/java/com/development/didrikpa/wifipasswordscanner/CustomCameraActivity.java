package com.development.didrikpa.wifipasswordscanner;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomCameraActivity extends Activity{

    CameraCaptureSession cameraCaptureSession = new CameraCaptureSession() {
        @NonNull
        @Override
        public CameraDevice getDevice() {
            return null;
        }

        @Override
        public void prepare(@NonNull Surface surface) throws CameraAccessException {

        }

        @Override
        public int capture(@NonNull CaptureRequest request, @Nullable CaptureCallback listener, @Nullable Handler handler) throws CameraAccessException {
            return 0;
        }

        @Override
        public int captureBurst(@NonNull List<CaptureRequest> requests, @Nullable CaptureCallback listener, @Nullable Handler handler) throws CameraAccessException {
            return 0;
        }

        @Override
        public int setRepeatingRequest(@NonNull CaptureRequest request, @Nullable CaptureCallback listener, @Nullable Handler handler) throws CameraAccessException {
            return 0;
        }

        @Override
        public int setRepeatingBurst(@NonNull List<CaptureRequest> requests, @Nullable CaptureCallback listener, @Nullable Handler handler) throws CameraAccessException {
            return 0;
        }

        @Override
        public void stopRepeating() throws CameraAccessException {

        }

        @Override
        public void abortCaptures() throws CameraAccessException {

        }

        @Override
        public boolean isReprocessable() {
            return false;
        }

        @Nullable
        @Override
        public Surface getInputSurface() {
            return null;
        }

        @Override
        public void close() {

        }
    };

}
