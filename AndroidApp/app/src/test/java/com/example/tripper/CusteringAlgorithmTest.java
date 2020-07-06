package com.example.tripper;

import com.example.tripper.algorithm.CMeans;
import com.example.tripper.algorithm.Centroid;
import com.example.tripper.algorithm.FuzzyCMeans;
import com.example.tripper.algorithm.HardCMeans;
import com.example.tripper.algorithm.PossibilisticCMeans;

import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CusteringAlgorithmTest {

    private int days = 4;

    private ArrayList<GeoPoint> markers;

    @Test
    public void clusteringHCMTest() {
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
    public void clusteringFCMTest() {
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
    public void clusteringPCMTest() {
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
    }
}