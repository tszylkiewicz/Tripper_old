package com.example.tripper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.tripper.model.CMeans;
import com.example.tripper.model.Centroid;
import com.example.tripper.model.FuzzyCMeans;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapFragmentPresenter implements MapFragmentContract.Presenter {

    public static int days;
    public static int type;
    public static int max_distance;

    public ArrayList<Marker> markers;
    ArrayList<Polyline> roadsOnMap;


    public RoadManager roadManager;
    public Road[] roads;
    public MapView mapView;
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
        this.view.removeMarker(marker);
        markers.remove(marker);
    }

    @Override
    public void addMarker(GeoPoint geoPoint, MapView map, Drawable icon) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setTitle("Element");
        if (icon != null) {
            marker.setIcon(icon);
        }
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            removeMarker(marker1);
            return false;
        });
        markers.add(marker);
        this.view.addMarker(marker);
        this.mapView = map;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void calculateRoad() {
        this.view.removeAllRoads(roadsOnMap);
        roadsOnMap.clear();
        //kMeansAlgorithm();

        fuzzyCMeans();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void kMeansAlgorithm() {
        int k = days;
        int temp = markers.size() / days;
        ArrayList<Centroid> centroids = new ArrayList<>();
        Map<GeoPoint, ArrayList<Marker>> test = new HashMap<>();
        for (int i = 0; i < k; i++) {
            centroids.add(new Centroid(markers.get(i * temp).getPosition()));
            //System.out.println(markers.get(i * temp).getPosition());
        }
        Random rnd = new Random();

        for (int i = 0; i < 10000; i++) {
            for (Marker marker : markers
            ) {
                GeoPoint markerPos = marker.getPosition();
                Centroid selectedCentroid = centroids.get(0);

                for (Centroid centroid : centroids
                ) {
                    if (centroid.position.distanceToAsDouble(markerPos) < selectedCentroid.position.distanceToAsDouble(markerPos)) {
                        selectedCentroid = centroid;
                    }
                }
                selectedCentroid.markers.add(marker);
            }

            for (Centroid centroid : centroids
            ) {
                centroid.calcuteNewPosition();
                if (i != 999) {
                    centroid.clearMarkers();
                } else {
                    //System.out.println(centroid.markers.size());
                    //System.out.println(centroid.markers);
                    ArrayList<GeoPoint> wps = new ArrayList<>();
                    for (Marker marker : centroid.markers
                    ) {
                        wps.add(marker.getPosition());
                    }
                    roads = roadManager.getRoads(wps);

                    for (Road singleRoad : roads
                    ) {
                        if (singleRoad.mStatus != Road.STATUS_OK) {
                            Log.d("Road Status", "" + singleRoad.mStatus);
                        } else {
                            Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                            roadOverlay.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                            roadOverlay.setWidth(7);
                            roadsOnMap.add(roadOverlay);
                        }
                    }
                    this.view.drawRoads(roadsOnMap);
                }
            }
        }
    }


    public void fuzzyCMeans() {
        CMeans fuzzyCMeans = new FuzzyCMeans(days, 0.0001, 2, markers);
        //CMeans fuzzyCMeans = new PossibilisticCMeans(days, 0.00001, 2, markers);

        ArrayList<Centroid> centroids = fuzzyCMeans.calculate();
        Random rnd = new Random();

        for (int i = 0; i < centroids.size(); i++) {
            addMarker(centroids.get(i).position, this.mapView, null);
        }

        fuzzyCMeans.generateClusters();
        for (Centroid centroid : centroids
        ) {
            ArrayList<GeoPoint> wps = new ArrayList<>();
            for (Marker marker : RNN(centroid.markers)
            ) {
                wps.add(marker.getPosition());
            }
            roads = roadManager.getRoads(wps);

            for (Road singleRoad : roads
            ) {
                if (singleRoad.mStatus != Road.STATUS_OK) {
                    Log.d("Road Status", "" + singleRoad.mStatus);
                } else {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                    roadOverlay.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                    roadOverlay.setWidth(8);
                    roadsOnMap.add(roadOverlay);
                }
            }
            this.view.drawRoads(roadsOnMap);
        }

        //TEST SET
        /*ArrayList<Marker> testSet = new ArrayList<>();

        Marker marker1 = new Marker(mapView);
        marker1.setPosition(new GeoPoint(4d, 4d));
        testSet.add(marker1);

        Marker marker2 = new Marker(mapView);
        marker2.setPosition(new GeoPoint(5d, 5d));
        testSet.add(marker2);

        Marker marker3 = new Marker(mapView);
        marker3.setPosition(new GeoPoint(2d, 2d));
        testSet.add(marker3);

        Marker marker4 = new Marker(mapView);
        marker4.setPosition(new GeoPoint(1d, 1d));
        testSet.add(marker4);

        Marker marker5 = new Marker(mapView);
        marker5.setPosition(new GeoPoint(3d, 3d));
        testSet.add(marker5);

        RNN(testSet);*/
    }

    public ArrayList<Marker> RNN(ArrayList<Marker> group) {
        int amount = group.size();
        System.out.println("Amount: " + amount);
        ArrayList<Marker> order = new ArrayList<>();
        ArrayList<Marker> finalOrder = new ArrayList<>();
        double currentDistance;
        double prevDistance = 0;

        for (int j = 0; j < amount; j++) {

            order.clear();
            order.add(group.get(j));
            currentDistance = 0;

            for (int i = 1; i < amount; i++) {
                int a = -1;
                for (Marker marker : group
                ) {
                    if (!order.contains(marker)) {
                        if (a == -1) {
                            a = group.indexOf(marker);
                        } else if (marker.getPosition().distanceToAsDouble(order.get(i - 1).getPosition()) < group.get(a).getPosition().distanceToAsDouble(order.get(i - 1).getPosition())) {
                            a = group.indexOf(marker);
                        }
                    }
                }
                order.add(group.get(a));
                currentDistance += order.get(i).getPosition().distanceToAsDouble(order.get(i - 1).getPosition());
            }

            if (currentDistance < prevDistance || finalOrder.isEmpty()) {
                finalOrder = (ArrayList<Marker>) order.clone();
                prevDistance = currentDistance;
            }
        }

        for (Marker marker : finalOrder) {
            System.out.println(marker.getPosition());
        }
        System.out.println("Total distance: " + prevDistance);
        return order;
    }
}

