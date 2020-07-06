package com.example.tripper.algorithm;

import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class PossibilisticCMeans extends CMeans {

    public PossibilisticCMeans(int c, double epsilon, double m, ArrayList<GeoPoint> markers) {
        super(c, epsilon, m, markers);
    }

    @Override
    public ArrayList<Centroid> calculate() {
        this.initializeCentroids();
        this.initializeMatrix();

        for (int t = 0; t < maxIteration; t++) {

            try {
                u0 = deepCopy(u1);
            } catch (Exception ex) {
                Log.e("cmeans", "Błąd kopiowania" + ex);
            }

            for (int i = 0; i < c; i++) {
                double numeratorLat = 0;
                double numeratorLon = 0;
                double denominator = 0;
                for (int j = 0; j < n; j++) {
                    numeratorLat += Math.pow(u1.get(j).get(i), m) * markers.get(j).getLatitude();
                    numeratorLon += Math.pow(u1.get(j).get(i), m) * markers.get(j).getLongitude();
                    denominator += Math.pow(u1.get(j).get(i), m);
                }
                centroids.get(i).position = new GeoPoint(numeratorLat / denominator, numeratorLon / denominator);
            }

            for (int i = 0; i < c; i++) {
                double numerator = 0;
                double denominator = 0;
                double ni;
                for (int j = 0; j < n; j++) {
                    numerator += Math.pow(u1.get(j).get(i), m) * Math.pow(centroids.get(i).position.distanceToAsDouble(markers.get(j)), 2);
                    denominator += Math.pow(u1.get(j).get(i), m);
                }

                ni = numerator / denominator;

                for (int j = 0; j < n; j++) {
                    double result = centroids.get(i).position.distanceToAsDouble(markers.get(j));

                    //Analiza skupień - Wierzchoń, Kłopotek
                    result = Math.pow(result, 2);
                    result = result / ni;
                    result = 1d + result;
                    double test = 1 / (m - 1);
                    result = Math.pow(result, test);
                    u1.get(j).set(i, result);

                    //Metody sztucznej inteligencji - Rutkowski
                    /*result = result / ni;
                    double test = 2 / (m - 1);
                    result = Math.pow(result, test);
                    result = 1 + result;
                    result = 1 / result;
                    u1.get(j).set(i, result);*/

                    //Najczęściej w artykułach się pojawia ten wzór ale nie działa
                    /*result = Math.pow(result, 2);
                    result = result / ni;
                    double test = 1d / (m - 1d);
                    result = Math.pow(result, test);
                    result = 1d + result;
                    result = 1d / result;
                    u1.get(j).set(i, result);*/
                }
            }

            if (matrixDifference() <= epsilon) {
                break;
            }
        }

        return centroids;
    }
}
