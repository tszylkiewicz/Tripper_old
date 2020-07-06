package com.example.tripper.algorithm;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public abstract class CMeans {

    public int n;               //markers size
    int c;                      //centroids size
    double epsilon;             //stop condition
    double m;                   //degree of fuzziness
    int maxIteration;           //max iteration

    Vector<Vector<Double>> u0;
    Vector<Vector<Double>> u1;

    ArrayList<GeoPoint> markers;
    ArrayList<Centroid> centroids;

    CMeans(int c, double epsilon, double m, ArrayList<GeoPoint> markers) {
        this.n = markers.size();
        this.c = c;
        this.epsilon = epsilon;
        this.m = m;
        this.maxIteration = 10000;
        this.markers = markers;

        this.u0 = new Vector<>();
        this.u1 = new Vector<>();
        this.centroids = new ArrayList<>();
    }

    public abstract ArrayList<Centroid> calculate();

    void initializeCentroids() {
        int temp = n / c;
        for (int i = 0; i < c; i++) {
            centroids.add(new Centroid(markers.get(i * temp)));
        }
    }

    public void initializeMatrix() {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Vector<Double> r = new Vector<>();
            double sum = 0;
            for (int k = 0; k < c; k++) {
                r.add(rand.nextDouble());
                sum += r.get(k);
            }
            for (int k = 0; k < c; k++) {
                r.set(k, r.get(k) / sum * 1d);
            }
            u1.add(r);
        }
    }

    double matrixDifference() {
        double result = 0;
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < c; k++) {
                result += Math.abs(u0.get(i).get(k) - u1.get(i).get(k));
            }
        }
        return result;
    }

    Vector<Vector<Double>> deepCopy(Vector<Vector<Double>> oldObj) throws Exception {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj); // C
            oos.flush(); // D
            ByteArrayInputStream bin = new ByteArrayInputStream(
                    bos.toByteArray()); // E
            ois = new ObjectInputStream(bin); // F
            // return the new object
            return (Vector<Vector<Double>>) ois.readObject(); // G
        } catch (Exception e) {
            System.out.println("Exception in ObjectCloner = " + e);
            throw (e);
        } finally {
            oos.close();
            ois.close();
        }
    }

    public void generateClusters() {
        for (int i = 0; i < n; i++) {
            int chosen = 0;
            for (int j = 0; j < c; j++) {
                if (u1.get(i).get(j) > u1.get(i).get(chosen)) {
                    chosen = j;
                }
            }
            centroids.get(chosen).markers.add(markers.get(i));
        }
    }
}
