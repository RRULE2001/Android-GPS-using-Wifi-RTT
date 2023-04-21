package com.example.tes_wifi_rtt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.view.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.*;
import android.Manifest;
import android.Manifest.permission;
import androidx.core.app.ActivityCompat;
import java.util.List;

public class MainActivity extends Activity {

    List<ScanResult> scanResults;
    public void setCompatible(Context context){
        TextView textCompatible = findViewById(R.id.textCompatible);

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            textCompatible.setText(R.string.supported);
            textCompatible.setTextColor(Color.GREEN);
        }
        else {
            textCompatible.setText(R.string.not_supported);
            textCompatible.setTextColor(Color.RED);
        }
    }
    public void scanWifi(Context context){
        TextView textError = findViewById(R.id.textError);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        boolean mLocationPermissionApproved = ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;

        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiRttManager mWifiRttManager = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        RangingRequest.Builder builder = new RangingRequest.Builder();

        linearLayout.removeAllViews();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Checks version is up to date
                String[] wifiMac = new String[10];
                int[] wifiSignalStrength = new int[10];
                int[] wifiDistance = new int[10];
                for(int i = 0; i < scanResults.size(); i++){ // Iterates through all scanned Wifi(s)
                    if((scanResults.get(i)).is80211mcResponder()) { // Checks that the router is RTT supported
                        builder.addAccessPoint(scanResults.get(i)); // Adds access point to request range
                    }
                }
                RangingRequest req = builder.build(); // Builds request for distances

                mWifiRttManager.startRanging(req, context.getMainExecutor(), new RangingResultCallback() {

                    @Override
                    public void onRangingFailure(int code) {
                        textError.setText(R.string.failure + code);
                        textError.setTextColor(Color.RED);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onRangingResults(List<RangingResult> results) {
                        textError.setText(R.string.success);
                        textError.setTextColor(Color.GREEN);
                        int index = 0;
                        for(int i = 0; i < results.size(); i++) {
                            if(results.get(i).getStatus() == RangingResult.STATUS_SUCCESS) { // If STATUS_SUCCESS
                                wifiMac[index] = String.valueOf(results.get(i).getMacAddress());
                                wifiSignalStrength[index] = results.get(i).getRssi();
                                wifiDistance[index++] = results.get(i).getDistanceMm();
                            }
                            else { // If STATUS_FAIL
                                wifiMac[i] = "FAIL";
                                wifiSignalStrength[i] = 0;
                                wifiDistance[i] = 0;
                            }
                        }
                        for( int i = 0; i < index; i++ )
                        {
                            TextView textView = new TextView(context);
                            ImageButton placeMapButton = new ImageButton(context);
                            placeMapButton.setBackgroundColor(0xFF2452A2); // Sets color to kettering blue
                            placeMapButton.setBackgroundResource(R.drawable.place_router_button);
                            placeMapButton.setImageResource(android.R.drawable.ic_dialog_map);

                            LinearLayout horizontalLayout = new LinearLayout(context);
                            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                            textView.setText(getString(R.string.mac) + wifiMac[i] + "\n"
                                    + getString(R.string.rssi) + wifiSignalStrength[i] + "\n"
                                    + getString(R.string.distance) + wifiDistance[i] + "\n"
                                    + getString(R.string.longitude) + 0 + "\n"
                                    + getString(R.string.latitude) + 0 + "\n");

                            textView.setTextSize(20);
                            textView.setTextColor(Color.BLACK);

                            linearLayout.addView(horizontalLayout);
                            horizontalLayout.addView(textView, 0);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                            layoutParams.setMargins(125,0,0,0);
                            horizontalLayout.addView(placeMapButton, 1, layoutParams);

                            placeMapButton.setOnClickListener(v -> textView.setTextColor(Color.GREEN));


                        }
                    }
                });
            }
        }
        else {
            textError.setText(R.string.permission_error);
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        ViewFlipper viewFlipper = findViewById(R.id.myViewFlipper);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setCompatible(context);

        ImageButton button = findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewFlipper.showNext();
            }
        });
    }

    Runnable r2=new Runnable() {
        @Override
        public void run() {
            //Your Toast
            Switch switch1 = findViewById(R.id.switch1);
            if (switch1.isChecked()) {
                Context context = getApplicationContext();
                scanWifi(context);
            }
            h2.postDelayed(r2,getResources().getInteger(R.integer.scan_delay));
        }
    };
    Handler h2=new Handler();
    @Override
    public void onResume() {
        super.onResume();
        h2.postDelayed(r2,getResources().getInteger(R.integer.scan_delay));
    }
    @Override
    public void onPause() {
        super.onPause();
        h2.removeCallbacks(r2);

    }
}