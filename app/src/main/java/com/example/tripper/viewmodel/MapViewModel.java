package com.example.tripper.viewmodel;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripper.model.CMeans;
import com.example.tripper.model.Centroid;
import com.example.tripper.model.FuzzyCMeans;
import com.example.tripper.model.HeldKarpDouble;
import com.example.tripper.model.enums.TransportType;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapViewModel extends ViewModel {

    private MutableLiveData<Integer> days;
    private MutableLiveData<TransportType> transportType;

    private ITileSource tileSource;
    private String navigationType;

    private MutableLiveData<ArrayList<Marker>> markersLiveData;
    private MutableLiveData<ArrayList<Polyline>> routesLiveData;

    private MutableLiveData<ArrayList<GeoPoint>> centroids;

    private ArrayList<Marker> markers;
    private ArrayList<Polyline> routes;
    private Road[] roads;


    private double currentZoomLevel = 15d;
    private GeoPoint currentCenter = new GeoPoint(51.13, 19.63);

    private ArrayList<GeoPoint> currentPoints;


    public ArrayList<Marker> getMarkers() {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        return markers;
    }

    public void addMarker(Marker marker) {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        markers.add(marker);
        System.out.println("Add: " + markers.size());
    }

    public void removeMarker(Marker marker) {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        markers.remove(marker);
        System.out.println("Remove: " + markers.size());
    }

    public void removeAllMarkers() {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        if (!markers.isEmpty()) {
            markers.clear();
        }
    }

    public ArrayList<Polyline> getRoutes() {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        return routes;
    }

    public void removeAllRoutes() {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        if (!routes.isEmpty()) {
            routes.clear();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ArrayList<Marker>> calculateRoad(RoadManager roadManager) {
        routes.clear();
        //kMeansAlgorithm(roadManager);
        if (days.getValue() == 0) {
            days.setValue(1);
        }
        return fuzzyCMeans(roadManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Polyline> kMeansAlgorithm(RoadManager roadManager) {
        int k = days.getValue();
        int temp = markers.size() / k;
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
                            routes.add(roadOverlay);
                        }
                    }
                    //this.view.drawRoads(routes);

                }
            }
        }
        return routes;
    }


    public ArrayList<ArrayList<Marker>> fuzzyCMeans(RoadManager roadManager) {
        CMeans fuzzyCMeans = new FuzzyCMeans(days.getValue(), 0.0001, 2, markers);
        //CMeans fuzzyCMeans = new PossibilisticCMeans(days.getValue(), 0.0001, 2, markers);

        ArrayList<Centroid> centroids = fuzzyCMeans.calculate();
        Random rnd = new Random();

        this.centroids.getValue().clear();
        for (int i = 0; i < centroids.size(); i++) {
            //addMarker(centroids.get(i).position, this.mapView, null);
            addCentroid(centroids.get(i).position);
        }

        ArrayList<ArrayList<Marker>> trips = new ArrayList<>();
        fuzzyCMeans.generateClusters();
        for (Centroid centroid : centroids
        ) {
            ArrayList<GeoPoint> wps = new ArrayList<>();
            ArrayList<Marker> tripPoints = new ArrayList<>();
            //HeldKarpAlgorithm(centroid.markers);
            //for (Marker marker : RNN(centroid.markers)
            //for (Marker marker : ThreeOpt(centroid.markers)
            for (Marker marker : HeldKarpAlgorithm(centroid.markers)
            ) {
                wps.add(marker.getPosition());
                tripPoints.add(marker);
            }
            trips.add(tripPoints);
            /*roads = roadManager.getRoads(wps);

            for (Road singleRoad : roads
            ) {
                if (singleRoad.mStatus != Road.STATUS_OK) {
                    Log.d("Road Status", "" + singleRoad.mStatus);
                } else {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                    roadOverlay.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                    roadOverlay.setWidth(8);
                    routes.add(roadOverlay);
                }
            }*/
            //this.view.drawRoads(routes);
        }
        return trips;
        //return routes;
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
        //System.out.println("Amount: " + amount);
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
            // System.out.println(marker.getPosition());
        }
        System.out.println("Total distance: " + prevDistance);
        return order;
    }

    public ArrayList<Marker> NN(ArrayList<Marker> group) {
        int amount = group.size();
        System.out.println("Amount: " + amount);
        ArrayList<Marker> order = new ArrayList<>();
        ArrayList<Marker> finalOrder = new ArrayList<>();
        double currentDistance;
        double prevDistance = 0;

        order.clear();
        order.add(group.get(0));
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


        for (Marker marker : finalOrder) {
            System.out.println(marker.getPosition());
        }
        System.out.println("Total distance: " + prevDistance);
        return order;
    }

    private ArrayList<Marker> groupOpt;
    private ArrayList<Marker> newGroupOpt;

    private ArrayList<Marker> TwoOpt(ArrayList<Marker> group) {
        // Get tour size
        System.out.println("---TWO OPT BEGIN---");
        this.groupOpt = group;
        newGroupOpt = new ArrayList<>();
        int size = groupOpt.size();

        //CHECK THIS!!
        for (int i = 0; i < size; i++) {
            newGroupOpt.add(i, groupOpt.get(i));
        }

        // repeat until no improvement is made
        int improve = 0;
        int iteration = 0;

        while (improve < size) {
            double best_distance = routeDistance(groupOpt);

            for (int i = 1; i < size - 1; i++) {
                for (int k = i + 1; k < size; k++) {
                    TwoOptSwap(i, k);
                    iteration++;
                    double new_distance = routeDistance(newGroupOpt);

                    if (new_distance < best_distance) {
                        // Improvement found so reset
                        improve = 0;

                        for (int j = 0; j < size; j++) {
                            groupOpt.set(j, newGroupOpt.get(j));
                        }

                        best_distance = new_distance;

                        System.out.println("---Begin---");
                        for (int g = 0; g < size; g++) {
                            System.out.print(groupOpt.get(g) + ", ");
                        }
                        System.out.println();
                        // Update the display
                        //NotifyTourUpdate(_tour, Double.toString(best_distance), Integer.toString(iteration));
                    }
                }
            }

            improve++;
        }
        return groupOpt;
    }

    private ArrayList<Marker> ThreeOpt(ArrayList<Marker> group) {
        // Get tour size
        System.out.println("---THREE OPT BEGIN---");
        this.groupOpt = group;
        newGroupOpt = new ArrayList<>();
        int size = groupOpt.size();

        //CHECK THIS!!
        for (int i = 0; i < size; i++) {
            newGroupOpt.add(i, groupOpt.get(i));
        }

        // repeat until no improvement is made
        int improve = 0;
        int iteration = 0;

        while (improve < size) {
            double best_distance = routeDistance(groupOpt);

            for (int i = 1; i < size - 3; i++) {
                for (int j = i + 1; j < size - 2; j++) {
                    for (int k = j + 1; k < size - 1; k++) {
                        TwoOptSwap(i, k);
                        TwoOptSwap(j, k);
                        iteration++;
                        double new_distance = routeDistance(newGroupOpt);

                        if (new_distance < best_distance) {
                            // Improvement found so reset
                            improve = 0;

                            for (int q = 0; q < size; q++) {
                                groupOpt.set(q, newGroupOpt.get(q));
                            }

                            best_distance = new_distance;

                            System.out.println("---Begin---");
                            for (int g = 0; g < size; g++) {
                                System.out.print(groupOpt.get(g) + ", ");
                            }
                            System.out.println();
                            // Update the display
                            //NotifyTourUpdate(_tour, Double.toString(best_distance), Integer.toString(iteration));
                        }
                    }
                }
            }

            improve++;
        }
        return groupOpt;
    }

    private void TwoOptSwap(int i, int k) {
        int size = groupOpt.size();

        // 1. take route[0] to route[i-1] and add them in order to new_route
        for (int c = 0; c <= i - 1; ++c) {
            newGroupOpt.set(c, groupOpt.get(c));
        }

        // 2. take route[i] to route[k] and add them in reverse order to new_route
        int dec = 0;
        for (int c = i; c <= k; ++c) {
            newGroupOpt.set(c, groupOpt.get(k - dec));
            dec++;
        }

        // 3. take route[k+1] to end and add them in order to new_route
        for (int c = k + 1; c < size; ++c) {
            newGroupOpt.set(c, groupOpt.get(c));
        }
    }

    private ArrayList<Marker> HeldKarpAlgorithm(ArrayList<Marker> points) {
        int size = points.size();
        double[][] distanceMatrix = new double[size][size];
        for (Marker marker :
                points) {
            for (Marker marker2 :
                    points) {
                distanceMatrix[points.indexOf(marker)][points.indexOf(marker2)] = marker.getPosition().distanceToAsDouble(marker2.getPosition());
            }
        }
        System.out.println("---DISTANCE MATRIX---");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(distanceMatrix[i][j] + ", ");
            }
            System.out.println();
        }
        System.out.println("---END DISTANCE MATRIX---");

        double[][] test1 = {
                {0, 2, 9, 10},
                {1, 0, 6, 4},
                {15, 7, 0, 8},
                {6, 3, 12, 0}
        };

        double[][] test2 = {
                {0, 49, 34, 96, 74},
                {49, 0, 10, 94, 43},
                {34, 10, 0, 21, 6},
                {96, 94, 21, 0, 70},
                {74, 43, 6, 70, 0}
        };

        double[][] test3 = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };

        //HeldKarp test = new HeldKarp(distanceMatrix, 0);
        HeldKarpDouble test = new HeldKarpDouble(distanceMatrix, 0);
        List<Integer> solution = test.calculateHeldKarp();


        ArrayList<Marker> resultSet = new ArrayList<>();

        for (int i = 0; i < solution.size() - 1; i++) {
            resultSet.add(points.get(solution.get(i)));
        }
        System.out.println("---HELD KARP SOLUTION---");
        System.out.println(solution + ";");
        System.out.println("---END HELD KARP SOLUTION---");

        return resultSet;
    }

    private double routeDistance(ArrayList<Marker> group) {
        double result = 0;
        for (int i = 0; i < group.size() - 1; i++) {
            result += group.get(i).getPosition().distanceToAsDouble(group.get(i + 1).getPosition());
        }
        return result;
    }

    public MutableLiveData<Integer> getDays() {
        if (days == null) {
            days = new MutableLiveData<>();
            days.setValue(1);
        }
        return days;
    }

    public void setDays(int days) {
        this.days.setValue(days);
    }

    public MutableLiveData<TransportType> getTransportType() {
        if (transportType == null) {
            transportType = new MutableLiveData<>();
            transportType.setValue(TransportType.CAR);
        }
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType.setValue(transportType);
    }

    public MutableLiveData<ArrayList<Marker>> getMarkersLiveData() {
        if (markersLiveData == null) {
            markersLiveData = new MutableLiveData<>();
            markers = new ArrayList<>();
            markersLiveData.setValue(markers);
        }
        return markersLiveData;
    }

    public MutableLiveData<ArrayList<Polyline>> getRoutesLiveData() {
        if (routesLiveData == null) {
            routesLiveData = new MutableLiveData<>();
            routes = new ArrayList<>();
            routesLiveData.setValue(routes);
        }
        return routesLiveData;
    }

    public MutableLiveData<ArrayList<GeoPoint>> getCentroids() {
        if (centroids == null) {
            centroids = new MutableLiveData<>();
            centroids.setValue(new ArrayList<>());
        }
        return centroids;
    }

    public void addCentroid(GeoPoint centroid) {
        centroids.getValue().add(centroid);
        centroids.setValue(centroids.getValue());
    }

    public void removeRoute(Polyline polyline) {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        routes.remove(polyline);
        System.out.println("Route removed");
    }

    public ITileSource getTileSource() {
        if (tileSource == null) {
            tileSource = TileSourceFactory.MAPNIK;
        }
        return tileSource;
    }

    public void setTileSource(ITileSource tileSource) {
        this.tileSource = tileSource;
    }

    public String getNavigationType() {
        if (navigationType == null) {
            navigationType = "Fastest";
        }
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
}
