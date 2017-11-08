package com.ideationts.android.pathadisa.pathadisanative.models;

import java.util.List;

/**
 * Created by ARITRA on 05-07-2017.
 */

public class AllVehicleResponse {
    private String requestId;
    private Long duration;
    private String status;

    public String getRequestId() {
        return requestId;
    }

    public Long getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getCount() {
        return count;
    }

    public List<VehicleInfo> getData() {
        return data;
    }


    private String message;
    private Long count;
    private List<VehicleInfo> data;
}
