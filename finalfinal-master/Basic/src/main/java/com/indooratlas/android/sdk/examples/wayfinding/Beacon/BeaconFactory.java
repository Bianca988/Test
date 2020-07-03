package com.indooratlas.android.sdk.examples.wayfinding.Beacon;



import java.util.HashMap;
import java.util.Map;

public class BeaconFactory {

    private Map<Class<? extends AdvertisingPacket>,Class<? extends Beacon>> beaconClasses = new HashMap<>();

    public BeaconFactory() {
        beaconClasses.put(IBeaconAdvertisingPacket.class, IBeacon.class);
        beaconClasses.put(EddystoneAdvertisingPacket.class, Eddystone.class);
    }

    public IBeacon createBeacon(AdvertisingPacket advertisingPacket) {
        Class<IBeacon> beaconClass = getBeaconClass(advertisingPacket);
        if (beaconClass == null) {
            return null;
        }
        try {
            return beaconClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<IBeacon> getBeaconClass(AdvertisingPacket advertisingPacket) {
        return (Class<IBeacon>) beaconClasses.get(advertisingPacket.getClass());
    }

    public void addBeaconClass(Class<IBeaconAdvertisingPacket> advertisingPacketClass, Class<IBeacon> beaconClass)
    {
        beaconClasses.put(advertisingPacketClass,beaconClass);
    }
    public Map<Class<? extends AdvertisingPacket>, Class<? extends Beacon>> getBeaconClasses()
    {
        return beaconClasses;
    }
    public void setBeaconClasses(Map<Class<? extends  AdvertisingPacket>, Class<? extends Beacon>> beaconClasses)
    {
        this.beaconClasses = beaconClasses;
    }
}
