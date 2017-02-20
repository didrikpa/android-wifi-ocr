package com.development.didrikpa.wifipasswordscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.development.didrikpa.wifipasswordscanner.R.id.scan_results;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private static final int PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 200;

    private static final String PUBLIC_STATIC_STRING_IDENTIFIER = "Wifi_Password";

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private List<String> SSIDs = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Switch wifiSwitch;
    private ListView scannedResults;

    private String password;
    private String ssid;
    private String authentication;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = wifiManager.getScanResults();
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
            authentication = scanResults.get(position).capabilities.split(" ")[0].split("-")[0].replaceAll("\\[", "");
            ssid = (String) parent.getItemAtPosition(position);

            final EditText editText = new EditText(MainActivity.this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setGravity(Gravity.CENTER);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(ssid);
            alertDialog.setMessage("Please enter password");
            alertDialog.setView(editText);

            if (authentication.equals("WPA") || authentication.equals("WPA2") || authentication.equals("WEP")) {
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        password = editText.getText().toString();
                        connectToWifi(authentication);
                        Toast.makeText(getApplicationContext(), "Connecting to " + ssid, Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancelled ", Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialog.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivityForResult(intent, 1);
                        Toast.makeText(getApplicationContext(), "Starting the camera", Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialog.show();
            } else if (authentication.equals("Open")) {
                connectToWifi(authentication);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SSIDs);

        wifiSwitch = (Switch) findViewById(R.id.switchWifi);
        scannedResults = (ListView) findViewById(scan_results);
        scannedResults.setAdapter(adapter);
        scannedResults.setOnItemClickListener(onItemClickListener);
        scannedResults.refreshDrawableState();

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        if (wifiManager.isWifiEnabled()) {
            wifiSwitch.setChecked(true);
        } else {
            wifiSwitch.setChecked(true);
        }

        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        } else {
            wifiManager.getScanResults();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeTessdata(){
        try {

            File mydir = this.getDir("tessdata", Context.MODE_PRIVATE);
            File traineddata = new File(mydir, "eng.traineddata");
            System.out.println(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
            System.out.println(traineddata.getCanonicalPath());
            FileOutputStream fileOutputStream = new FileOutputStream(traineddata); //openFileOutput("eng.traineddata", Context.MODE_PRIVATE);//new FileOutputStream(Environment.DIRECTORY_DOCUMENTS);
            InputStream inputStream = this.getAssets().open("tessdata/eng.traineddata");
            System.out.println(inputStream.available());
            byte[] buffer = new byte[1024];
            int length;
            while((length = inputStream.read(buffer))>0){
                fileOutputStream.write(buffer, 0, length);
            }
            try{
                inputStream.close();
            } catch(IOException e){
                Log.e(this.getLocalClassName(), e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        scannedResults.setAdapter(adapter);
        scannedResults.setClickable(true);
        scannedResults.setOnItemClickListener(onItemClickListener);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            wifiSwitch.setChecked(true);
            searchForWiFiNetworks();
        } else {
            SSIDs.clear();
            adapter.notifyDataSetChanged();
            wifiSwitch.setChecked(false);
        }

        System.out.println("HELLO");
        for (String a:Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).list()) {
            System.out.println(a);

        }
        initializeTessdata();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                password = data.getStringExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                System.out.println(password + "HEHEHE");
                connectToWifi(authentication);
            }
        }
    }

    public void turnWifiOnOrOff(View view) {
        if (wifiManager.isWifiEnabled()) {
            SSIDs.clear();
            adapter.notifyDataSetChanged();
            wifiManager.setWifiEnabled(false);
        } else if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            searchForWiFiNetworks();
        }
    }

    private void connectToWifi(String authentication) {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\"";

        switch (authentication) {
            case "WPA2":
            case "WPA":
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.preSharedKey = "\"" + password + "\"";
                break;
            case "WEP":
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.wepKeys[0] = "\"" + password + "\"";
                wifiConfiguration.wepTxKeyIndex = 0;
                break;
            case "Open":
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfiguration.allowedAuthAlgorithms.clear();
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                break;
        }
        wifiManager.addNetwork(wifiConfiguration);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    private void searchForWiFiNetworks() {
        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();
        SSIDs.clear();
        if (scanResults.size() > 0) {
            for (ScanResult result : scanResults) {
                if (!SSIDs.contains(result.SSID) && !result.SSID.equals("")) {
                    SSIDs.add(result.SSID);
                }
            }
        }
        adapter.notifyDataSetChanged();

    }

    public void searchForWifiNetworksButton(View view) {
        searchForWiFiNetworks();
    }
}