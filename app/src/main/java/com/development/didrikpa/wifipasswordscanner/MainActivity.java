package com.development.didrikpa.wifipasswordscanner;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private List<String> SSIDs = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ToggleButton wifiToggle;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = wifiManager.getScanResults();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SSIDs);
        ListView scannedResults = (ListView) findViewById(R.id.scan_results);
        wifiToggle = (ToggleButton) findViewById(R.id.toggleWifi);
        scannedResults.setAdapter(adapter);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        if (wifiManager.isWifiEnabled()) {
            wifiToggle.setChecked(true);
        } else {
            wifiToggle.setChecked(false);
        }

        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        } else {
            wifiManager.getScanResults();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            wifiManager.getScanResults();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            wifiToggle.setChecked(true);
        } else {
            wifiToggle.setChecked(false);
        }
    }

    public void turnWifiOnOrOff(View view) {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        } else if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void searchForWifiNetworks(View view) {
        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();
        SSIDs.clear();
        if (scanResults.size() > 0) {
            for (ScanResult result : scanResults) {
                SSIDs.add(result.SSID);
            }


        }
        adapter.notifyDataSetChanged();
    }

    public void connectToWifiNetwork(View view) {


    }


}