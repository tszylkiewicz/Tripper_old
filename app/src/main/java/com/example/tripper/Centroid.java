package com.example.tripper;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class Centroid {

    public GeoPoint position;

    public ArrayList<Marker> markers;

    public Centroid(GeoPoint pos) {
        this.position = pos;
        this.markers = new ArrayList<>();
    }

    public void calcuteNewPosition() {
        double lat = 0;
        double lon = 0;
        for (Marker marker : markers
        ) {
            lat += marker.getPosition().getLatitude();
            lon += marker.getPosition().getLongitude();
        }
        lat = lat / markers.size();
        lon = lon / markers.size();
        this.position = new GeoPoint(lat, lon);
    }

    public void clearMarkers() {
        this.markers.clear();
    }
}
