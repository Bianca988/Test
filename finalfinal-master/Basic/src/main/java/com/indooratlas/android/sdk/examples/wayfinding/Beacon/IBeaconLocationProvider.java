package com.indooratlas.android.sdk.examples.wayfinding.Beacon;

public abstract class IBeaconLocationProvider<B extends IBeacon> extends BeaconLocationProvider<B> {



    public IBeaconLocationProvider(B beacon) {

        super(beacon);

    }



}