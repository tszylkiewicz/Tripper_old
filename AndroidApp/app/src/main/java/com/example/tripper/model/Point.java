package com.example.tripper.model;

import com.google.gson.annotations.SerializedName;

public class Point extends Model {

    @SerializedName("trip_id")
    private int tripId;
    private double longitude;
    private double latitude;
    private double altitude;

    Point(int id, int tripId, double longitude, double latitude, double altitude) {
        super(id);
        this.tripId = tripId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
