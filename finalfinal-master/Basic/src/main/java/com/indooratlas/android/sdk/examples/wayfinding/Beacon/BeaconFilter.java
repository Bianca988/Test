package com.indooratlas.android.sdk.examples.wayfinding.Beacon;

import java.util.Collection;
import java.util.List;

public interface BeaconFilter<B extends Beacon> {



    boolean canMatch(Beacon beacon);



    boolean matches(B beacon);



    List<B> getMatches(Collection<B> beacons);



}