package com.development.didrikpa.wifipasswordscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;


public class CameraActivity extends Activity {

    protected Button _button;
    protected ImageView _image;
    protected EditText _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";

    private static String PUBLIC_STATIC_STRING_IDENTIFIER = "Wifi_Password";
    private static String WIFI_PASSWORD = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        _image = (ImageView) findViewById(R.id.imageView);
        _field = (EditText) findViewById(R.id.editText);
        _button = (Button) findViewById(R.id.button2);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });

        _path = Environment.getExternalStorageDirectory() + "/DCIM/100ANDRO/make_machine_example.jpg";


    }

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                try {
                    onPhotoTaken();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CameraActivity.PHOTO_TAKEN, _taken);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            if (savedInstanceState.getBoolean(CameraActivity.PHOTO_TAKEN)) {
                onPhotoTaken();
            }

        } catch (Exception e) {
            Log.i("MakeMachine", "onRestoreInstanceState()");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onPhotoTaken() throws Exception {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        ExifInterface exif = new ExifInterface(_path);
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        _image.setImageBitmap(bitmap);


        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);

        File myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        baseAPI.init(myDir.toString(), "eng");
        baseAPI.setImage(bitmap);
        String recognizedText = baseAPI.getUTF8Text();
        _field.setText(recognizedText);
        _field.setClickable(true);
        System.out.println(recognizedText);
        baseAPI.end();

        File fdelete = new File("/DCIM/100ANDRO/make_machine_example.jpg");
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + "/DCIM/100ANDRO/make_machine_example.jpg");
            } else {
                System.out.println("file not Deleted :" + "/DCIM/100ANDRO/make_machine_example.jpg");
            }
        }
    }


    public void backToMain(View view) {
        Intent resultIntent = new Intent();
        WIFI_PASSWORD = _field.toString();
        resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER, WIFI_PASSWORD);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


}
