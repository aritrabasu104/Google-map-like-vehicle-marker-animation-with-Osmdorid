package com.ideationts.android.pathadisa.pathadisanative.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.ideationts.android.pathadisa.pathadisanative.R;
import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;

import com.ideationts.android.pathadisa.pathadisanative.helper.icon.AmbulanceIconHelper;
import com.ideationts.android.pathadisa.pathadisanative.helper.icon.BusIconHelper;
import com.ideationts.android.pathadisa.pathadisanative.helper.icon.IconHelper;
import com.ideationts.android.pathadisa.pathadisanative.models.PointMap;
import com.ideationts.android.pathadisa.pathadisanative.models.Vehicle;
import com.ideationts.android.pathadisa.pathadisanative.models.VehicleInfo;
import com.ideationts.android.pathadisa.pathadisanative.tasks.VehicleDataFetcher;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.ReusableBitmapDrawable;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


/**
 * Created by ARITRA on 12-07-2017.
 */

public class MarkerDrawer {
    private Marker myCurrentLocationMarker=null;
    private MapView mMapView;
    private static MarkerDrawer instance=null;
    private List<VehicleInfo> olderVehicleInfo=null;
    private Map<String,Marker> previousVehicleListMarkerMap = new HashMap<>();
    private TextView mRouteCodeTextView;
    private TextView mVehicleNumberTextView;
    private TextView mVehicleCrowdTextView;
    private LinearLayout mVehicleDetailsLayout;
    private RadioGroup mVehicleTypeGroup;
    private IconHelper iconHelper;
    private boolean isDetailviewShown=false;

