package com.ideationts.android.pathadisa.pathadisanative.data;

/**
 * Created by ARITRA on 04-07-2017.
 */

public class ApplicationConstants {

    //Toasts for user
    public static final String TOAST_GPS_PERMISSION_NOT_PROVIDED="GPS permission not available";
    public static final String TOAST_STORAGE_PERMISSION_NOT_PROVIDED = "Storage permission not available";
    public static final String TOAST_DEVICE_PERMISSION_NOT_PROVIDED = "Device permission not available";
/*
    public static final String TOAST_GPS_PROVIDER_STATUS_TEMP_UNAVAILABLE="GPS Provider Tempory Unaavailable";
    public static final String TOAST_GPS_PROVIDER_STATUS_OUT_OF_SERVICE="GPS Provider Out Of Service";
    public static final String TOAST_GPS_PROVIDER_STATUS_AVAILABLE="GPS Provider Available";
*/
    //gps related
    public static final long TIME_INTERVAL_IN_MILI_GPS_UPDATE=5000;
    public static final long MIN_DISTANCE_TO_UPDATE_GPS_METER=5;
    public static final int ZOOM_LEVEL_DEFAULT=16;

    //def lat long
    public static final float LATITUDE_DEF = 22.5679937f;
    public static final float LONGITUDE_DEF = 88.4304456f;
    public static final String REQUEST_ALL_VEHICLE_MAPKEY_LATITUDE = "latitude";
    public static final String REQUEST_ALL_VEHICLE_MAPKEY_LONGITUDE = "longitude";

    //base url uat
    public static final String BASE_URL_UAT="uat.ideationts.com:8080";
    //query urls
    public static final String URL_GET_ALL_BUS_DATA = "http://"+BASE_URL_UAT+"/app/vehicles/getVehicles.json";
    public static final String URL_GET_ALL_ROUTE_DATA = "http://"+BASE_URL_UAT+"/app/routes/getAllRoutes.json";
    public static final String URL_GET_ALL_AMBULANCE_DATA = "";
    public static final String VEHICLE_TYPE_BUS = "bus";
    public static final String VEHICLE_TYPE_AMBULANCE = "ambulance";


    public static final String TITLE_MAP_PERSON = "You";
    public static final String REQ_ID_GET_ALL_BUS_DATA = "KJHKJH687687KJKJKKLJ6";

    public static final short MAP_TILE_DOWNLOAD_THREADS =25;
    public static final short MAP_TILE_CACHE_COUNT = 1050;

    //icon aligning angle
    public static final int ANGLE_OFFSET_FOR_BUS_ICON = 0 ;
    public static final int ANGLE_OFFSET_FOR_AMBULANCE_ICON = 0 ;

    public static final long TIME_INTERVAL_IN_MILI_VEHICLE_DATA_REFRESH = 10000;
    public static final long TIME_INTERVAL_IN_MILI_ICON_ANIMATE = 2000;
    public static final double ANGLE_MIN_DIFF_FOR_BUS_ICON_ANIMATE = 10 ;
    public static final float BUS_ICON_TEXT_SIZE = 25;
    public static final int ZOOM_LEVEL_MIN = 14;
    public static final int ZOOM_LEVEL_MAX = 18;
    public static final String TOAST_CHANGE_APP_PERMISSION_FOR_GPS = "Change app permission to allow GPS!";
    public static final String TOAST_CHANGE_APP_PERMISSION_FOR_STORAGE = "Change app permission to allow External Storage!";

    //If bus moves more than this distance then the road is not crowded
    public static final int DISTANCE_LIMIT_IN_METER_BUSY_ROAD = 10;

    public static final int SERVER_TIMEOUT_IN_MILI =1500;


}
