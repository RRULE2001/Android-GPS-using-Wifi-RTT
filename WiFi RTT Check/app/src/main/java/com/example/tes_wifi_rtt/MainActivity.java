package com.example.tes_wifi_rtt;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;
import android.content.pm.PackageManager;
import android.content.Context;

public class MainActivity extends Activity {

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);

        Context context = getApplicationContext();
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT))
            result.setText("Yes");
        else
            result.setText("No");
    }
}