    private MarkerDrawer(MapView mapView) {
        this.mMapView=mapView;
        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                isDetailviewShown=false;
                alterDetailviewState();
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.e("MapView", "long click");
                return false;
            }

        }));

    }


    private void alterDetailviewState(){
        if(!isDetailviewShown){
            if(mVehicleDetailsLayout!=null) {
                if (mVehicleDetailsLayout.getVisibility() != View.GONE)
                    mVehicleDetailsLayout.setVisibility(View.GONE);
                else
                    Log.e("alterDetailviewState","mBusDetailsLayout is not GONE");
            }else
                Log.e("alterDetailviewState","mBusDetailsLayout is null");
            if(mVehicleTypeGroup!=null) {
                if (mVehicleTypeGroup.getVisibility() != View.VISIBLE)
                    mVehicleTypeGroup.setVisibility(View.VISIBLE);
                else
                    Log.e("alterDetailviewState","mVehicleTypeGroup is not VISIBLE");
            }else
                Log.e("alterDetailviewState","mVehicleTypeGroup is null");
        }else
        {
            if(mVehicleTypeGroup!=null) {
                if (mVehicleTypeGroup.getVisibility() != View.GONE)
                    mVehicleTypeGroup.setVisibility(View.GONE);
                else
                    Log.e("alterDetailviewState","mVehicleTypeGroup is not GONE");
            }else
                Log.e("alterDetailviewState","mVehicleTypeGroup is null");
            if(mVehicleDetailsLayout!=null) {
                if (mVehicleDetailsLayout.getVisibility() != View.VISIBLE)
                    mVehicleDetailsLayout.setVisibility(View.VISIBLE);
                else
                    Log.e("alterDetailviewState","mBusDetailsLayout is not VISIBLE");
            }else
                Log.e("alterDetailviewState","mBusDetailsLayout is null");

        }
    }
    public static synchronized MarkerDrawer getInstance() {
        return instance;

    }
    public static synchronized void createInstance(MapView mapView) {
        instance = new MarkerDrawer(mapView);

     }

    public void drawPerson(GeoPoint geoPoint,Context context) {
        //remove the item with previous location
        Log.d("drawperson","In..");
        if(myCurrentLocationMarker!=null) {
            myCurrentLocationMarker.closeInfoWindow();
            mMapView.getOverlayManager().remove(myCurrentLocationMarker);
        }
        myCurrentLocationMarker = new Marker(mMapView);
        myCurrentLocationMarker.setPosition(geoPoint);

        myCurrentLocationMarker.setIcon(context.getResources().getDrawable(R.drawable.person));
        //MarkerInfoWindow markerInfoWindow =new MarkerInfoWindow(R.layout.bonuspack_bubble,mMapView);
        //myCurrentLocationMarker.setTitle(ApplicationConstants.TITLE_MAP_PERSON);

        //myCurrentLocationMarker.setInfoWindow(markerInfoWindow);
        //myCurrentLocationMarker.showInfoWindow();


        mMapView.getOverlays().add(myCurrentLocationMarker);

        mMapView.invalidate();

    }

    //this part should be executed from background thread
    //except UI drawing parts


    public void drawVehicles(List<VehicleInfo> allVehicleInfo , Activity activity){


       //initialize the layout components
        mVehicleDetailsLayout=activity.findViewById(R.id.vehicle_details_linear_layout);
        mRouteCodeTextView=activity.findViewById(R.id.vehicle_route_tv);
        mVehicleNumberTextView=activity.findViewById(R.id.vehicle_number_tv);
        mVehicleCrowdTextView=activity.findViewById(R.id.vehicle_crowd_tv);
        mVehicleTypeGroup = activity.findViewById(R.id.vehicle_type_rg);


        if(VehicleDataFetcher.getInstance().getVehicleType()==ApplicationConstants.VEHICLE_TYPE_BUS) {
            iconHelper = BusIconHelper.getInstance();
        }else
        if(VehicleDataFetcher.getInstance().getVehicleType()==ApplicationConstants.VEHICLE_TYPE_AMBULANCE)
        {
            iconHelper=AmbulanceIconHelper.getInstance();
        }else
        {
            Log.e("drawVehicles","unknown Vehicle type in VehicleDataFetcher"+
                    VehicleDataFetcher.getInstance().getVehicleType());
            return;
        }

        if(olderVehicleInfo!=null && olderVehicleInfo.size()>0) {
            VehicleDrawingHelper vehicleDrawingHelper = VehicleDrawingHelper.getInstance(olderVehicleInfo, allVehicleInfo);

            //getRequired list and map from bus drawing helper
            List<String> commVehicleNos= vehicleDrawingHelper.getComVehicleNos();
            List<String> oldVehicleNos=vehicleDrawingHelper.getOldVehicleNos();
            List<String> newVehicleNos=vehicleDrawingHelper.getNewVehicleNos();

            Map<String,VehicleInfo> oldVehiclesInfoMap = vehicleDrawingHelper.getOldVehiclesInfoMap();
            Map<String,VehicleInfo> newVehiclesInfoMap = vehicleDrawingHelper.getNewVehiclesInfoMap();

            //remove the markers for vehicles no longer in the map
            if(oldVehicleNos.size()>0) {
                Log.d("newrun","removeObsoleteMarkers"+oldVehicleNos.size());
                removeObsoleteMarkers(activity, oldVehicleNos, previousVehicleListMarkerMap);
            }
            //handle the marker for vehicles that are still in the
            if(commVehicleNos.size()>0) {
                Log.d("newrun","animateCommonBusMarkers"+commVehicleNos.size());
                animateCommonBusMarkers(activity, commVehicleNos, oldVehiclesInfoMap,
                        newVehiclesInfoMap, previousVehicleListMarkerMap);
            }

            //add new markers
            if(newVehicleNos.size()>0) {
                Log.d("newrun","addNewBusMarkers"+newVehicleNos.size());
                addNewVehicleMarkers(activity, newVehicleNos, newVehiclesInfoMap);
            }
            //simpleUpdateMap(allBusInfo,iconNormal,activity);

        }else
            addNewVehicleMarkers(activity,allVehicleInfo);


         olderVehicleInfo=allVehicleInfo;

        //refresh map
        Log.i("Overlay:","Count:"+mMapView.getOverlays().size());
        mMapView.postInvalidate();


    }

    //this version of addNewBusMarkers is used for first call
    private void addNewVehicleMarkers(Activity activity,List<VehicleInfo> allVehicleInfo){
        Log.d("addNewVehicleMarkers",allVehicleInfo.toString());

        List<Marker> allVehicleMarker=new ArrayList<>();
        for(VehicleInfo vehicleInfo:allVehicleInfo)
        {
            Marker vehicleMarker = iconHelper.prepareMarker(mMapView,vehicleInfo,activity);
            vehicleMarker.setOnMarkerClickListener(new MyMarkerClickListener(vehicleInfo,activity));

            allVehicleMarker.add(vehicleMarker);
            previousVehicleListMarkerMap.put(vehicleInfo.getVehicleNo(),vehicleMarker);
        }
        mMapView.getOverlays().addAll(allVehicleMarker);


    }

    //this version of addNewBusMarkers is used for consecutive calls
    private void addNewVehicleMarkers(Activity activity,List<String> newVehicleNos, Map<String, VehicleInfo> newVehiclesInfoMap
                ) {
        Log.d("addNewVehicleMarkers","count:"+newVehicleNos.size());
        List<Marker> allVehicleMarker=new ArrayList<>();
        //add new markers
        for(final String vehicleNo:newVehicleNos){
            final VehicleInfo vehicleInfo = newVehiclesInfoMap.get(vehicleNo);
            Marker vehicleMarker = iconHelper.prepareMarker(mMapView,vehicleInfo,activity);
            vehicleMarker.setOnMarkerClickListener(new MyMarkerClickListener(vehicleInfo,activity));

            //add in collections for animation in next drawPerson call
            allVehicleMarker.add(vehicleMarker);
            previousVehicleListMarkerMap.put(vehicleNo,vehicleMarker);
        }

        mMapView.getOverlays().addAll(allVehicleMarker);

    }



    private class MyMarkerClickListener implements  Marker.OnMarkerClickListener{
        private VehicleInfo busInfo;
        private Activity activity;
        MyMarkerClickListener(VehicleInfo busInfo,Activity activity){
            this.busInfo=busInfo;
            this.activity=activity;
        }
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            Log.d("MyMarkerClickListener","onMarkerClick");
            isDetailviewShown=true;
            alterDetailviewState();
            mRouteCodeTextView.setText(activity.getResources().getString(R.string.route_code_textview_text,busInfo.getRouteCode()));
            mVehicleNumberTextView.setText(activity.getResources().getString(R.string.vehicle_no_textview_text,busInfo.getVehicleNo()));
            mVehicleCrowdTextView.setText(activity.getResources().getString(R.string.crowd_textview_text,busInfo.getCrowd()));
            return true;
        }
    }




    private void removeObsoleteMarkers(Activity activity,List<String> oldVehicleNos, Map<String, Marker> previousBusListMarkerMap) {

        for (String vehicleNo : oldVehicleNos){
            Marker obsoleteMarker = previousBusListMarkerMap.get(vehicleNo);
            activity.runOnUiThread(new RemoveMarkerOnUi(obsoleteMarker));
            previousBusListMarkerMap.remove(obsoleteMarker);
        }
    }

    private class RemoveMarkerOnUi implements Runnable{
            Marker obsoleteMarker;
            RemoveMarkerOnUi(Marker obsoleteMarker){
                this.obsoleteMarker = obsoleteMarker;
            }
            @Override
            public void run() {
                obsoleteMarker.closeInfoWindow();
                Boolean isRemoved=mMapView.getOverlays().remove(obsoleteMarker);
                Log.d("RemoveMarkerOnUi",""+isRemoved);

            }

    }

    //iterate through the common vehicle numbers to animate the corresponding markers
    private void animateCommonBusMarkers(Activity activity,List<String> commVehicleNos,
                                         Map<String,VehicleInfo> oldVehiclesInfoMap,Map<String,VehicleInfo> newVehiclesInfoMap,
                                         Map<String,Marker> previousBusListMarkerMap
                                        ){

        //these two vars are used to control which thread should refresh the map
        boolean shouldRefreshMap=false;
        int vehicleCount=0;
        Log.d("animateCommonBusMarkers","count:"+commVehicleNos.size());
        for(String vehicleNo:commVehicleNos){
            vehicleCount++;

            final long start = SystemClock.uptimeMillis();

            //get the marker used in earlier render
            final Marker marker = previousBusListMarkerMap.get(vehicleNo);
            if(marker!=null) {
                //get old position
                PointMap lastLocation = oldVehiclesInfoMap.get(vehicleNo).getLastLocation();
                final GeoPoint fromPosition = new GeoPoint(lastLocation.get(PointMap.KEY_LATITUDE),
                        lastLocation.get(PointMap.KEY_LONGITUDE));
                final int fromAngle = oldVehiclesInfoMap.get(vehicleNo).getAngle();


                //get new position
                PointMap currentLocation = newVehiclesInfoMap.get(vehicleNo).getLastLocation();
                final GeoPoint toPosition = new GeoPoint(currentLocation.get(PointMap.KEY_LATITUDE),
                        currentLocation.get(PointMap.KEY_LONGITUDE));
                final int toAngle = newVehiclesInfoMap.get(vehicleNo).getAngle();

                //currently working with this
                VehicleInfo vehicleInfo = newVehiclesInfoMap.get(vehicleNo);
                //moveMarker(activity, marker, fromPosition, toPosition, busInfo, toAngle, iconNormalId, iconRedId);


                //if the marker does not move long enough show the red icon
                if(fromPosition.distanceTo(toPosition)<ApplicationConstants.DISTANCE_LIMIT_IN_METER_BUSY_ROAD) {
                    iconHelper.makeIconSlow(marker,vehicleInfo,activity);

                }else{
                    //else show normal icon
                    iconHelper.makeIconNormal(marker,vehicleInfo,activity);
                }

                final Interpolator interpolator = new LinearInterpolator();
                if(vehicleCount==commVehicleNos.size())
                    shouldRefreshMap=true;
                //run this part on ui thread
                activity.runOnUiThread(new MarkerPositionAnimator(interpolator, fromPosition, toPosition, mMapView,
                        start, ApplicationConstants.TIME_INTERVAL_IN_MILI_VEHICLE_DATA_REFRESH,
                        fromAngle, toAngle, marker,shouldRefreshMap));
            }
        }

    }
