package com.ideationts.android.pathadisa.pathadisanative.helper.icon;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;
import com.ideationts.android.pathadisa.pathadisanative.models.PointMap;
import com.ideationts.android.pathadisa.pathadisanative.models.VehicleInfo;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by ARITRA on 04-08-2017.
 */

public class AmbulanceIconHelper implements IconHelper {
    private AmbulanceIconHelper(){}
    private static AmbulanceIconHelper ambulanceIconHelper = new AmbulanceIconHelper();
    public static AmbulanceIconHelper getInstance(){
        return ambulanceIconHelper;
    }


    @Override
    public Marker prepareMarker(MapView mapView, VehicleInfo vehicleInfo, Activity activity) {
        return null;
    }

    @Override
    public void makeIconSlow(Marker marker, VehicleInfo vehicleInfo, Activity activity) {

    }

    @Override
    public void makeIconNormal(Marker marker, VehicleInfo vehicleInfo, Activity activity) {

    }

}
