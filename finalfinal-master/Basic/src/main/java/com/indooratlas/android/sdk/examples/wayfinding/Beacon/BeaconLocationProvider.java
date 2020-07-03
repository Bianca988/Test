package com.indooratlas.android.sdk.examples.wayfinding.Beacon;




public abstract class BeaconLocationProvider<B extends Beacon> implements LocationProvider {

   protected B beacon;
   protected Location location ;

   public BeaconLocationProvider(B beacon)
   {
       this.beacon=beacon;
   }
   protected abstract void updateLocation();

   protected boolean shouldUpdateLocation()
   {
       return location == null ;
   }

   protected abstract boolean canUpdateLocation();

   public Location getLocation()
   {
       if(shouldUpdateLocation() && canUpdateLocation())
       {
           updateLocation();
       }
       return location;
   }

   public boolean hasLocation()
   {
       Location location = getLocation();
       return location != null && location.hasLatitudeAndLongitude();
   }
}
