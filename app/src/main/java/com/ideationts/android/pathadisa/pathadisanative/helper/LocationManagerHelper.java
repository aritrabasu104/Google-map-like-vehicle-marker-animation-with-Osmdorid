package com.ideationts.android.pathadisa.pathadisanative.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ideationts.android.pathadisa.pathadisanative.MainActivity;
import com.ideationts.android.pathadisa.pathadisanative.R;
import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;


public class LocationManagerHelper extends BroadcastReceiver implements LocationListener  {
    private Context context;
   // private MainActivity activity;

    private static LocationManagerHelper instance = null;
    private LocationManager locationManager;
    private static boolean isGPSEnabled, isNetworkEnabled;
    private boolean canGetLocation;
    private MapView mapView;
    private ContentResolver contentResolver;
    private Location lastLocation = null;
    private AlertDialog gpsAlert=null;
    private static void resetFlags(){
        isGPSEnabled=false;
        isNetworkEnabled=false;
    }
    private LocationManagerHelper(Context context,MapView mapView,ContentResolver contentResolver) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.mapView =mapView;
        this.contentResolver=contentResolver;
        //listen to location mode change broadcasts :)
        IntentFilter filter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        context.registerReceiver(this, filter);

    }


    public static synchronized LocationManagerHelper createInstance(Context activityContext, MapView mapView,ContentResolver
                                                                    contentResolver) {

        instance = new LocationManagerHelper(activityContext,mapView,contentResolver);
        return instance;

    }
    public static synchronized LocationManagerHelper getInstance(){
        resetFlags();
        return instance;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }



    /*
    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
*/

    //if GPS not enabled, show alert
    private void promptEnableGPS() {
        if(gpsAlert!=null) {
            Log.d("promptEnableGPS"," remove earlier alert");
            gpsAlert.dismiss();
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Change GPS settings?");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((MainActivity)context).finish();
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

            @Override
            public void onCancel(DialogInterface dialogInterface) {

                dialogInterface.dismiss();
                //if(!checkLocationSetting())
                //    promptEnableGPS();
            }
        });

        gpsAlert = alertDialog.create();
        gpsAlert.setCancelable(false);
        gpsAlert.show();
    }


    public void setCenter(){
        IGeoPoint currentGeoPoint;
        if(lastLocation!=null)
            currentGeoPoint = new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude());
        else
            currentGeoPoint = new GeoPoint(ApplicationConstants.LATITUDE_DEF,ApplicationConstants.LONGITUDE_DEF);
        mapView.getController().animateTo(currentGeoPoint);

    }
    private boolean checkLocationSetting(){
        boolean isGPSEnable;
        int provider;
        try {
            provider = Settings.Secure.getInt(contentResolver,
                    Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if(provider==Settings.Secure.LOCATION_MODE_OFF){
            isGPSEnable = false;
        }else{
            isGPSEnable = true;
        }
        Log.d("checkLocationSetting",String.valueOf(provider));
        return  isGPSEnable;

    }
    //check if GPS & NET enabled
    private void checkOrEnableGPS() {
        Log.d("checkOrEnableGPS", "In..");
        boolean isGPSProviderEnabled;
        try {
            if (locationManager != null) {
                isGPSEnabled = checkLocationSetting();
                isGPSProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                Log.d("checkOrEnableGPS", "isGPSEnabled: " + isGPSEnabled + " isNetworkEnabled: " + isNetworkEnabled
                +" isGPSProviderEnabled "+isGPSProviderEnabled );
                if (!isGPSEnabled)
                    promptEnableGPS();
            }
        } catch (Exception ex) {
            Log.e("checkOrEnableGPS", "Exception: " + ex.getMessage());
        }
    }

    public Location getLocation() {

        Location temp = null;
        Log.d("getLocation", "canGetLocation: " + canGetLocation + " isGPSEnabled: " + isGPSEnabled + " isNetworkEnabled: " + isNetworkEnabled);

        try {
            if (canGetLocation) {
                if (isGPSEnabled) {
                    Log.d("getLocation", "In isGPSEnabled");
                    temp = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else if (isNetworkEnabled) {
                    Log.d("getLocation", "In isNetworkEnabled");
                    temp = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } catch (Exception ex) {
            Log.e("getLocation", "Exception: " + ex.getMessage());
        }
        Log.d("getLocation", "location: " + temp);
        lastLocation = temp;
        return temp;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
                lastLocation = location;

            MarkerDrawer.getInstance().drawPerson(
                    new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()),
                    context);
            //loadBusData();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(context, "Provider "+provider+" status changed.Status : "+status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Provider "+provider+" got enabled. You disable if required", Toast.LENGTH_SHORT).show();
        isGPSEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Provider "+provider+" got disabled. You enable if required", Toast.LENGTH_SHORT).show();
        isGPSEnabled = false;
    }

    public void stopUsingGPS() {
        try {
            if (locationManager != null) {
                //locationManager.removeUpdates(RouteAmendActivity.this);
            }
            canGetLocation = false;
        } catch (Exception ex) {
            Log.e("stopUsingGPS", "Exception: " + ex.getMessage());
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    //start the GPS monitoring
    public void startUsingGPS() {
        Log.d("startUsingGPS", "isNetworkEnabled.."+isNetworkEnabled+ "isGPSEnabled .. "+isGPSEnabled);
        if (!(isNetworkEnabled || isGPSEnabled))
            checkOrEnableGPS();

        try {
            if (!canGetLocation && isGPSEnabled) {
                Log.d("startUsingGPS", "In !canGetLocation && isGPSEnabled.");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ApplicationConstants.
                        MIN_DISTANCE_TO_UPDATE_GPS_METER,
                        ApplicationConstants.TIME_INTERVAL_IN_MILI_GPS_UPDATE, this);
                canGetLocation = true;
            }
            if (!canGetLocation && isNetworkEnabled) {
                Log.d("startUsingGPS", "!canGetLocation && isNetworkEnabled.");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        ApplicationConstants.MIN_DISTANCE_TO_UPDATE_GPS_METER,
                        ApplicationConstants.TIME_INTERVAL_IN_MILI_GPS_UPDATE, this);
                canGetLocation = true;
            }

        } catch (Exception ex) {
            Log.e("startUsingGPS", "Exception: " + ex.getMessage());
            promptEnableGPS();
        }
    }
    public void cleanupOnPause() {
      dimissGPSAlert();
    }
    private void dimissGPSAlert(){
        if(gpsAlert!=null && gpsAlert.isShowing())
            gpsAlert.dismiss();
    }

    public void cleanupOnDestroy() {
        context.unregisterReceiver(this);
    }

    //listen to locationmanager MODE_CHANGED_ACTION
    @Override
    public void onReceive(Context context, Intent intent) {
        dimissGPSAlert();
        if(!checkLocationSetting())
            promptEnableGPS();
    }
}
