package com.example.tes_wifi_rtt;

//import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.*;
import android.Manifest.permission;
import android.widget.ListView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import java.util.List;

public class MainActivity extends Activity {
    final static int textSize = 20;
    final static int textColor = Color.WHITE;
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
        textCompatible.setTextColor(textColor);
        textCompatible.setTextSize(textSize);

        TextView textError = findViewById(R.id.textError);
        textError.setTextColor(textColor);
        textError.setTextSize(textSize);

        TextView textView1 = findViewById(R.id.textView);
        textView1.setTextColor(textColor);
        textView1.setTextSize(textSize);

        TextView textView2 = findViewById(R.id.textView2);
        textView2.setTextColor(textColor);
        textView2.setTextSize(textSize);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);


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

        WifiRttManager mWifiRttManager = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        final Button button = findViewById(R.id.buttonRefresh);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                        String[] wifiSignalStrength = new String[10];
                        int[] wifiDistance = new int[10];

                        for(int i = 0; i < scanResults.size(); i++){ // Iterates through all scanned Wifi(s)

                            if((scanResults.get(i)).is80211mcResponder()) { // Checks that the router is RTT supported
                                //wifiMac[j] = ((scanResults.get(i)).getApMldMacAddress()).toString();
                                builder.addAccessPoint(scanResults.get(i)); // Adds access point to request range
                            }
                        }
                        RangingRequest req = builder.build(); // Builds request for distances

                        mWifiRttManager.startRanging(req, context.getMainExecutor(), new RangingResultCallback() {

                            @Override
                            public void onRangingFailure(int code) {
                                textError.setText("Failure " + code);
                            }

                            @Override
                            public void onRangingResults(List<RangingResult> results) {
                                textError.setText("Success");
                                int index = 0;
                                for(int i = 0; i < results.size(); i++) {
                                    if(results.get(i).getStatus() == RangingResult.STATUS_SUCCESS) { // If STATUS_SUCCESS
                                        wifiMac[index] = String.valueOf(results.get(i).getMacAddress());
                                        wifiSignalStrength[index] = String.valueOf(results.get(i).getRssi());
                                        wifiDistance[index++] = results.get(i).getDistanceMm();
                                    }
                                    else { // If STATUS_FAIL
                                        wifiMac[i] = "FAIL";
                                        wifiSignalStrength[i] = "FAIL";
                                        wifiDistance[i] = 0;
                                    }
                                    System.out.println("MAC:" + wifiMac[i]);
                                    System.out.println("SignalStrength(dBm):" + wifiSignalStrength[i]);
                                    System.out.println("Distance(mm):" + wifiDistance[i]);
                                    System.out.println("Result Size:" + results.size());

                                }
                                for( int i = 0; i < index; i++ )
                                {
                                    TextView textView = new TextView(context);
                                    textView.setText("MAC: " + wifiMac[i] + "\n"
                                            + "SignalStrength(dBm): " + wifiSignalStrength[i] + "\n"
                                            + "Distance(mm): " + wifiDistance[i]);
                                    textView.setTextColor(textColor); // Sets color to white
                                    textView.setTextSize(textSize);
                                    linearLayout.addView(textView);
                                }
                            }
                        });
                    }
                }
                else {
                    textError.setText("Scanning Error");
                }
            }
        });


    }
}