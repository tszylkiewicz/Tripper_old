package com.example.tripper.model;

import com.example.tripper.model.CMeans;
import com.example.tripper.model.Centroid;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class FuzzyCMeans extends CMeans {

    public FuzzyCMeans(int c, double epsilon, double m, ArrayList<Marker> markers) {
        super(c, epsilon, m, markers);
    }

    @Override
    public ArrayList<Centroid> calculate() {

        this.initializeCentroids();
        this.initializeMatrix();

        System.out.println("---Initial Matrix---");
        printMatrix();

        for (int t = 0; t < maxIteration; t++) {

            try {
                u0 = deepCopy(u1);
            } catch (Exception ex) {
                System.out.println("Błąd kopiowania" + ex);
            }

            for (int i = 0; i < c; i++) {
                double numeratorLat = 0;
                double numeratorLon = 0;
                double denominator = 0;
                for (int j = 0; j < n; j++) {
                    numeratorLat += Math.pow(u1.get(j).get(i), m) * markers.get(j).getPosition().getLatitude();
                    numeratorLon += Math.pow(u1.get(j).get(i), m) * markers.get(j).getPosition().getLongitude();
                    denominator += Math.pow(u1.get(j).get(i), m);
                }
                centroids.get(i).position = new GeoPoint(numeratorLat / denominator, numeratorLon / denominator);
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < c; j++) {
                    double result = 0;
                    double numerator = markers.get(i).getPosition().distanceToAsDouble(centroids.get(j).position);
                    for (int k = 0; k < c; k++) {
                        double temp = Math.pow(numerator / markers.get(i).getPosition().distanceToAsDouble(centroids.get(k).position), 2);
                        result += Math.pow(temp, 1 / (m - 1));
                    }
                    u1.get(i).set(j, 1 / result);
                }
            }

            if (matrixDifference() <= epsilon) {
                break;
            }
        }

        System.out.println("---Exit Matrix---");
        printMatrix();

        return centroids;
    }

}
