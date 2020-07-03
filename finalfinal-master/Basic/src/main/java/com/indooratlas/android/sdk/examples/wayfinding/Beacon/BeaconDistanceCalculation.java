package com.indooratlas.android.sdk.examples.wayfinding.Beacon;

import com.indooratlas.android.sdk.examples.wayfinding.Beacon.Beacon;
import com.indooratlas.android.sdk.examples.wayfinding.Beacon.IBeacon;
import com.indooratlas.android.sdk.examples.wayfinding.Beacon.Eddystone;




public abstract class BeaconDistanceCalculation {

    public static final float PATH_LOSS_PARAMETER_OPEN_SPACE = 2;
    public static final float PATH_LOSS_PARAMETER_INDOOR = 1.7f;
    public static final float PATH_LOSS_PARAMETER_OFFICE_HARD_PARTITION = 3f;
    public static final int CALIBRATED_RSSI_AT_ONE_METER = -62;
    public static final int SIGNAL_LOSS_AT_ONE_METER = -41;
    private static float pathLossParameter = PATH_LOSS_PARAMETER_OFFICE_HARD_PARTITION;

    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistanceTo(beacon,beacon.getFilteredRssi());
    }

    public static float calculateDist(Beacon beacon, float rssi, double device) {
        float distance = calculateDistanceTo(beacon, rssi);
        if (beacon.hasLocation() && beacon.getLocation().hasElevation()) {
            double elevD = Math.abs(beacon.getLocation().getElevation() - device);
            if (elevD > 0 && distance > (elevD * 2)) {
                double delta = Math.pow(distance, 2) - Math.pow(elevD, 2);
                return (float) Math.sqrt(delta);
            } else {
                return distance;
            }
        } else {
            return distance;
        }
    }
    public static float calculateDistanceTo(Beacon beacon , float rssi)
    {
        return calculateDistance(rssi,beacon.getCalibratedRssi(), beacon.getCalibratedDistance(),pathLossParameter);

    }

    public static float getCalibratedRssiAtOneMeter(float calibratedRssi , float calibratedDistance)
    {
        float calibratedRssiAtOneMeter ;
        if(calibratedDistance == IBeacon.CALIBRATION_DISTANCE_DEFAULT)
        {
            calibratedRssiAtOneMeter = calibratedRssi;
        }else if(calibratedDistance == Eddystone.CALIBRATION_DISTANCE_DEFAULT)
        {
            calibratedRssiAtOneMeter = calibratedRssi + SIGNAL_LOSS_AT_ONE_METER;
        }else {
            calibratedRssiAtOneMeter = CALIBRATED_RSSI_AT_ONE_METER;
        }
        return calibratedRssiAtOneMeter;
    }
    public static float calculateDistance(float rssi, float calibratedRssi , int calibratedDistance,float pathLossParam)
    {
        return calculateDistance(rssi, getCalibratedRssiAtOneMeter(calibratedRssi, calibratedDistance), pathLossParameter);
    }
    public static float calculateDistance(float rssi , float calibratedRssi,float pathLossParameter)
    {
        return (float) Math.pow(10,(calibratedRssi-rssi)/(10*pathLossParameter));
    }

    public static void setPathLossParameter(float pathLossParameter)
    {
        BeaconDistanceCalculation.setPathLossParameter(pathLossParameter);
    }

    public static float getPathLossParameter ()
    {
        return BeaconDistanceCalculation.getPathLossParameter();
    }
}

