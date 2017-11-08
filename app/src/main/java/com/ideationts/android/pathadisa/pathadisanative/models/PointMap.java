package com.ideationts.android.pathadisa.pathadisanative.models;

import com.ideationts.android.pathadisa.pathadisanative.data.ApplicationConstants;

import java.text.ParseException;
import java.util.HashMap;

/**
 * Created by ARITRA on 05-07-2017.
 */

public class PointMap<K extends String,V extends Double> extends HashMap{
    public static final String KEY_LATITUDE= ApplicationConstants.REQUEST_ALL_VEHICLE_MAPKEY_LATITUDE;
    public static final String KEY_LONGITUDE=ApplicationConstants.REQUEST_ALL_VEHICLE_MAPKEY_LONGITUDE;

    @Override
    public Double get(Object key) {
        String skey=String.valueOf(key);
        try {
            return Double.parseDouble(super.get(skey).toString());
        }catch (NumberFormatException e)
        {
            e.printStackTrace();
            return -999d;
        }
    }
}
