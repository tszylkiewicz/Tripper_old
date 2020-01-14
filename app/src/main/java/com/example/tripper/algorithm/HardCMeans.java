package com.example.tripper.algorithm;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Vector;

public class HardCMeans extends CMeans {

    public HardCMeans(int c, double epsilon, double m, ArrayList<GeoPoint> markers) {
        super(c, epsilon, m, markers);
    }

    @Override
    public ArrayList<Centroid> calculate() {

        this.initializeCentroids();
        this.initializeMatrix();

        System.out.println("---Initial Matrix---");
        //printMatrix();

        for (int t = 0; t < maxIteration; t++) {

            try {
                u0 = deepCopy(u1);
            } catch (Exception ex) {
                System.out.println("Błąd kopiowania" + ex);
            }

            for (int i = 0; i < n; i++) {
                int selected = 0;

                for (int j = 0; j < c; j++) {
                    u1.get(i).set(j, 0d);
                    if (markers.get(i).distanceToAsDouble(centroids.get(j).position) < markers.get(i).distanceToAsDouble(centroids.get(selected).position)) {
                        selected = j;
                    }
                }
                u1.get(i).set(selected, 1d);
            }

            for (int i = 0; i < c; i++) {
                double numeratorLat = 0;
                double numeratorLon = 0;
                double denominator = 0;
                for (int j = 0; j < n; j++) {
                    numeratorLat += u1.get(j).get(i) * markers.get(j).getLatitude();
                    numeratorLon += u1.get(j).get(i) * markers.get(j).getLongitude();
                    denominator += u1.get(j).get(i);
                }
                centroids.get(i).position = new GeoPoint(numeratorLat / denominator, numeratorLon / denominator);
            }


            if (matrixDifference() <= epsilon) {
                System.out.println("Iteracje: " + t);
                break;
            }
        }
        System.out.println("Iteracje: " + maxIteration);
        System.out.println("---Exit Matrix---");
        //printMatrix();

        return centroids;
    }

    @Override
    public void initializeMatrix() {
        for (int i = 0; i < n; i++) {
            Vector<Double> r = new Vector<>();
            for (int k = 0; k < c; k++) {
                r.add(0d);
            }
            u1.add(r);
        }
    }
}
