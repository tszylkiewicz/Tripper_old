package com.example.tripper;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class MapFragmentPresenter implements MapFragmentContract.Presenter {

    public static int days;
    public static int type;
    public static int max_distance;

    public ArrayList<Marker> markers;
    ArrayList<Polyline> roadsOnMap;

    public RoadManager roadManager;
    public Road[] roads;

    public MapFragmentContract.View view;

    public MapFragmentPresenter(MapFragmentContract.View view, Context context) {
        this.view = view;
        roadManager = new OSRMRoadManager(context);
        this.view.defaultSettings();
        markers = new ArrayList<>();
        roadsOnMap = new ArrayList<>();
        days = 1;
        type = 0;
    }

    @Override
    public void clearMap() {
        this.view.removeAllMarkers(markers);
        this.view.removeAllRoads(roadsOnMap);
        if (!markers.isEmpty()) {
            markers.clear();
        }
        if (!roadsOnMap.isEmpty()) {
            roadsOnMap.clear();
        }
    }

    @Override
    public void removeMarker(Marker marker) {

    }

    @Override
    public void addMarker(GeoPoint geoPoint, MapView map) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setTitle("Element");
        markers.add(marker);
        this.view.addMarker(marker);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void calculateRoad() {
        int equal = markers.size() / days;
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        this.view.removeAllRoads(roadsOnMap);
        roadsOnMap.clear();
        for (int i = 0; i < days; i++) {
            waypoints.clear();
            for (int j = i * equal; j < (i + 1) * equal; j++) {
                waypoints.add(markers.get(j).getPosition());
            }
            roads = roadManager.getRoads(waypoints);

            for (Road singleRoad : roads
            ) {
                if (singleRoad.mStatus != Road.STATUS_OK) {
                    Log.d("Road Status", "" + singleRoad.mStatus);
                } else {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                    roadOverlay.setColor(Color.GREEN);
                    roadOverlay.setWidth(7);
                    roadsOnMap.add(roadOverlay);
                }
            }
        }

        this.view.drawRoads(roadsOnMap);
    }
}
