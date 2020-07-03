package com.indooratlas.android.sdk.examples.wayfinding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.examples.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class RangingActivity extends Activity implements BeaconConsumer{

    protected static final String TAG = "RangingActivity";
    public LatLng center;

    private BeaconManager beaconManager;
    LocationFinder locationFinder = new LocationFinder();

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.bind(this);

    }



    @Override

    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override

    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                int beacon_number = beacons.size();
                Beacon[] beacon_array = beacons.toArray(new Beacon[beacons.size()]);
                Beacon device1 = null, device2 = null, device3 = null;
                Constants constants = new Constants();
                float txPow1 = 0;
                double RSSI1Unfiltered = 0;
                double RSSI2Unfiltered = 0;
                float txPow2 = 0;
                double RSSI3Unfiltered = 0;
                float txPow3 = 0;
                if (beacon_number == 3) {
                    if (beacon_array[0].getIdentifier(0).toString() == constants.DEVICE1_UUID) {
                        device1 = beacon_array[0];
                    } else if (beacon_array[1].getIdentifier(0).toString() == constants.DEVICE1_UUID) {
                        device1 = beacon_array[1];
                    } else {
                        device1 = beacon_array[2];
                    }

                    if (beacon_array[0].getIdentifier(0).toString() == constants.DEVICE2_UUID) {
                        device2 = beacon_array[0];
                    } else if (beacon_array[1].getIdentifier(0).toString() == constants.DEVICE2_UUID) {
                        device2 = beacon_array[1];
                    } else {
                        device2 = beacon_array[2];
                    }

                    if (beacon_array[0].getIdentifier(0).toString() == constants.DEVICE3_UUID) {
                        device3 = beacon_array[0];
                    } else if (beacon_array[1].getIdentifier(0).toString() == constants.DEVICE3_UUID) {
                        device3 = beacon_array[1];
                    } else {
                        device3 = beacon_array[2];
                    }
                    RSSI1Unfiltered = device1.getRssi();
                    RSSI2Unfiltered = device2.getRssi();
                    RSSI3Unfiltered = device3.getRssi();

                    txPow1 = device1.getTxPower();
                    txPow2 = device2.getTxPower();
                    txPow3 = device3.getTxPower();
                    Log.d(TAG, "FLOAT!!!!!!!!" + txPow1);

                    //pass location
                    center = locationFinder.findLocation(RSSI1Unfiltered, txPow1, RSSI2Unfiltered, txPow2, RSSI3Unfiltered, txPow3);
                    Log.d(TAG,"beacon in reagion"+ beacons.size());
                    Log.d(TAG, "Current coordinates: asta e asta e !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + center.toString());


                }/* else if (beacon_number > 0) {
                    Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    for (int i = 0; i < beacon_number; i++) {
                        Beacon nextBeacon = beacon_array[i];
                        Log.d(TAG, "The next beacon " + nextBeacon.getIdentifier(0) + " is about " + nextBeacon.getDistance() + " meters away." + "RSSI is: " + nextBeacon.getRssi());
                        logToDisplay("The next beacon" + nextBeacon.getIdentifier(0) + " is about " + nextBeacon.getDistance() + " meters away." + "RSSI is: " + nextBeacon.getRssi());
                    }

                }*/





                   // Intent intent00 = new Intent(RangingActivity.this, WayfindingOverlayActivity.class);
                   // Bundle args = new Bundle();
                   // args.putParcelable("b", center);
                    //intent00.putExtras(args);
                   // startActivity(intent00);
                   // finish();



            }

            private void logToDisplay(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText editText = RangingActivity.this.findViewById(R.id.textView3);
                        editText.append(s+"\n");
                    }
                });
            }
        };

        try {

            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));

            beaconManager.addRangeNotifier(rangeNotifier);

        } catch (RemoteException e) {
        }

    }




/*
       RangingActivity(BlockingQueue q)
       {
           queue = q;
       }


        public void run() {

            LatLng res;
            try
            {
                res = center;
                queue.put(res);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        Object produce;
        }

*/

}