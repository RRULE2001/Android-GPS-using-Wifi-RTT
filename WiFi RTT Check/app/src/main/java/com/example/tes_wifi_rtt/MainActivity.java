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

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends Activity {

    List<ScanResult> scanResults;

    GPSCoreAPI coreAPI = new GPSCoreAPI();

    int clearCounter = 0;
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

        RangingRequest req = builder.build(); // Builds request for distances

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
                boolean rttRoutersAvailable = false;

                for (int i = 0; i < scanResults.size(); i++) { // Iterates through all scanned Wifi(s)
                    if ((scanResults.get(i)).is80211mcResponder()) { // Checks that the router is RTT supported
                        builder.addAccessPoint(scanResults.get(i)); // Adds access point to request range
                        rttRoutersAvailable = true;
                    }
                }
                scanResults.clear();
                if (rttRoutersAvailable) {
                    rttRoutersAvailable = false;
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
                            for (int i = 0; i < results.size(); i++) {
                                if (results.get(i).getStatus() == RangingResult.STATUS_SUCCESS) { // If STATUS_SUCCESS
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
                            if(index > 0) {
                                linearLayout.removeAllViews();
                            }

                            for (int i = 0; i < index; i++) {
                                coreAPI.appendRouterList(wifiDistance[i], wifiMac[i], wifiSignalStrength[i], context); // Adds router to list of routers
                            }

                            for (int i = 0; i < index; i++) {
                                TextView textView = new TextView(context);
                                textView.setTextSize(20);
                                textView.setTextColor(Color.BLACK);
                                /*textView.setText(getString(R.string.mac)    + wifiMac[i] + "\n"
                                        + getString(R.string.rssi)          + wifiSignalStrength[i] + "\n"
                                        + getString(R.string.distance)      + wifiDistance[i] + "\n"
                                        + getString(R.string.longitude)     + 0 + "\n"
                                        + getString(R.string.latitude)      + 0 + "\n");*/

                                textView.setText(getString(R.string.mac)    + coreAPI.getRouterMACAddr(i) + "\n"
                                        + getString(R.string.rssi)          + coreAPI.getRouterRssi(i) + "\n"
                                        + getString(R.string.distance)      + coreAPI.getRouterDist(i) + "\n"
                                        + getString(R.string.longitude)     + coreAPI.getRouterX(i) + "\n"
                                        + getString(R.string.latitude)      + coreAPI.getRouterY(i) + "\n");

                                LinearLayout horizontalLayout = new LinearLayout(context);
                                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                                linearLayout.addView(horizontalLayout);
                                horizontalLayout.addView(textView, 0);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                                layoutParams.setMargins(125, 0, 0, 0);


                                // Handling of button
                                ImageButton placeMapButton = new ImageButton(context);
                                placeMapButton.setBackgroundColor(0xFF2452A2); // Sets color to kettering blue
                                placeMapButton.setBackgroundResource(R.drawable.place_router_button);
                                placeMapButton.setImageResource(android.R.drawable.ic_dialog_map);
                                horizontalLayout.addView(placeMapButton, 1, layoutParams);
                                placeMapButton.setOnClickListener(v -> textView.setTextColor(Color.GREEN));

                            }
                        }
                    });
                }
                else {
                    textError.setText(R.string.no_rtt_error);
                    textError.setTextColor(Color.RED);
                }
            }
        }
        else {
            textError.setText(R.string.permission_error);
            textError.setTextColor(Color.RED);
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

        ImageView map = findViewById(R.id.map);

        ImageButton button = findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewFlipper.showNext();
            }
        });

        Button buttonZoomIn = findViewById(R.id.buttonZoomIn);

        buttonZoomIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get the map ImageView
                ImageView map = findViewById(R.id.map);

                // Get the current scale of the map
                float currentScale = map.getScaleX();

                // Increase the scale by a factor of 1.2
                float newScale = currentScale * 1.2f;

                float maxScale = 4f;

                if (newScale > maxScale) {
                    newScale = maxScale;
                }

                // Set the new scale for the map
                map.setScaleX(newScale);
                map.setScaleY(newScale);

            }
        });

        Button buttonZoomOut = findViewById(R.id.buttonZoomOut);

        buttonZoomOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get the map ImageView
                ImageView map = findViewById(R.id.map);

                // Get the current scale of the map
                float currentScale = map.getScaleX();

                // Decrease the scale by a factor of 1.2
                float newScale = currentScale / 1.2f;

                float minScale = 1f;

                if (newScale < minScale) {
                    newScale = minScale;
                }

                // Set the new scale for the map
                map.setScaleX(newScale);
                map.setScaleY(newScale);
            }
        });
    }

    public void moveMap(int x, int y) {
        int offsetX = x; // offset not yet determined
        int offsetY = y;
        ImageView map = findViewById(R.id.map);
        View rootView = findViewById(android.R.id.content).getRootView();
        int newX = map.getLeft() + offsetX;
        int newY = map.getTop() + offsetY;
        int maxX = rootView.getWidth() - map.getWidth();
        int maxY = rootView.getHeight() - map.getHeight();
        newX = Math.min(Math.max(0, newX), maxX);
        newY = Math.min(Math.max(0, newY), maxY);
        map.layout(newX, newY, newX + map.getWidth(), newY + map.getHeight());
    }

    Runnable r2=new Runnable() {
        @Override
        public void run() {

            Switch switch1 = findViewById(R.id.switch1);
            if (switch1.isChecked()) {
                Context context = getApplicationContext();
                scanWifi(context);

                double[] userPosition = coreAPI.calculatePosition();

                if(userPosition[0] > 0) {
                    moveMapplease((int) userPosition[0], (int) userPosition[1]);
                }

                TextView position = findViewById(R.id.positionText);
                position.setText("(" + String.format("%.2f", userPosition[0]) + "," + String.format("%.2f", userPosition[1]) + ")");
                System.out.println("DATAREADING: " + userPosition[0] + "," + userPosition[1]);
            }
            clearCounter++;
            if(clearCounter >= 10){
                coreAPI.clearRouterList();
                System.out.println("Clear Router List");
                clearCounter = 0;
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

    public void moveMapplease(float x, float y) {
        float xDP = x*13/2.54f; // offset not yet determined
        float yDP = y*13/2.54f;

        //float xPositionDP =  410/2 - xDP;
        //float yPositionDP =  630/2 - yDP;

        float xPositionDP =  380/2 - xDP;
        float yPositionDP =  760/2 - yDP;

        //float xPositionDP = xDP;
        //float yPositionDP = yDP;

        //ImageView map = findViewById(R.id.map);
        //View rootView = findViewById(android.R.id.content).getRootView();

        final float scale = getResources().getDisplayMetrics().density;

        int xPopsitionPx = (int) (xPositionDP * scale + 0.5f);
        int yPopsitionPx = (int) (yPositionDP * scale + 0.5f);

        FrameLayout mapContainer = findViewById(R.id.mapContainer);

        //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mapContainer.getWidth(), mapContainer.getHeight());
        //lp.leftMargin = xPopsitionPx;
        //lp.topMargin = yPopsitionPx;
        //lp.setMargins((int)xPopsitionPx, (int)yPopsitionPx, 0, 0);
        //map.setLayoutParams(lp);
        mapContainer.setX((int)xPopsitionPx);
        mapContainer.setY((int)yPopsitionPx);


        //map.setPadding(xPopsitionPx, yPopsitionPx, 0 ,0);

        //LinearLayout player = findViewById(R.id.playerContainer);
        //player.setX(xPopsitionPx);
        //player.setY(yPopsitionPx);

        /*
        float newX = map.getLeft() + xPosition;
        float newY = map.getTop() + yPosition;
        float maxX = rootView.getWidth() - map.getWidth();
        float maxY = rootView.getHeight() - map.getHeight();
        newX = Math.min(Math.max(0, newX), maxX);
        newY = Math.min(Math.max(0, newY), maxY);
        map.layout((int)newX, (int)newY, (int)newX + map.getWidth(), (int)newY + map.getHeight());*/
    }

    public void placeRouter(Context context){
        ConstraintSet constraints = new ConstraintSet();
        ImageButton router = new ImageButton(context);
        router.setImageResource(R.drawable.round_button);

        router.setBackgroundColor(0xFF2452A2); // Sets color to kettering blue
        router.setBackgroundResource(R.drawable.place_router_button);
        router.setImageResource(android.R.drawable.ic_dialog_map);


        //float[] positionMeters = this.lookupTable.get(MACAddr);
        //float xPositionDP = positionMeters[0]*25/2.54f;
        //float yPositionDP = positionMeters[1]*25/2.336f;

        //final float scale = getResources().getDisplayMetrics().density;

        //int xPopsitionPx = (int) (xPositionDP * scale + 0.5f);
        //int yPopsitionPx = (int) (yPositionDP * scale + 0.5f);

        //router.setTop(200);
        //router.setLeft(200);
        //router.setPadding(200,0,0,0);
        //router.setX((int)xPositionDP);
        //router.setY((int)yPositionDP);

        router.setX(0);
        router.setY(0);
    }
}