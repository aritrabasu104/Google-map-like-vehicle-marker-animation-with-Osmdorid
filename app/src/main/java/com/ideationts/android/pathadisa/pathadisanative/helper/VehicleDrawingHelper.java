package com.ideationts.android.pathadisa.pathadisanative.helper;

import android.util.Log;
import com.ideationts.android.pathadisa.pathadisanative.models.VehicleInfo;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ARITRA on 14-07-2017.
 */

public class VehicleDrawingHelper {
    private static VehicleDrawingHelper instance=null;
    private static List<VehicleInfo> previousBusList;
    private static List<VehicleInfo> currentBusList;
    private Map<String,VehicleInfo> oldVehiclesInfoMap;
    private Map<String,VehicleInfo> newVehiclesInfoMap;
    private List<String> oldVehicleNos;

    public Map<String, VehicleInfo> getOldVehiclesInfoMap() {
        return oldVehiclesInfoMap;
    }

    public Map<String, VehicleInfo> getNewVehiclesInfoMap() {
        return newVehiclesInfoMap;
    }

    public List<String> getOldVehicleNos() {
        return oldVehicleNos;
    }

    public List<String> getNewVehicleNos() {
        return newVehicleNos;
    }

    public List<String> getComVehicleNos() {
        return comVehicleNos;
    }

    private List<String> newVehicleNos;
    private List<String> comVehicleNos;

    private VehicleDrawingHelper() {

    }

    public static synchronized VehicleDrawingHelper getInstance(List<VehicleInfo> oldBusList,List<VehicleInfo> currentBusList) {
        VehicleDrawingHelper.previousBusList=oldBusList;
        VehicleDrawingHelper.currentBusList=currentBusList;
        instance.calculate();
        return instance;
    }
    public static synchronized void createInstance() {
        instance = new VehicleDrawingHelper();

    }
    private void calculate(){

        //getting the vehicles mapped according to vehicle number
        oldVehiclesInfoMap = new HashMap<>();
        for(VehicleInfo bus:previousBusList)
            oldVehiclesInfoMap.put(bus.getVehicleNo(),bus);
        newVehiclesInfoMap = new HashMap<>();
        for(VehicleInfo bus:currentBusList)
            newVehiclesInfoMap.put(bus.getVehicleNo(),bus);

        //creating initial collections of vehicle numbers
        oldVehicleNos =new ArrayList<>(oldVehiclesInfoMap.keySet());
        newVehicleNos =new ArrayList<>(newVehiclesInfoMap.keySet());
        comVehicleNos = new ArrayList<>(newVehicleNos);

        //getting actual old,common and new vehicle numbers
        comVehicleNos.retainAll(oldVehicleNos);
        newVehicleNos.removeAll(comVehicleNos);
        oldVehicleNos.removeAll(comVehicleNos);
        Log.d("calculate","common:"+comVehicleNos);
        Log.d("calculate","newVehicleNos:"+newVehicleNos);
        Log.d("calculate","oldVehicleNos:"+oldVehicleNos);


    }
}
