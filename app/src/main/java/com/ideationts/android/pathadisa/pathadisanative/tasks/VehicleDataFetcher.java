package com.ideationts.android.pathadisa.pathadisanative.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;
import com.ideationts.android.pathadisa.pathadisanative.helper.MarkerDrawer;
import com.ideationts.android.pathadisa.pathadisanative.models.AllVehicleResponse;
import com.ideationts.android.pathadisa.pathadisanative.models.DeviceInfoRequest;
import com.ideationts.android.pathadisa.pathadisanative.models.PointMap;
import com.ideationts.android.pathadisa.pathadisanative.utils.NetworkUtils;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by ARITRA on 14-07-2017.
 */

public class VehicleDataFetcher implements LoaderManager.LoaderCallbacks<String>{
    private final int LOADER_ID_ALL_VEHICLE_DATA=673;
    private final String BUNDLE_KEY_ALL_VEHICLE_URL="allVehicleKey";
    private final String BUNDLE_KEY_DEVICE_INFO_JSON = "deviceInfo" ;

    private static VehicleDataFetcher instance=null;
    private Handler mHandler=new Handler();
    private MapView mMapView;
    private String mDeviceId;
    private LoaderManager supportLoaderManager;
    private Activity callingActivity;

    private volatile String vehicleType;
    private VehicleDataFetcher(MapView mapView, String deviceId, LoaderManager loaderManager,Activity callingActivity) {
        this.mMapView=mapView;
        this.mDeviceId=deviceId;
        this.supportLoaderManager=loaderManager;
        this.callingActivity=callingActivity;
        //initialize loader
        supportLoaderManager.initLoader(LOADER_ID_ALL_VEHICLE_DATA,null,this);
    }

    public static synchronized VehicleDataFetcher getInstance() {
        return instance;
    }
    public static synchronized void createInstance(MapView mapView, String deviceId, LoaderManager loaderManager, Activity
                                                   callingActivity) {
        instance = new VehicleDataFetcher(mapView,deviceId,loaderManager,callingActivity);
      }

    public void startFetchingData(String vehicleType){
        this.vehicleType = vehicleType;
        // to reinitialize drawer
        MarkerDrawer.createInstance(mMapView);
        dataLoader.run();
    }
    public void stopFetchingData(){
        mHandler.removeCallbacks(dataLoader);
    }
   private Runnable dataLoader = new Runnable() {
        @Override
        public void run() {
            try {
                loadBusData();
            } finally {
                // 100% guarantee that this always happens, even if
                // your loadBusData method throws an exception
                mHandler.postDelayed(dataLoader, ApplicationConstants.TIME_INTERVAL_IN_MILI_VEHICLE_DATA_REFRESH);
            }
        }
    };

    private void loadBusData() {
        Log.i("loadBusData","In");

        DeviceInfoRequest deviceInfoRequest = new DeviceInfoRequest();
        BoundingBox bounds = mMapView.getBoundingBox();

        //prepare the points
        PointMap<String, Double> pointNw = new PointMap<>();
        PointMap<String, Double> pointSe = new PointMap<>();

        pointNw.put(PointMap.KEY_LATITUDE, bounds.getLatNorth());
        pointNw.put(PointMap.KEY_LONGITUDE, bounds.getLonWest());

        pointSe.put(PointMap.KEY_LATITUDE, bounds.getLatSouth());
        pointSe.put(PointMap.KEY_LONGITUDE, bounds.getLonEast());
        Log.d("corner nw :", pointNw.toString());
        Log.d("corner se :", pointSe.toString());

        deviceInfoRequest.setRequestId(mDeviceId);
        deviceInfoRequest.setPointNW(pointNw);
        deviceInfoRequest.setPointSE(pointSe);
        deviceInfoRequest.setVehicleType(ApplicationConstants.VEHICLE_TYPE_BUS);

        Bundle bundle = new Bundle();
        switch (vehicleType) {
            case ApplicationConstants.VEHICLE_TYPE_BUS:
                bundle.putString(BUNDLE_KEY_ALL_VEHICLE_URL, ApplicationConstants.URL_GET_ALL_BUS_DATA);
                break;
            case ApplicationConstants.VEHICLE_TYPE_AMBULANCE:
                bundle.putString(BUNDLE_KEY_ALL_VEHICLE_URL, ApplicationConstants.URL_GET_ALL_AMBULANCE_DATA);
                break;
        }
        bundle.putString(BUNDLE_KEY_DEVICE_INFO_JSON, new Gson().toJson(deviceInfoRequest));
        //restart loader
        Log.d("onResume", bundle.toString());
        supportLoaderManager.restartLoader(LOADER_ID_ALL_VEHICLE_DATA, bundle, this);
        Log.i("loadBusData","Out");
    }
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>((Context) callingActivity) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args==null)
                    return;
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                Log.d("loadInBackground", String.valueOf(args));
                if(!isOnline()) {
                    promptDataService();
                    return null;
                }
                String urlString = args.getString(BUNDLE_KEY_ALL_VEHICLE_URL);
                String deviceInfoRequestJson = args.getString(BUNDLE_KEY_DEVICE_INFO_JSON);
                try {
                    String body = NetworkUtils.getAllVehicleData(urlString, deviceInfoRequestJson
                    ,ApplicationConstants.SERVER_TIMEOUT_IN_MILI);
                    AllVehicleResponse allVehicleResponse = new Gson().fromJson(body, AllVehicleResponse.class);
                    MarkerDrawer.getInstance().drawVehicles(allVehicleResponse.getData(),callingActivity);
                    return body;
                }catch (IOException e){
                    Log.d("loadInBackground",e.getMessage());
                }
                return null;

            }

        };
    }
    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            Log.d("isOnline",String.valueOf(true));
            return true;
        } catch (IOException e) {
            Log.d("isOnline",String.valueOf(false));
            return false; }
    }
    private void promptDataService(){
        //TODO
    }
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(data!=null) {
            Log.i("onLoadFinished", data);
        }else
            Log.d("onLoadFinished","Null data received");
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public String getVehicleType() {
        return vehicleType;
    }

}

