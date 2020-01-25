package com.example.tripper;

import com.example.tripper.algorithm.CMeans;
import com.example.tripper.algorithm.Centroid;
import com.example.tripper.algorithm.FuzzyCMeans;
import com.example.tripper.algorithm.HardCMeans;
import com.example.tripper.algorithm.PossibilisticCMeans;
import com.example.tripper.algorithm.HeldKarpDouble;

import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClusteringUnitTest {

    private int days = 4;

    private ArrayList<GeoPoint> markers;

    @Test
    public void clusteringHCM() {
        initMarkers();
        System.out.println(markers.size());
        CMeans hardCMeans = new HardCMeans(days, 0.0001, 2, markers);
        long startTime = System.nanoTime();
        ArrayList<Centroid> result = hardCMeans.calculate();
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1000000d;
        System.out.println("Elapsed time " + totalTime);
        hardCMeans.generateClusters();

        for (int i = 0; i < result.size(); i++) {
            Centroid centroid = result.get(i);
            System.out.println("Obiektów w klastrze " + i + ": " + centroid.markers.size());
            double averageDistanceToCenter = 0;
            for (GeoPoint point : centroid.markers) {
                averageDistanceToCenter += point.distanceToAsDouble(centroid.position);
            }
            averageDistanceToCenter = averageDistanceToCenter / centroid.markers.size();
            System.out.println("Srednia odległosc do centrum: " + averageDistanceToCenter);
        }
    }

    @Test
    public void clusteringFCM() {
        initMarkers();
        CMeans fuzzyCMeans = new FuzzyCMeans(days, 0.0001, 2, markers);
        long startTime = System.nanoTime();
        ArrayList<Centroid> result = fuzzyCMeans.calculate();
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1000000d;
        System.out.println("Elapsed time " + totalTime);

        fuzzyCMeans.generateClusters();

        for (int i = 0; i < result.size(); i++) {
            Centroid centroid = result.get(i);
            System.out.println("Obiektów w klastrze " + i + ": " + centroid.markers.size());
            double averageDistanceToCenter = 0;
            for (GeoPoint point : centroid.markers) {
                averageDistanceToCenter += point.distanceToAsDouble(centroid.position);
            }
            averageDistanceToCenter = averageDistanceToCenter / centroid.markers.size();
            System.out.println("Srednia odległosc do centrum: " + averageDistanceToCenter);
        }

    }

    @Test
    public void clusteringPCM() {
        initMarkers();
        CMeans possibilisticCMeans = new PossibilisticCMeans(days, 0.0001, 2, markers);
        long startTime = System.nanoTime();
        ArrayList<Centroid> result = possibilisticCMeans.calculate();
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1000000d;
        System.out.println("Elapsed time " + totalTime);

        possibilisticCMeans.generateClusters();

        for (int i = 0; i < result.size(); i++) {
            Centroid centroid = result.get(i);
            System.out.println("Obiektów w klastrze " + i + ": " + centroid.markers.size());
            double averageDistanceToCenter = 0;
            for (GeoPoint point : centroid.markers) {
                averageDistanceToCenter += point.distanceToAsDouble(centroid.position);
            }
            averageDistanceToCenter = averageDistanceToCenter / centroid.markers.size();
            System.out.println("Srednia odległosc do centrum: " + averageDistanceToCenter);
        }
    }

    @Test
    public void tspRnn() {
        initMarkers();
        System.out.println(markers.size());
        CMeans hardCMeans = new FuzzyCMeans(days, 0.0001, 2, markers);
        ArrayList<Centroid> result = hardCMeans.calculate();
        hardCMeans.generateClusters();

        for (Centroid centroid : result
        ) {
            long startTime;
            long endTime;
            double totalTime;

            System.out.println("Ilość punktów: " + centroid.markers.size());

            System.out.println("RNN");
            startTime = System.nanoTime();
            RNN(centroid.markers);
            endTime = System.nanoTime();
            totalTime = (endTime - startTime) / 1000000d;
            System.out.println("Elapsed time: " + totalTime);

            System.out.println("ThreeOpt");
            startTime = System.nanoTime();
            ThreeOpt(centroid.markers);
            endTime = System.nanoTime();
            totalTime = (endTime - startTime) / 1000000d;
            System.out.println("Elapsed time: " + totalTime);

            System.out.println("HeldKarpAlgorithm");
            startTime = System.nanoTime();
            HeldKarpAlgorithm(centroid.markers);
            endTime = System.nanoTime();
            totalTime = (endTime - startTime) / 1000000d;
            System.out.println("Elapsed time: " + totalTime);
        }
    }

    @Test
    public void tsp3opt() {
        initMarkers();
        System.out.println(markers.size());
        CMeans hardCMeans = new FuzzyCMeans(days, 0.0001, 2, markers);
        ArrayList<Centroid> result = hardCMeans.calculate();
        hardCMeans.generateClusters();

        for (Centroid centroid : result
        ) {
            System.out.println("Ilość punktów: " + centroid.markers.size());
            long startTime = System.nanoTime();
            ThreeOpt(centroid.markers);
            long endTime = System.nanoTime();
            double totalTime = (endTime - startTime) / 1000000d;
            System.out.println("Elapsed time: " + totalTime);
        }
    }

    @Test
    public void heldKarpTest() {
        initMarkers();
        System.out.println(markers.size());
        CMeans hardCMeans = new FuzzyCMeans(days, 0.0001, 2, markers);
        ArrayList<Centroid> result = hardCMeans.calculate();
        hardCMeans.generateClusters();

        for (Centroid centroid : result
        ) {
            System.out.println("Ilość punktów: " + centroid.markers.size());
            long startTime = System.nanoTime();
            HeldKarpAlgorithm(centroid.markers);
            long endTime = System.nanoTime();
            double totalTime = (endTime - startTime) / 1000000d;
            System.out.println("Elapsed time: " + totalTime);
        }
    }


    private void initMarkers() {
        markers = new ArrayList<>();
        markers.add(new GeoPoint(51.760420, 19.407577));
        markers.add(new GeoPoint(51.757175, 19.425164));
        markers.add(new GeoPoint(51.763931, 19.412367));
        markers.add(new GeoPoint(51.766401, 19.415226));
        markers.add(new GeoPoint(51.764568, 19.421320));
        markers.add(new GeoPoint(51.776187, 19.437574));
        markers.add(new GeoPoint(51.778947, 19.446647));
        markers.add(new GeoPoint(51.780009, 19.447795));
        markers.add(new GeoPoint(51.780699, 19.447065));
        markers.add(new GeoPoint(51.778781, 19.451303));
        markers.add(new GeoPoint(51.780732, 19.456903));
        markers.add(new GeoPoint(51.776776, 19.454704));
        markers.add(new GeoPoint(51.776285, 19.455155));
        markers.add(new GeoPoint(51.775953, 19.454812));
        markers.add(new GeoPoint(51.774771, 19.455091));
        markers.add(new GeoPoint(51.772879, 19.455788));
        markers.add(new GeoPoint(51.770644, 19.464006));
        markers.add(new GeoPoint(51.769628, 19.465669));
        markers.add(new GeoPoint(51.768054, 19.469961));
        markers.add(new GeoPoint(51.779788, 19.463809));
        /*markers.add(new GeoPoint(51.780419, 19.468186));
        markers.add(new GeoPoint(51.782437, 19.469205));
        markers.add(new GeoPoint(51.785496, 19.474616));
        markers.add(new GeoPoint(51.785290, 19.470764));
        markers.add(new GeoPoint(51.792483, 19.471519));
        markers.add(new GeoPoint(51.763779, 19.457791));
        markers.add(new GeoPoint(51.759068, 19.458263));
        markers.add(new GeoPoint(51.745045, 19.462705));
        markers.add(new GeoPoint(51.759743, 19.474864));
        markers.add(new GeoPoint(51.760533, 19.479864));
        markers.add(new GeoPoint(51.754974, 19.482192));
        markers.add(new GeoPoint(51.754368, 19.444021));*/
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
        System.out.println("---SOLUTION---");
        System.out.println(order + ";");
        System.out.println("Uzyskany dystans: " + prevDistance);
        prevDistance += order.get(0).distanceToAsDouble(order.get(order.size() - 1));
        System.out.println("Uzyskany dystans 2.0: " + prevDistance);
        return order;
    }

    private ArrayList<GeoPoint> groupOpt;
    private ArrayList<GeoPoint> newGroupOpt;

    private ArrayList<GeoPoint> ThreeOpt(ArrayList<GeoPoint> group) {
        this.groupOpt = group;
        newGroupOpt = new ArrayList<>();
        int size = groupOpt.size();

        for (int i = 0; i < size; i++) {
            newGroupOpt.add(i, groupOpt.get(i));
        }

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
                            improve = 0;

                            for (int q = 0; q < size; q++) {
                                groupOpt.set(q, newGroupOpt.get(q));
                            }

                            best_distance = new_distance;
                        }
                    }
                }
            }

            improve++;
        }
        System.out.println("---SOLUTION---");
        System.out.println(groupOpt + ";");
        System.out.println("Uzyskany dystans: " + routeDistance(groupOpt));
        double prevDistance = routeDistance(groupOpt) + groupOpt.get(0).distanceToAsDouble(groupOpt.get(groupOpt.size() - 1));
        System.out.println("Uzyskany dystans 2.0: " + prevDistance);
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

    private double routeDistance(ArrayList<GeoPoint> group) {
        double result = 0;
        for (int i = 0; i < group.size() - 1; i++) {
            result += group.get(i).distanceToAsDouble(group.get(i + 1));
        }
        return result;
    }

    private ArrayList<GeoPoint> HeldKarpAlgorithm(ArrayList<GeoPoint> points) {
        int size = points.size();
        double[][] distanceMatrix = new double[size][size];
        for (GeoPoint marker :
                points) {
            for (GeoPoint marker2 :
                    points) {
                distanceMatrix[points.indexOf(marker)][points.indexOf(marker2)] = marker.distanceToAsDouble(marker2);
            }
        }

        HeldKarpDouble test = new HeldKarpDouble(distanceMatrix, 0);
        List<Integer> solution = test.calculateHeldKarp();

        ArrayList<GeoPoint> resultSet = new ArrayList<>();

        for (int i = 0; i < solution.size() - 1; i++) {
            resultSet.add(points.get(solution.get(i)));
        }

        double distance = 0;
        for (int i = 1; i < resultSet.size(); i++) {
            distance += resultSet.get(i).distanceToAsDouble(resultSet.get(i - 1));
        }
        System.out.println("---SOLUTION---");
        System.out.println(resultSet + ";");
        //System.out.println(solution + ";");
        System.out.println("Uzyskany dystans: " + distance);
        double prevDistance = distance + resultSet.get(0).distanceToAsDouble(resultSet.get(resultSet.size() - 1));
        System.out.println("Uzyskany dystans 2.0: " + prevDistance);

        return resultSet;
    }
}