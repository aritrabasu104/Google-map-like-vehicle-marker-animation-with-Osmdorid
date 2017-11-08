package com.ideationts.android.pathadisa.pathadisanative.helper.icon;


import android.app.Activity;

import com.ideationts.android.pathadisa.pathadisanative.models.VehicleInfo;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by ARITRA on 04-08-2017.
 */

public interface IconHelper {

    public Marker prepareMarker(MapView mapView, VehicleInfo vehicleInfo, Activity activity);
    public void makeIconSlow(Marker marker,VehicleInfo vehicleInfo, Activity activity);
    public void makeIconNormal(Marker marker,VehicleInfo vehicleInfo, Activity activity);

}
