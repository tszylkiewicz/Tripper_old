package com.example.tripper;

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
    public int c;               //centroids size
    public double epsilon;      //stop condition
    public double m;            //degree of fuzziness
    public int maxIteration;   //max iteration

    public Vector<Vector<Double>> u0;
    public Vector<Vector<Double>> u1;

    public ArrayList<Marker> markers;
    public ArrayList<Centroid> centroids;

    public CMeans(int c, double epsilon, double m, ArrayList<Marker> markers) {
        this.n = markers.size();
        this.c = c;
        this.epsilon = epsilon;
        this.m = m;
        this.maxIteration = 100000;
        this.markers = markers;

        this.u0 = new Vector<>();
        this.u1 = new Vector<>();
        this.centroids = new ArrayList<>();
    }

    public abstract ArrayList<Centroid> calculate();

    public void initializeCentroids() {
        int temp = n / c;
        for (int i = 0; i < c; i++) {
            centroids.add(new Centroid(markers.get(i * temp).getPosition()));
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

    public double matrixDifference() {
        double result = 0;
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < c; k++) {
                result += Math.abs(u0.get(i).get(k) - u1.get(i).get(k));
            }
        }
        return result;
    }

    public Vector<Vector<Double>> deepCopy(Vector<Vector<Double>> oldObj) throws Exception {
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

    public void printMatrix() {
        DecimalFormat df2 = new DecimalFormat("#.##");
        for (int i = 0; i < n; i++) {
            Vector<Double> r = u1.get(i);
            for (int k = 0; k < c; k++) {
                System.out.print(r.get(k) + ", ");
                //System.out.print(df2.format(r.get(k)) + ", ");
            }
            System.out.println();
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
