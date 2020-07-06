package com.example.tripper.algorithm;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class Centroid {

    public GeoPoint position;

    public ArrayList<GeoPoint> markers;

    public Centroid(GeoPoint pos) {
        this.position = pos;
        this.markers = new ArrayList<>();
    }

}
