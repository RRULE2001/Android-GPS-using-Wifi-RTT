package com.example.tes_wifi_rtt;

//import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.WifiSsid;
import android.Manifest.permission;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private boolean mLocationPermissionApproved = false;
    private WifiManager mWifiManager;

    List<ScanResult> scanResults;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        TextView textCompatible = findViewById(R.id.textCompatible);
        TextView textView = findViewById(R.id.textView);

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            textCompatible.setText("Supported");
        }
        else {
            textCompatible.setText("Not Supported");
        }

        mLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        //RangingRequest.Builder builder = new RangingRequest.Builder();
        //builder.addAccessPoint(ap1ScanResult);
        //builder.addAccessPoint(ap2ScanResult);

        //RangingRequest req = builder.build();


        final Button button = findViewById(R.id.buttonRefresh);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (mLocationPermissionApproved) {
                    if (ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    scanResults = mWifiManager.getScanResults();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String string = "";
                        for(int i = 0; i < scanResults.size(); i++){
                            string += "\n" + ((scanResults.get(i)).getWifiSsid()).toString();
                        }

                        textView.setText(string);
                        //textCompatible.setText(((scanResults.get(0)).getWifiSsid()).toString());
                    }
                }
                else {
                    textCompatible.setText("Error Scanning");
                }
            }
        });


    }
}