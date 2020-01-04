package com.example.tripper.model;

import com.google.gson.annotations.SerializedName;

public class TripPoint extends Model {

    @SerializedName(value = "trip_id")
    private int tripId;
    @SerializedName(value = "point_id")
    private int pointId;

    TripPoint(int id) {
        super(id);
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getUserId() {
        return pointId;
    }

    public void setUserId(int userId) {
        this.pointId = userId;
    }
}
