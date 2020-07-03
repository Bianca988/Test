package com.indooratlas.android.sdk.examples;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.indooratlas.android.sdk.examples.imageview.ImageViewActivity;
import com.indooratlas.android.sdk.examples.wayfinding.MonitoringActivity;
import com.indooratlas.android.sdk.examples.wayfinding.RangingActivity;
import com.indooratlas.android.sdk.examples.wayfinding.WayfindingOverlayActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class Bianca extends Activity implements BootstrapNotifier {
    private static final String TAG = "RANGE";
    private RegionBootstrap regionBootstrap;
    private Button button;
    private BackgroundPowerSaver backgroundPowerSaver;

    private boolean haveDetectedBeaconsSinceBoot = false;

    private MonitoringActivity monitoringActivity = null;

    private String cumulativeLog = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
   //--------------------------------meniu -------------------------------
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAct();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAct2();
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAct3();
            }
        });

        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAct4();
            }
        });
    //-----------------------------meniu----------------------------------
    Notification.Builder builder = new Notification.Builder(this);
    Intent intent = new Intent(this,WayfindingOverlayActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pendingIntent);
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap((BootstrapNotifier) this, region);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    public void openAct()
    {
        Intent intent = new Intent(this, WayfindingOverlayActivity.class);
        startActivity(intent);
    }

    public void openAct2()
    {
        Intent intent2 = new Intent(this, RangingActivity.class);
        startActivity(intent2);
    }
    public void openAct3()
    {
        Intent intent4 = new Intent(this, ImageViewActivity.class);
        startActivity(intent4);
    }

    public void openAct4()
    {
        Intent intent5 = new Intent(this,RegionsActivity.class);
        startActivity(intent5);
    }
    public void disableMonitoring() {

        if (regionBootstrap != null) {

            regionBootstrap.disable();

            regionBootstrap = null;

        }

    }


    public void enableMonitoring() {

        Region region = new Region("backgroundRegion",

                null, null, null);

        regionBootstrap = new RegionBootstrap((BootstrapNotifier) this, region);

    }

    public void didEnterRegion(Region arg0) {

        // In this example, this class sends a notification to the user whenever a Beacon

        // matching a Region (defined above) are first seen.

        Log.d(TAG, "did enter region.");

        if (!haveDetectedBeaconsSinceBoot) {

            Log.d(TAG, "auto launching MainActivity");


            // The very first time since boot that we detect an beacon, we launch the

            // MainActivity

            Intent intent = new Intent(this, WayfindingOverlayActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Important:  make sure to add android:launchMode="singleInstance" in the manifest

            // to keep multiple copies of this activity from getting created if the user has

            // already manually launched the app.

            this.startActivity(intent);

            haveDetectedBeaconsSinceBoot = true;

        } else {

            if (monitoringActivity != null) {

                // If the Monitoring Activity is visible, we log info about the beacons we have

                // seen on its display

                Log.d(TAG, "I see a beacon again");

            } else {

                // If we have already seen beacons before, but the monitoring activity is not in

                // the foreground, we send a notification to the user on subsequent detections.

                Log.d(TAG, "Sending notification.");


            }

        }
    }


        public void didExitRegion(Region arg0) {

           Log.d(TAG,"I no longer see a beacon.");

        }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }


}



