package com.indooratlas.android.sdk.examples.wayfinding.Beacon;



public abstract class EddystoneLocationProvider<B extends Eddystone> extends BeaconLocationProvider<B> {



    public EddystoneLocationProvider(B beacon) {

        super(beacon);

    }



}