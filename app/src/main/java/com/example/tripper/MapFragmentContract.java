package com.example.tripper;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public interface MapFragmentContract {

    interface View {
        void addMarker(Marker marker);

        void removeMarker(Marker marker);
        void removeAllMarkers(ArrayList<Marker> markers);
        void removeAllRoads(ArrayList<Polyline> polylines);
        void drawRoads(ArrayList<Polyline> roads);
        void defaultSettings();
    }

    interface Presenter {
        void clearMap();

        void removeMarker(Marker marker);

        void addMarker(GeoPoint geoPoint, MapView map);
        void calculateRoad();
    }
}
