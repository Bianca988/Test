package com.indooratlas.android.sdk.examples.wayfinding.Beacon;




import org.altbeacon.beacon.service.RssiFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager.getBeaconKey;
import static com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager.processAdvertisingPacket;
import static com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager.processClosestBeacon;
import static com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager.removeInactiveBeacons;

public class BeaconManager {
    private static volatile BeaconManager instance;
    private BeaconFactory beaconFactory = new BeaconFactory();
    private AdvertisingPacketFactoryManager advertisingPacketFactoryManager = new AdvertisingPacketFactoryManager();
    private Map<String,Beacon> beaconMap = new LinkedHashMap<>();
    private final Set<BeaconUpdateListener> beaconUpdateListenerset = new HashSet<>();
    private long inactivityDuration = TimeUnit.MINUTES.toMillis(3);
    private Beacon closestBeacon;
    private static final WindowFilter meanFilter = new MeanFilter(15,TimeUnit.SECONDS);

    private BeaconManager()
    {

    }

    public static BeaconManager getInstance()
    {
        if(instance == null)
        {
            synchronized (BeaconManager.class)
            {
                if(instance == null )
                {
                    instance = new BeaconManager();
                }
            }
        }
        return instance;
    }

    public static AdvertisingPacket processAdvertisingData(String macAddres , byte[] advertisingData , int rssi)
    {
        AdvertisingPacket advertisingPacket = getInstance().advertisingPacketFactoryManager.createAdvertisingPacket(advertisingData);
        if(advertisingPacket != null)
        {
            advertisingPacket.setRssi(rssi);
        }
        return processAdvertisingPacket(macAddres,advertisingPacket);
    }


    public static AdvertisingPacket processAdvertisingPacket(String macAddres, AdvertisingPacket advertisingPacket)
    {
        if(advertisingPacket == null)
        {
            return null;
        }
        BeaconManager instance = getInstance();
        String key = getBeaconKey(macAddres,advertisingPacket);
        Beacon beacon;
        if(instance.beaconMap.containsKey(key))
        {
            beacon = instance.beaconMap.get(key);

        }else
        {
            removeInactiveBeacons();
            beacon = instance.beaconFactory.createBeacon(advertisingPacket);
            if(beacon == null)
            {
                return advertisingPacket;
            }
            beacon.setMadAddress(macAddres);
            instance.beaconMap.put(key,beacon);
        }
        beacon.addAdvertisingPacket(advertisingPacket);
        processClosestBeacon(beacon);
        instance.notifyBeaconUpdateListeners(beacon);
        return advertisingPacket;

    }

    public static void processClosestBeacon(Beacon beacon)
    {
        BeaconManager instance = getInstance();

        meanFilter.setMaximumTimestamp(beacon.getLatestAdvertisingPacket().getTimestamp());
        meanFilter.setMinimumTimestamp(beacon.getLatestAdvertisingPacket().getTimestamp()-meanFilter.getTimeUnit().toMillis(meanFilter.getDuration()));

        if(instance.closestBeacon == null )
        {
           instance.closestBeacon = beacon;

            } else {
            if(instance.closestBeacon != beacon)
            {
                if(beacon.getDistance((com.indooratlas.android.sdk.examples.wayfinding.Beacon.RssiFilter) meanFilter) + 1 < instance.closestBeacon.getDistance((com.indooratlas.android.sdk.examples.wayfinding.Beacon.RssiFilter) meanFilter))
                {
                    instance.setClosestBeacon(beacon);
                }
            }
        }
        }
        private void notifyBeaconUpdateListeners(Beacon beacon)
        {
            synchronized (beaconUpdateListenerset)
            {
                for (BeaconUpdateListener beaconUpdateListener : beaconUpdateListenerset)
                {
                    try
                    {
                         beaconUpdateListener.onBeaconUpdated(beacon);

                    }catch (ClassCastException e )
                    {

                    }
                }
            }
        }
        public static boolean registerBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener)
        {
            synchronized (getInstance().beaconUpdateListenerset)
            {
                return getInstance().beaconUpdateListenerset.add(beaconUpdateListener);
            }
        }
    public static boolean unregisterBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener)
    {
        synchronized (getInstance().beaconUpdateListenerset)
        {
            return getInstance().beaconUpdateListenerset.remove(beaconUpdateListener);
        }
    }
    public static String getBeaconKey(String macAddress , AdvertisingPacket advertisingPacket)
    {
        return macAddress + "- " + Arrays.hashCode(advertisingPacket.getData());
    }

    public static Beacon getBeacon(String macAddres , AdvertisingPacket advertisingPacket)
    {
        String key = getBeaconKey(macAddres,advertisingPacket);
        return getInstance().beaconMap.get(key);
    }

    public static void removeInactiveBeacons()
    {
        removeInactiveBeacons(getInstance().inactivityDuration,TimeUnit.MILLISECONDS);
    }

    private static void removeInactiveBeacons(long inactivityDuration, TimeUnit timeUnit) {
        removeInactiveBeacons(System.currentTimeMillis()-timeUnit.toMillis(inactivityDuration));
    }
   private static void removeInactiveBeacons(long minimunAdvertisingTimeStamp)
   {
       BeaconManager instance = getInstance();
       AdvertisingPacket  latestAdvertisingPacket;
       List<String> inactiveBeaconKeys = new ArrayList<>();
       for(Iterator<Map.Entry<String,Beacon>> beaconMapIterator = instance.beaconMap.entrySet().iterator(); beaconMapIterator.hasNext();)
       {
           Map.Entry<String,Beacon> beaconEntry = beaconMapIterator.next();
           latestAdvertisingPacket = beaconEntry.getValue().getLatestAdvertisingPacket();
           if(latestAdvertisingPacket == null || latestAdvertisingPacket.getTimestamp() < minimunAdvertisingTimeStamp)
           {
               inactiveBeaconKeys.add(beaconEntry.getKey());
           }
       }
       instance.beaconMap.keySet().removeAll(inactiveBeaconKeys);
   }

   public Beacon getClosestBeacon()
   {
       return closestBeacon;
   }
   public void setClosestBeacon(Beacon closestBeacon)
   {
       this.closestBeacon = closestBeacon;
   }
   public BeaconFactory getBeaconFactory()
   {
       return beaconFactory;
   }
   public void setBeaconFactory(BeaconFactory beaconFactory)
   {
       this.beaconFactory = beaconFactory;
   }
   public AdvertisingPacketFactoryManager getAdvertisingPacketFactoryManager()
   {
       return advertisingPacketFactoryManager;
   }
   public void setAdvertisingPacketFactoryManager(AdvertisingPacketFactoryManager advertisingPacketFactoryManager)
   {
       this.advertisingPacketFactoryManager = advertisingPacketFactoryManager;
   }
   public Map<String,Beacon> getBeaconMap()
   {
       return beaconMap;
   }
   public long getInactivityDuration()
   {
       return inactivityDuration;
   }
   public void setInactivityDuration(long inactivityDuration)
   {
       this.inactivityDuration = inactivityDuration;
   }
}



