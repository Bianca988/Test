package com.indooratlas.android.sdk.examples.wayfinding.Beacon;









import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Beacon <P extends com.indooratlas.android.sdk.examples.wayfinding.Beacon.AdvertisingPacket>  {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(60);

    protected String madAddress;
    protected int rssi, calibratedRssi, calibratedDistance, transmissionPower;
    protected float distance;
    protected boolean shoulUpdateDistance = true;
    protected final ArrayList<P> advertisingPackets = new ArrayList<>();
    protected BeaconLocationProvider locationProvider;

    public Beacon() {
        this.locationProvider = createLocationProvider();
    }

    public static Beacon from(AdvertisingPacket advertisingPacket) {
      Beacon beacon = null;

        if (advertisingPacket instanceof IBeaconAdvertisingPacket) {
            beacon = new IBeacon();
        } else if (advertisingPacket instanceof EddystoneAdvertisingPacket) {
            beacon = new Eddystone();
        }
        return beacon;
    }

    public boolean hasLocation() {
        return locationProvider != null && locationProvider.hasLocation();
    }

    public Location getLocation() {
        return  locationProvider == null ? null : locationProvider.getLocation();
    }

    public static List<Location> getLocation(List<? extends Beacon> beacons) {
        List<Location> locations = new ArrayList<>();
        for (Beacon beacon : beacons) {
            if (beacon.hasLocation()) {
                locations.add(beacon.getLocation());
            }
        }
        return locations;
    }

    public abstract BeaconLocationProvider createLocationProvider();

    public boolean hasAnyAdvertisingPacket() {
        return !advertisingPackets.isEmpty();
    }

    public P getOldestAdvertisingPacket() {
        synchronized (advertisingPackets) {
            if (!hasAnyAdvertisingPacket()) {
                return null;
            }
            return advertisingPackets.get(0);
        }
    }

    public P getLatestAdvertisingPacket() {
        synchronized (advertisingPackets) {
            if (!hasAnyAdvertisingPacket()) {
                return null;
            }
            return advertisingPackets.get(advertisingPackets.size() - 1);
        }
    }

    public ArrayList<P> getAdvertisingPacketsBetween(long startTimestamp, long endTimestamp) {
        return AdvertisingPacketUtil.getAdvertisingPacketsBetween(new ArrayList<P>(advertisingPackets), startTimestamp, endTimestamp);
    }

    public ArrayList<P> getAdvertisingPacketsFromLast(long amount, TimeUnit timeUnit) {
        return getAdvertisingPacketsBetween(System.currentTimeMillis() - timeUnit.toMillis(amount), System.currentTimeMillis());
    }

    public ArrayList<P> getAdvertisingPacketsBetween(long timestamp) {
        return getAdvertisingPacketsBetween(0, timestamp);
    }

    public void addAdvertisingPacket(P advertisingPacket) {

        synchronized (advertisingPackets) {

            rssi = advertisingPacket.getRssi();



            P latestAdvertisingPacket = getLatestAdvertisingPacket();

            if (latestAdvertisingPacket == null || !advertisingPacket.dataEquals(latestAdvertisingPacket)) {

                applyPropertiesFromAdvertisingPacket(advertisingPacket);

            }



            if (latestAdvertisingPacket != null && latestAdvertisingPacket.getTimestamp() > advertisingPacket.getTimestamp()) {

                return;

            }



            advertisingPackets.add(advertisingPacket);

            trimAdvertisingPackets();

            invalidateDistance();

        }

    }
    public void applyPropertiesFromAdvertisingPacket(AdvertisingPacket advertisingPacket) {

    }

    public void trimAdvertisingPackets()
    {
        synchronized (advertisingPackets)
        {
            if(!hasAnyAdvertisingPacket())
            {
                return;
            }
            List<P> removableAdvertisingPackets = new ArrayList<>();
            AdvertisingPacket latestAdvertisingPacket = getLatestAdvertisingPacket();
            long minimumPacketTimestamp = System.currentTimeMillis() - MAXIMUM_PACKET_AGE;
            for(P advertisingPacket :advertisingPackets)
            {
                if(advertisingPacket == latestAdvertisingPacket)
                {
                    continue;
                }
                if (advertisingPacket.getTimestamp() < minimumPacketTimestamp)
                {
                    removableAdvertisingPackets.add(advertisingPacket);
                }
            }

            advertisingPackets.removeAll(removableAdvertisingPackets);
        }
    }

    public boolean equalsLastAdvertisingPackage(AdvertisingPacket advertisingPacket)
    {
        return hasAnyAdvertisingPacket() && getLatestAdvertisingPacket().equals(advertisingPacket);
    }
    public boolean hasBeenSeenSince(long timestamp)
    {
        if(!hasAnyAdvertisingPacket())
        {
            return false;
        }
        return getLatestAdvertisingPacket().getTimestamp()>timestamp;
    }
    public boolean hasBeenSeenInThePast(long duration,TimeUnit timeUnit)
    {
        if(!hasAnyAdvertisingPacket())
        {
            return false;
        }
        return getLatestAdvertisingPacket().getTimestamp() > System.currentTimeMillis() - timeUnit.toMillis(duration);

    }
    public float getRssi(RssiFilter filter)
    {
        return filter.filter(this);
    }
    public float getFilteredRssi()
    {
        return getRssi((RssiFilter) createSuggestedWindowFilter());
    }
    public void invalidateDistance()
    {
        shoulUpdateDistance = true;
    }
    public float getDistance()
    {
        if(shoulUpdateDistance)
        {
            distance = getDistance((RssiFilter) createSuggestedWindowFilter());
            shoulUpdateDistance = false;
        }
        return distance;
    }

    public  float getDistance(RssiFilter filter)
    {
        float filteredRssi = getRssi(filter);
        return BeaconDistanceCalculation.calculateDistanceTo(this,filteredRssi);
    }

    public float getEstimatedAdvertisingRange()
    {
        return BeaconUtil.getAdvertisingRange(transmissionPower);
    }
    public long getLatestTimestamp()
    {
        if(!hasAnyAdvertisingPacket())
        {
            return 0;
        }
        return getLatestAdvertisingPacket().getTimestamp();
    }
    public WindowFilter createSuggestedWindowFilter()
    {
        return new KalmanFilter(getLatestTimestamp());
    }


    public static Comparator<Beacon> RssiComparator = new Comparator<Beacon>() {
        @Override
        public int compare(Beacon firstBeacon, Beacon secondBeacon) {
            if(firstBeacon.equals(secondBeacon)) {
                return 0;
            }
            return Integer.compare(firstBeacon.rssi,secondBeacon.rssi);
        }
    };
    public String toString()
    {
        return "Beacon(" + " , macAddress='"+ madAddress + " / " + " rssi = " + rssi +
                " calRssi = " + calibratedRssi + " calDistance" + calibratedDistance +
                "Tx = " + transmissionPower + " ) ";
    }

    public String getMadAddress()
    {
        return madAddress;
    }
    public void setMadAddress(String madAddress)
    {
        this.madAddress = madAddress;
    }
    public int getRssi()
    {
        return rssi;
    }
    public void setRssi(int rssi)
    {
        this.rssi= rssi;
        invalidateDistance();
    }
    public int getCalibratedRssi()
    {
        return calibratedRssi;
    }
    public void setCalibratedRssi(int calibratedRssi)
    {
        this.calibratedRssi = calibratedRssi;
        invalidateDistance();
    }
    public int getCalibratedDistance()
    {
        return calibratedDistance;
    }
    public void setCalibratedDistance(int calibratedDistance)
    {
        this.calibratedDistance = calibratedDistance;
        invalidateDistance();
    }
    public void setTransmissionPower(int transmissionPower)
    {
        this.transmissionPower = transmissionPower;
    }
    public ArrayList<P> getAdvertisingPackets()
    {
        return advertisingPackets;
    }
    public LocationProvider getLocationProvider()
    {
        return locationProvider;
    }
    public void setLocationProvider(BeaconLocationProvider  locationProvider)
    {
        this.locationProvider = locationProvider;
    }



}
