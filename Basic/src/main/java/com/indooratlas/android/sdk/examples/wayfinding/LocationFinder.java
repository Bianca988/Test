package com.indooratlas.android.sdk.examples.wayfinding;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static org.altbeacon.beacon.service.BeaconService.TAG;

/**
 * Wrapper class that is used to compute the location of the device running the application
 * based on the distances to the Bluetooth beacons
 */
public class LocationFinder  {

    // Kalman R & Q
    private static final double KALMAN_R = 0.125d;
    private static final double KALMAN_Q = 0.5d;

    private List<Double> convertGpsToECEF(double lat, double longi) {
        double a = 6378.1;
        double b = 6356.8;
        double N;

        double e = 1 - (Math.pow(b, 2) / Math.pow(a, 2));
        N = a / (Math.sqrt(1.0 - (e * Math.pow(Math.sin(Math.toRadians(lat)), 2))));
        double cosLatRad = Math.cos(Math.toRadians(lat));
        double cosLongiRad = Math.cos(Math.toRadians(longi));
        double sinLatRad = Math.sin(Math.toRadians(lat));
        double sinLongiRad = Math.sin(Math.toRadians(longi));

        double x = (N + 0.001) * cosLatRad * cosLongiRad;
        double y = (N + 0.001) * cosLatRad * sinLongiRad;

        List<Double> ecef = new ArrayList<>();
        ecef.add(x);
        ecef.add(y);

        return ecef;

    }

    private LatLng getLocationbyTrilateration( LatLng poz1 , double distance1 , LatLng poz2 , double distance2 , LatLng poz3 , double distance3)
    {
        //declaring variables

        double[] p1 = new double[2];
        double[] p2 = new double[2];
        double[] p3 = new double[2];
        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];

        double jval = 0 ;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        double triptx , ttripty , xval , yval ,t1,t2,t3,t,exx,d,eyy ;
        // translate points to vectors :
        //poin1:

        p1[0] = poz1.latitude;
        p1[1] = poz1.longitude;

        p2[0] = poz2.latitude;
        p2[1] = poz2.longitude;

        p3[0] = poz3.latitude;
        p3[1] = poz3.longitude;

        distance1 = (distance1/100000);
        distance2 = (distance2/100000);
        distance3 = (distance3/100000);

        for(int i = 0 ; i<p1.length;i++)
        {

            t1 = p2[i];
            t2 = p1[i];
            t = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < p1.length; i++) {
            t1 = p2[i];
            t2 = p1[i];
            exx = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < p3.length; i++) {
            t1 = p3[i];
            t2 = p1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int i=0;i<p3.length;i++)
        {
            t1=p3[i];
            t2=p1[i];
            t3=ex[i]*ival;
            t=t1-t2-t3;
            p3p1i += (t*t);
        }
        for(int i =0; i<p3.length;i++)
        {
            t1= p3[i];
            t2 =p2[i];
            t3 = ex[i]*ival;
            eyy = (t1-t2-t3)/Math.sqrt(p3p1i);
            ey[i]=eyy;
        }
        for (int i =0 ; i<ey.length;i++)
        {
            t1=ey[i];
            t2=p3p1[i];
            jval += (t1*t2);
        }

        xval = (Math.pow(distance1,2)-Math.pow(distance2,2)+Math.pow(d,2))/(2*d);
        yval = ((Math.pow(distance1,2)-Math.pow(distance3,2)+Math.pow(ival,2)+Math.pow(jval,2))/(2*jval))-((ival/jval)*xval);

        t1=poz1.latitude;
        t2=ex[0]*xval;
        t3=ey[0]*yval;
        triptx= t1+t2+t3;
        t1 = poz1.longitude;
        t2=ex[1]*xval;
        t3=ey[1]*yval;
        ttripty = t1+t2+t3;
        return new LatLng(triptx,ttripty);

    }

    private double calculateDistance(float txPower, double rssi) {

        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    private double applyCustomKalmanFilterToRssi(double kalmanR, double kalmanQ, double mRSSI){
        KalmanFilter mKalmanFilter = new KalmanFilter(kalmanR, kalmanQ);
        return mKalmanFilter.applyFilter(mRSSI);
    }

    private double applyStandardKalmanFilterToRssi(double mRSSI){
        return applyCustomKalmanFilterToRssi(KALMAN_R, KALMAN_Q, mRSSI);
    }

    public LatLng findLocation(double rssi1Unfilt, float txPow1, double rssi2Unfilt, float txPow2, double rssi3Unfilt, float txPow3) {

        Constants constants = new Constants();
        double rssi1Filt = applyStandardKalmanFilterToRssi(rssi1Unfilt);
        double rssi2Filt = applyStandardKalmanFilterToRssi(rssi2Unfilt);
        double rssi3Filt = applyStandardKalmanFilterToRssi(rssi3Unfilt);

        LatLng pos1 = Constants.DEVICE1_COORDINATES;
        double dist1 = calculateDistance(txPow1, rssi1Filt);
        LatLng pos2 = Constants.DEVICE2_COORDINATES;
        double dist2 = calculateDistance(txPow2, rssi2Filt);
        LatLng pos3 = Constants.DEVICE3_COORDINATES;
        double dist3 = calculateDistance(txPow3, rssi3Filt);

        return getLocationbyTrilateration(pos1, dist1, pos2, dist2, pos3, dist3);


    }


}
