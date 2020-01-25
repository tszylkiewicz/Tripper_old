package com.example.tripper.viewmodel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripper.algorithm.CMeans;
import com.example.tripper.algorithm.Centroid;
import com.example.tripper.algorithm.FuzzyCMeans;

import com.example.tripper.model.Point;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private MutableLiveData<ArrayList<GeoPoint>> centroids;

    /**************************************************************************************************/
    private GeoPoint currentCenter = new GeoPoint(52.13, 19.63);
    private ArrayList<GeoPoint> currentPoints;
    private double currentZoomLevel = 15d;

    private int days = 1;
    private String navigationType = "Fastest";
    private ITileSource tileSource = TileSourceFactory.MAPNIK;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ArrayList<GeoPoint>> calculateRoad() {

        CMeans fuzzyCMeans = new FuzzyCMeans(days, 0.0001, 2, currentPoints);

        //CMeans fuzzyCMeans = new HardCMeans(4, 0.0001, 2, currentPoints);
        //CMeans fuzzyCMeans = new FuzzyCMeans(4, 0.0001, 2, currentPoints);
        //CMeans fuzzyCMeans = new PossibilisticCMeans(4, 0.0001, 2, currentPoints);

        ArrayList<Centroid> centroids = fuzzyCMeans.calculate();

        this.centroids.getValue().clear();
        for (int i = 0; i < centroids.size(); i++) {
            addCentroid(centroids.get(i).position);
        }

        ArrayList<ArrayList<GeoPoint>> trips = new ArrayList<>();
        fuzzyCMeans.generateClusters();
        for (Centroid centroid : centroids
        ) {
            ArrayList<GeoPoint> wps = new ArrayList<>();
            ArrayList<GeoPoint> tripPoints = new ArrayList<>();
            //HeldKarpAlgorithm(centroid.markers);
            for (GeoPoint marker : RNN(centroid.markers)
            //for (GeoPoint marker : ThreeOpt(centroid.markers)
            //for (GeoPoint marker : HeldKarpAlgorithm(centroid.markers)
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
        //System.out.println("Amount: " + amount);
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

        for (GeoPoint marker : finalOrder) {
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

    private ArrayList<GeoPoint> groupOpt;
    private ArrayList<GeoPoint> newGroupOpt;

    private ArrayList<GeoPoint> TwoOpt(ArrayList<GeoPoint> group) {
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

    private ArrayList<GeoPoint> ThreeOpt(ArrayList<GeoPoint> group) {
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

    /*private ArrayList<GeoPoint> HeldKarpAlgorithm(ArrayList<GeoPoint> points) {
        int size = points.size();
        double[][] distanceMatrix = new double[size][size];
        for (GeoPoint marker :
                points) {
            for (GeoPoint marker2 :
                    points) {
                distanceMatrix[points.indexOf(marker)][points.indexOf(marker2)] = marker.distanceToAsDouble(marker2);
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


        HeldKarpDouble test = new HeldKarpDouble(distanceMatrix, 0);
        List<Integer> solution = test.calculateHeldKarp();


        ArrayList<GeoPoint> resultSet = new ArrayList<>();

        for (int i = 0; i < solution.size() - 1; i++) {
            resultSet.add(points.get(solution.get(i)));
        }
        System.out.println("---HELD KARP SOLUTION---");
        System.out.println(solution + ";");
        System.out.println("---END HELD KARP SOLUTION---");

        return resultSet;
    }*/

    private double routeDistance(ArrayList<GeoPoint> group) {
        double result = 0;
        for (int i = 0; i < group.size() - 1; i++) {
            result += group.get(i).distanceToAsDouble(group.get(i + 1));
        }
        return result;
    }


    public MutableLiveData<ArrayList<GeoPoint>> getCentroids() {
        if (centroids == null) {
            centroids = new MutableLiveData<>();
            centroids.setValue(new ArrayList<>());
        }
        return centroids;
    }

    private void addCentroid(GeoPoint centroid) {
        centroids.getValue().add(centroid);
        centroids.setValue(centroids.getValue());
    }


    /**************************************************************************************************/
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
