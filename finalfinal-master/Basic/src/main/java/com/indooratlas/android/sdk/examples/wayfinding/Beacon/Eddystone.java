package com.indooratlas.android.sdk.examples.wayfinding.Beacon;






public class Eddystone<P extends EddystoneAdvertisingPacket> extends Beacon<P> {

    public static final int CALIBRATION_DISTANCE_DEFAULT = 0;

    public Eddystone()
    {
        this.calibratedDistance = CALIBRATION_DISTANCE_DEFAULT;
    }
    @Override

    public BeaconLocationProvider<Eddystone<P>> createLocationProvider() {

        return new EddystoneLocationProvider<Eddystone<P>>( this ) {

            @Override

            protected void updateLocation() {

                // nope

            }



            @Override

            protected boolean canUpdateLocation() {

                return false;

            }

        };

    }
}
