package com.indooratlas.android.sdk.examples.wayfinding.Beacon;

import com.indooratlas.android.sdk.IALocation;

public abstract class LocationDistanceCalculator {

    public static final int EARTH_RADIUS = 6371;

    public static double calculateDistanceBetween(Location fromLocation , Location toLocation)
    {
        return calculateDistanceBetween( fromLocation.getLatitude(),fromLocation.getLongitude(),fromLocation.getAltitude(),
                toLocation.getLatitude(), toLocation.getLongitude(), toLocation.getAltitude());

    }

    public static double calculateDistanceBetween(double fromLatitude , double fromLongitude , double fromAltitude , double toLatitude , double toLongitude , double toAltitude)
    {
        double latDistance = Math.toRadians(toLatitude-fromLatitude);
        double lonDistance = Math.toRadians(toLongitude-fromLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(fromLatitude)) * Math.cos(Math.toRadians(toLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters
        double height = fromAltitude - toAltitude;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);

        }

}
