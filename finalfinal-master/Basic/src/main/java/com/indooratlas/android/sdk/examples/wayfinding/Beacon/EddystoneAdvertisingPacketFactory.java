package com.indooratlas.android.sdk.examples.wayfinding.Beacon;



public class EddystoneAdvertisingPacketFactory extends AdvertisingPacketFactory {



    public EddystoneAdvertisingPacketFactory() {

        this(EddystoneAdvertisingPacket.class);

    }



    public <AP extends AdvertisingPacket> EddystoneAdvertisingPacketFactory(Class<AP> packetClass) {

        super(packetClass);

    }





    public boolean canCreateAdvertisingPacket(byte[] advertisingData) {

        return EddystoneAdvertisingPacket.meetsSpecification(advertisingData);

    }





    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {

        return new EddystoneAdvertisingPacket(advertisingData);

    }



}