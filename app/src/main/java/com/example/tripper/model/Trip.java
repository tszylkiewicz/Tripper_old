package com.example.tripper.model;

import com.google.gson.annotations.SerializedName;

public class Trip extends Model {


    @SerializedName(value = "user_id")
    private int userId;
    private String name;
    private String description;
    private double distance;
    @SerializedName(value = "transport_type")
    private String transportType;
    private double rating;
    @SerializedName(value = "rating_count")
    private int ratingCount;
    private int shared;

    public Trip() {
        super(1);

    }

    public Trip(int id, int userId, String name, String description, double distance, String transportType, double rating, int ratingCount, int shared) {
        super(id);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.transportType = transportType;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.shared = shared;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int isShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public String getAllData(){
        StringBuilder builder = new StringBuilder();
        builder.append("Id: " + getId() + "\n");
        builder.append("user: " + userId + "\n");
        builder.append("name: " + name + "\n");
        builder.append("description: " + description + "\n");
        builder.append("distance: " + distance + "\n");
        builder.append("transportType: " + transportType + "\n");
        builder.append("rating: " + rating + "\n");
        builder.append("ratingCount: " + ratingCount + "\n");
        builder.append("shared: " + shared + "\n");

        return new String(builder);
    }
}
