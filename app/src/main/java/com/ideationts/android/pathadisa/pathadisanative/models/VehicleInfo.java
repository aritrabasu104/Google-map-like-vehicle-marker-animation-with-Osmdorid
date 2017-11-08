package com.ideationts.android.pathadisa.pathadisanative.models;

/**
 * Created by ARITRA on 05-07-2017.
 */

public class VehicleInfo {
    private String dataSource;
    private String vehicleNo;
    private String routeCode;
    private boolean journeyStarted;
    private boolean outOfPath;
    private PointMap<String,Double> lastLocation;
    private long lastTime;
    private long timeToDestinationStop;
    private double speed;
    private String direction;
    private int angle;
    private String crowd;
    private String vehicleType;

    public String getDataSource() {
        return dataSource;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public boolean isJourneyStarted() {
        return journeyStarted;
    }

    public boolean isOutOfPath() {
        return outOfPath;
    }

    public PointMap<String, Double> getLastLocation() {
        return lastLocation;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long getTimeToDestinationStop() {
        return timeToDestinationStop;
    }

    public double getSpeed() {
        return speed;
    }

    public String getDirection() {
        return direction;
    }

    public int getAngle() {
        return angle;
    }

    public String getCrowd() {
        return crowd;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    private Vehicle vehicle;
}
