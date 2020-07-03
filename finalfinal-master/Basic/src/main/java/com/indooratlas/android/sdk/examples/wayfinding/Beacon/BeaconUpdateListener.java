package com.indooratlas.android.sdk.examples.wayfinding.Beacon;


public interface BeaconUpdateListener<B extends Beacon> {



    void onBeaconUpdated(B beacon);



}