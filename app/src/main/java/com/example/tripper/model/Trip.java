package com.example.tripper.model;

import com.example.tripper.model.enums.TransportType;

import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class Trip extends Model {

    private User user;
    private ArrayList<Marker> points;
    private String name;
    private String description;
    private double distance;
    private TransportType transportType;
    private double rating;
    private int ratingCount;
    private boolean shared;

    public Trip(int id, User user, ArrayList<Marker> points, String name, String description, double distance, TransportType transportType, double rating, int ratingCount, boolean shared) {
        super(id);
        this.user = user;
        this.points = points;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.transportType = transportType;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.shared = shared;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Marker> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Marker> points) {
        this.points = points;
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

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
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

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
