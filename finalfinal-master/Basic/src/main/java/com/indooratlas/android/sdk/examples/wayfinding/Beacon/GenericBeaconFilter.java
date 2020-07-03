package com.indooratlas.android.sdk.examples.wayfinding.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenericBeaconFilter<B extends Beacon> implements BeaconFilter<B> {



    protected String macAddress;



    protected boolean matchMacAddress;



    @Override

    public boolean canMatch(Beacon beacon) {

        return true;

    }



    @Override

    public boolean matches(B beacon) {

        if (matchMacAddress) {

            if (!macAddress.equals(beacon.getMadAddress())) {

                return false;

            }

        }

        return true;

    }



    public List<B> getMatches(Collection<B> beacons) {

        List<B> matches = new ArrayList<>();

        for (B beacon : beacons) {

            if (matches(beacon)) {

                matches.add(beacon);

            }

        }

        return matches;

    }



    /*

        Getter & Setter

     */



    public String getMacAddress() {

        return macAddress;

    }



    public void setMacAddress(String macAddress) {

        this.macAddress = macAddress;

    }



    public boolean isMatchMacAddress() {

        return matchMacAddress;

    }



    public void setMatchMacAddress(boolean matchMacAddress) {

        this.matchMacAddress = matchMacAddress;

    }



}