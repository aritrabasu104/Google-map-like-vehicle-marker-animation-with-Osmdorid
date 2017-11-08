package com.ideationts.android.pathadisa.pathadisanative.models;

import java.util.Map;

/**
 * Created by ARITRA on 05-07-2017.
 */

public class DeviceInfoRequest {
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setPointNW(PointMap<String, Double> pointNW) {
        this.pointNW = pointNW;
    }

    public void setPointSE(PointMap<String, Double> pointSE) {
        this.pointSE = pointSE;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    private String requestId;
    private Map<String,Double> pointNW;
    private Map<String,Double> pointSE;
    private String vehicleType;



}
