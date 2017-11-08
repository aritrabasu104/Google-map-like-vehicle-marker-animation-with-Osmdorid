package com.ideationts.android.pathadisa.pathadisanative.helper.icon;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.ideationts.android.pathadisa.pathadisanative.R;
import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;
import com.ideationts.android.pathadisa.pathadisanative.models.PointMap;
import com.ideationts.android.pathadisa.pathadisanative.models.VehicleInfo;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by ARITRA on 04-08-2017.
 */

public class BusIconHelper implements IconHelper {
    private int normalIconId=R.mipmap.bus_icon_normal;
    private int slowIconId=R.mipmap.bus_icon_red;
    private static BusIconHelper busIconHelper = new BusIconHelper();

    private BusIconHelper(){}
    public static BusIconHelper getInstance(){
        return busIconHelper;
    }

    @Override
    public Marker prepareMarker(MapView mapView, VehicleInfo vehicleInfo, Activity activity) {

        Marker marker = new Marker(mapView);
        GeoPoint geoPoint;
        geoPoint = new GeoPoint(vehicleInfo.getLastLocation().get(PointMap.KEY_LATITUDE),
                vehicleInfo.getLastLocation().get(PointMap.KEY_LONGITUDE));

        marker.setPosition(geoPoint);

        //give angle for direction :)
        if(vehicleInfo.getAngle()<=360 && vehicleInfo.getAngle()>=0)
            marker.setRotation(vehicleInfo.getAngle()+ ApplicationConstants.ANGLE_OFFSET_FOR_BUS_ICON);

        //marker.setIcon(icon);
        marker.setIcon(getDrawableIcon(activity,normalIconId,vehicleInfo));


        /*
        MarkerInfoWindow markerInfoWindow =new MarkerInfoWindow(R.layout.bonuspack_bubble,mMapView);
        marker.setTitle(busInfo.getVehicleNo());

        marker.setInfoWindow(markerInfoWindow);
        marker.showInfoWindow();
        */
        marker.setSnippet(vehicleInfo.getVehicleNo());
        return marker;
    }

    private BitmapDrawable getDrawableIcon(Activity activity, int drawableId, VehicleInfo vehicleInfo){
        //only show the route not direction
        String routeWithOutDir=getRouteWithoutDir(vehicleInfo);

        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(ApplicationConstants.BUS_ICON_TEXT_SIZE);

        Canvas canvas = new Canvas(bm);
        canvas.rotate(90,0,0);
        canvas.drawText(routeWithOutDir, bm.getHeight()/4, -bm.getWidth()/4, paint);

        return new BitmapDrawable(activity.getResources(),bm);
    }

    private static String getRouteWithoutDir(VehicleInfo vehicleInfo){
        return vehicleInfo.getRouteCode().split(":")[0];
    }



    @Override
    public void makeIconSlow(Marker marker,VehicleInfo vehicleInfo, Activity activity) {
        marker.setIcon(getDrawableIcon(activity,slowIconId,vehicleInfo));

    }

    @Override
    public void makeIconNormal(Marker marker,VehicleInfo vehicleInfo, Activity activity) {
        marker.setIcon(getDrawableIcon(activity,normalIconId,vehicleInfo));
    }
}
