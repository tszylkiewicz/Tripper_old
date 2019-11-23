package com.example.tripper.model;

import com.example.tripper.model.CMeans;
import com.example.tripper.model.Centroid;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class PossibilisticCMeans extends CMeans {

    public PossibilisticCMeans(int c, double epsilon, double m, ArrayList<Marker> markers) {
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

            for (int i = 0; i < c; i++) {
                double numerator = 0;
                double denominator = 0;
                double ni;
                for (int j = 0; j < n; j++) {
                    numerator += Math.pow(u1.get(j).get(i), m) * Math.pow(centroids.get(i).position.distanceToAsDouble(markers.get(j).getPosition()), 2);
                    denominator += Math.pow(u1.get(j).get(i), m);
                }

                ni = numerator / denominator;

                for (int j = 0; j < n; j++) {
                    double result = centroids.get(i).position.distanceToAsDouble(markers.get(j).getPosition());
                    /*result = Math.pow(result, 2);
                    result = result / ni;
                    result = Math.pow(result, 1 / m - 1);
                    result += 1;
                    u1.get(j).set(i, 1 / result);*/

                    result = result / ni;
                    result = Math.pow(result, 2);
                    result = Math.pow(result, 1 / m - 1);
                    result += 1;
                    u1.get(j).set(i, 1 / result);
                }
            }

            if (matrixDifference() <= epsilon) {
                System.out.println("Koniec po: " + t + " iteracjach.");
                break;
            }
        }
        System.out.println("Koniec po: max iteracjach.");
        System.out.println("---Exit Matrix---");
        printMatrix();

        return centroids;
    }
}
