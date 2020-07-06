package com.example.tripper.viewmodel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.example.tripper.algorithm.CMeans;
import com.example.tripper.algorithm.Centroid;
import com.example.tripper.algorithm.FuzzyCMeans;
import com.example.tripper.model.Point;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private GeoPoint currentCenter = new GeoPoint(52.13, 19.63);
    private ArrayList<GeoPoint> currentPoints;
    private double currentZoomLevel = 15d;

    private int days = 1;
    private String navigationType = "Fastest";
    private ITileSource tileSource = TileSourceFactory.MAPNIK;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ArrayList<GeoPoint>> calculateRoad() {

        CMeans fuzzyCMeans = new FuzzyCMeans(days, 0.0001, 2, currentPoints);
        ArrayList<Centroid> centroids = fuzzyCMeans.calculate();

        ArrayList<ArrayList<GeoPoint>> trips = new ArrayList<>();
        fuzzyCMeans.generateClusters();
        for (Centroid centroid : centroids
        ) {
            ArrayList<GeoPoint> wps = new ArrayList<>();
            ArrayList<GeoPoint> tripPoints = new ArrayList<>();
            for (GeoPoint marker : RNN(centroid.markers)
            ) {
                wps.add(marker);
                tripPoints.add(marker);
            }
            trips.add(tripPoints);
        }
        return trips;
    }

    private ArrayList<GeoPoint> RNN(ArrayList<GeoPoint> group) {
        int amount = group.size();
        ArrayList<GeoPoint> order = new ArrayList<>();
        ArrayList<GeoPoint> finalOrder = new ArrayList<>();
        double currentDistance;
        double prevDistance = 0;

        for (int j = 0; j < amount; j++) {

            order.clear();
            order.add(group.get(j));
            currentDistance = 0;

            for (int i = 1; i < amount; i++) {
                int a = -1;
                for (GeoPoint marker : group
                ) {
                    if (!order.contains(marker)) {
                        if (a == -1) {
                            a = group.indexOf(marker);
                        } else if (marker.distanceToAsDouble(order.get(i - 1)) < group.get(a).distanceToAsDouble(order.get(i - 1))) {
                            a = group.indexOf(marker);
                        }
                    }
                }
                order.add(group.get(a));
                currentDistance += order.get(i).distanceToAsDouble(order.get(i - 1));
            }

            if (currentDistance < prevDistance || finalOrder.isEmpty()) {
                finalOrder = (ArrayList<GeoPoint>) order.clone();
                prevDistance = currentDistance;
            }
        }

        return order;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public ITileSource getTileSource() {
        return tileSource;
    }

    public void setTileSource(ITileSource tileSource) {
        this.tileSource = tileSource;
    }

    public String getNavigationType() {
        System.out.println(navigationType);
        return navigationType;
    }

    public void setNavigationType(String navigationType) {
        this.navigationType = navigationType;
    }

    public double getCurrentZoomLevel() {
        return currentZoomLevel;
    }

    public void setCurrentZoomLevel(double currentZoomLevel) {
        this.currentZoomLevel = currentZoomLevel;
    }

    public GeoPoint getCurrentCenter() {
        return currentCenter;
    }

    public void setCurrentCenter(GeoPoint currentCenter) {
        this.currentCenter = currentCenter;
    }

    public ArrayList<GeoPoint> getCurrentPoints() {
        if (currentPoints == null) {
            currentPoints = new ArrayList<>();
        }
        return currentPoints;
    }

    public void setCurrentPoints(ArrayList<GeoPoint> points) {
        currentPoints = points;
    }

    public void loadCurrentPoints(List<Point> points) {
        currentPoints = new ArrayList<>();
        for (Point point : points) {
            currentPoints.add(new GeoPoint(point.getLatitude(), point.getLongitude()));
        }
    }
}
