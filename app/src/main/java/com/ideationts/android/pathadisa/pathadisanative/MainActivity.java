package com.ideationts.android.pathadisa.pathadisanative;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import  android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.ideationts.android.pathadisa.pathadisanative.helper.LocationManagerHelper;
import com.ideationts.android.pathadisa.pathadisanative.helper.MarkerDrawer;
import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;
import com.ideationts.android.pathadisa.pathadisanative.helper.VehicleDrawingHelper;
import com.ideationts.android.pathadisa.pathadisanative.models.DeviceInfoRequest;

import com.ideationts.android.pathadisa.pathadisanative.tasks.VehicleDataFetcher;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureDetector;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity
        implements RotationGestureDetector.RotationListener,
         NavigationView.OnNavigationItemSelectedListener {

    private MapView mMapView;
    private IMapController mMapController;
    private boolean isLocationAccessible=Boolean.FALSE;
    private boolean isStorageAccessible=Boolean.FALSE;
    private boolean isDeviceStateAccessible;
    private IConfigurationProvider mapConfigurationProvider;
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private String mDeviceId;
    private RadioGroup mVehicleTypeGroup;
    //private RadioButton mBusSelectionButton;
    //private RadioButton mAmbulanceSelectionButton;
    //permission related
    public static final int PERMISSION_CONSTANT_GET_GPS_FINE=189;
    public static final int PERMISSION_CONSTANT_WRITE_EXTERNAL_STORAGE = 247;
    public static final int PERMISSION_CONSTANT_READ_PHONE_STATE =562;



    public static final OnlineTileSourceBase GoogleRoads = new XYTileSource("Google-Roads",
            0, 19, 256, ".png", new String[] {
            "http://mt0.google.com",
            "http://mt1.google.com",
            "http://mt2.google.com",
            "http://mt3.google.com",

    }) {
        @Override
        public String getTileURLString(MapTile aTile) {
            return getBaseUrl() + "/vt/lyrs=m&x=" + aTile.getX() + "&y=" +aTile.getY() + "&z=" + aTile.getZoomLevel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context appContext =getApplicationContext();
        Configuration.getInstance().load(appContext, PreferenceManager.getDefaultSharedPreferences(appContext));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_main);


        mVehicleTypeGroup = (RadioGroup) findViewById(R.id.vehicle_type_rg);
        //mBusSelectionButton = (RadioButton) findViewById(R.id.vehicle_type_bus_button);
        //mAmbulanceSelectionButton = (RadioButton) findViewById(R.id.vehicle_type_ambulance_button);
        mVehicleTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.vehicle_type_ambulance_button:
                        VehicleDataFetcher.getInstance().stopFetchingData();
                        VehicleDataFetcher.getInstance().startFetchingData(getVehicleType());
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadBusData();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //initialize Device ID
        setDeviceId();
        //setting up the map
        initializeMap();
        //intitialize the helper class instances
        initiaLizeHelpers();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void initializeMap(){

        //change config and increase threads to download tiles daster
        mapConfigurationProvider
                =Configuration.getInstance();
        mapConfigurationProvider.setTileDownloadThreads(ApplicationConstants.MAP_TILE_DOWNLOAD_THREADS);
        mapConfigurationProvider.setCacheMapTileCount(ApplicationConstants.MAP_TILE_CACHE_COUNT);
        Configuration.setConfigurationProvider(mapConfigurationProvider);
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        //mMapView.setTileSource(GoogleRoads);
        //mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController=mMapView.getController();

        mMapController.setZoom(ApplicationConstants.ZOOM_LEVEL_DEFAULT);
        mMapView.setMaxZoomLevel(ApplicationConstants.ZOOM_LEVEL_MAX);
        mMapView.setMinZoomLevel(ApplicationConstants.ZOOM_LEVEL_MIN);

        manageMap();


    }
    private void initiaLizeHelpers(){

        //intitialize the helper class instances
        LocationManagerHelper.createInstance(this,mMapView,getContentResolver());
        MarkerDrawer.createInstance(mMapView);
        VehicleDataFetcher.createInstance(mMapView,mDeviceId,getSupportLoaderManager(),this);
        VehicleDrawingHelper.createInstance();

    }
    public void getOverlayPermissions() {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
            if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 101);
            }
    }

    @Override
    protected void onResume() {
        Log.i("onResume","**##");
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //this is enabled and disable on onresume and onPause

        //getOverlayPermissions();
        getRuntimePermissions();
        getLocationManagerHelper().setCenter();
        MarkerDrawer.getInstance().drawPerson(
                new GeoPoint(ApplicationConstants.LATITUDE_DEF,ApplicationConstants.LONGITUDE_DEF),
                this);

        VehicleDataFetcher.getInstance().startFetchingData(getVehicleType());

    }
    private String getVehicleType(){
        String vehicleType;
        if(mVehicleTypeGroup.getCheckedRadioButtonId()==R.id.vehicle_type_bus_button)
            vehicleType = ApplicationConstants.VEHICLE_TYPE_BUS;
        else
            vehicleType =  ApplicationConstants.VEHICLE_TYPE_AMBULANCE;
        return vehicleType;
    }

    @Override
    protected void onPause() {
        VehicleDataFetcher.getInstance().stopFetchingData();
        getLocationManagerHelper().cleanupOnPause();

        super.onPause();
        Log.i("onPause","**##");
    }
    @Override
    protected void onStop() {
        Log.i("onStop","**##");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        mCompassOverlay.disableCompass();
        getLocationManagerHelper().cleanupOnDestroy();

        Log.i("onDestroy","**##");
        super.onDestroy();

    }

    private void manageMap(){
        //add rotation
        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mMapView.getOverlays().add(mRotationGestureOverlay);
        mRotationGestureOverlay.setEnabled(true);

        //add compass
        mCompassOverlay = new CompassOverlay(getApplicationContext(),
                new InternalCompassOrientationProvider(getApplicationContext()), mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(mCompassOverlay);

    }

    private void setLocationManager(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startGPSAndUse(locationManager);

    }
    private LocationManagerHelper getLocationManagerHelper() {
        return LocationManagerHelper.getInstance();
    }
    private void startGPSAndUse(LocationManager locationManager) {
        getLocationManagerHelper().setLocationManager(locationManager);
        getLocationManagerHelper().startUsingGPS();
    }




    private void getRuntimePermissions(){
        getGPSPermission();
        getExternalStoragePermission();
        getDeviceStatePermission();
    }
    private void getDeviceStatePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=
                PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSION_CONSTANT_READ_PHONE_STATE);



    }
    private void getExternalStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED)

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_CONSTANT_WRITE_EXTERNAL_STORAGE);



    }

    private void getGPSPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("getGPSPermission","NOT IMPLEMENTED!");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_CONSTANT_GET_GPS_FINE);

                // PERMISSION_CONSTANT_GET_GPS_FINE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }else {
            setLocationManager();
        }
    }

    public boolean isOnline() {
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

    private void setDeviceId(){
        DeviceInfoRequest deviceInfoRequest = new DeviceInfoRequest();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (isDeviceStateAccessible && telephonyManager.getDeviceId() != null)
            mDeviceId =telephonyManager.getDeviceId();
        else
            mDeviceId = ApplicationConstants.REQ_ID_GET_ALL_BUS_DATA;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CONSTANT_GET_GPS_FINE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isLocationAccessible=true;
                    setLocationManager();
                    // permission was granted, yay! Do the
                    // gps-related task you need to do.

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.d("getGPSPermission", "NOT IMPLEMENTED!");

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission. {
                        //Show permission explanation dialog...
                    } else {
                        Toast.makeText(this, ApplicationConstants.TOAST_CHANGE_APP_PERMISSION_FOR_GPS, Toast.LENGTH_LONG).show();
                        //Never ask again selected, or device policy prohibits the app from having that permission.
                        //So, disable that feature, or fall back to another situation...
                        finish();
                    }
                }
                break;

            case PERMISSION_CONSTANT_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isStorageAccessible=true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.d("getGPSPermission", "NOT IMPLEMENTED!");

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission. {
                        //Show permission explanation dialog...
                    } else {
                        Toast.makeText(this, ApplicationConstants.TOAST_CHANGE_APP_PERMISSION_FOR_STORAGE, Toast.LENGTH_LONG).show();
                        //Never ask again selected, or device policy prohibits the app from having that permission.
                        //So, disable that feature, or fall back to another situation...
                        finish();
                    }
                }
            case PERMISSION_CONSTANT_READ_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isDeviceStateAccessible=true;
                }else{
                    //Toast.makeText(this,ApplicationConstants.TOAST_DEVICE_PERMISSION_NOT_PROVIDED,Toast.LENGTH_LONG).show();
                    isDeviceStateAccessible=false;
                }
                break;
                    // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.gotoMyLoc_button)
        {
            getLocationManagerHelper().setCenter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRotate(float deltaAngle) {
        VehicleDataFetcher.getInstance().stopFetchingData();
        VehicleDataFetcher.getInstance().startFetchingData(getVehicleType());
        Log.i("onRotate",String.valueOf(deltaAngle));
    }

}
