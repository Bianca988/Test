package com.indooratlas.android.sdk.examples.wayfinding.Beacon;


    public class IBeaconAdvertisingPacketFactory extends AdvertisingPacketFactory {



        public IBeaconAdvertisingPacketFactory() {

            this(IBeaconAdvertisingPacket.class);

        }



        <AP extends AdvertisingPacket> IBeaconAdvertisingPacketFactory(Class<AP> packetClass) {

            super(packetClass);

        }



        @Override

        public boolean canCreateAdvertisingPacket(byte[] advertisingData) {

            return IBeaconAdvertisingPacket.meetsSpecification(advertisingData);

        }



        @Override

        public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {

            return new IBeaconAdvertisingPacket(advertisingData);

        }



    }