/*
    private void moveMarker(Activity activity,Marker marker,GeoPoint fromPosition,GeoPoint toPosition,VehicleInfo vehicleInfo
                            ,int toAngle
                            ){
        //if the marker does not move long enough show the red icon
        if(fromPosition.distanceTo(toPosition)<ApplicationConstants.DISTANCE_LIMIT_IN_METER_BUSY_ROAD) {
            iconHelper.makeIconSlow(marker,vehicleInfo,activity);

        }else{
         //else show normal icon
            iconHelper.makeIconNormal(marker,vehicleInfo,activity);
        }
        marker.setRotation((float) toAngle + ApplicationConstants.ANGLE_OFFSET_FOR_BUS_ICON);
        marker.setPosition(toPosition);

    }
*/
    private class MarkerPositionAnimator extends TimerTask{
        private Interpolator interpolator;
        private IGeoPoint fromPosition,toPosition;;
        private MapView mMapView;
        private long start,duration;
        private Marker marker;
        private int fromAngle;
        private int toAngle;
        private boolean shouldRefreshMap;

        private MarkerPositionAnimator(Interpolator interpolator, IGeoPoint fromPosition,
                                      IGeoPoint toPosition, MapView mapView, long start, long duration,
                                      int fromAngle, int toAngle, Marker marker, boolean shouldRefreshMap)
                                                                {
            this.interpolator=interpolator;
            this.fromPosition=fromPosition;
            this.toPosition=toPosition;
            this.mMapView=mapView;
            this.start=start;
            this.duration=duration;
            this.marker=marker;
            this.fromAngle=fromAngle;
            this.toAngle=toAngle;
            this.shouldRefreshMap=shouldRefreshMap;
        }

        @Override
        public void run() {
            //initialize handler for animation
            Handler handler = new Handler();
            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float) elapsed / duration);
            double lng = t * toPosition.getLongitude() + (1 - t) * fromPosition.getLongitude();
            double lat = t * toPosition.getLatitude() + (1 - t) * fromPosition.getLatitude();
            double angle=t *toAngle +(1-t)*fromAngle;
            if(lat!=fromPosition.getLatitude()||lng!=fromPosition.getLongitude()||
                    Math.abs(angle-fromAngle)>ApplicationConstants.ANGLE_MIN_DIFF_FOR_BUS_ICON_ANIMATE)
            {
                marker.setPosition(new GeoPoint(lat, lng));
                marker.setRotation((float) angle + ApplicationConstants.ANGLE_OFFSET_FOR_BUS_ICON);
            }
            if (t < 1.0) {
                handler.postDelayed(this, ApplicationConstants.TIME_INTERVAL_IN_MILI_ICON_ANIMATE);
            }
            if(shouldRefreshMap) {
                Log.d("shouldRefreshMap","refreshing");
                mMapView.invalidate();
            }
        }
    }

}
