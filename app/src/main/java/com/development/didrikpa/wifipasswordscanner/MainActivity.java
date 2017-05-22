package com.development.didrikpa.wifipasswordscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private static final int PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 200;
    private static final int PERMISSIONS_REQUEST_CODE_CAMERA = 1;

    private Button search, qr, qr_password, ssid_password;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        search = (Button) findViewById(R.id.select);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        qr = (Button) findViewById(R.id.qrw);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        qr_password = (Button) findViewById(R.id.qrp);
        qr_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ssid_password = (Button) findViewById(R.id.ssidpass);
        ssid_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE_CAMERA);
        }

        initializeTessdata();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeTessdata() {
        try {

            File tessdataDir = new File(getExternalCacheDir(), "tessdata");
            boolean isCreated = tessdataDir.mkdir();
            File traineddata = new File(tessdataDir, "eng.traineddata");
            Uri.fromFile(traineddata);
            FileOutputStream fileOutputStream = new FileOutputStream(traineddata);
            InputStream inputStream = this.getAssets().open("tessdata/eng.traineddata");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.getCause();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("WifiManagerLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
    }

    public void goToSearchView(View view){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("activity", "main");
        startActivityForResult(intent, 1);
    }


}