package com.example.tripper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class MapFragmentPresenter implements MapFragmentContract.Presenter {

    public static int days;
    public static int type;
    public static int max_distance;

    public ArrayList<Marker> markers;
    ArrayList<Polyline> roadsOnMap;


    public RoadManager roadManager;
    public Road[] roads;
    public MapView mapView;
    public MapFragmentContract.View view;

    public MapFragmentPresenter(MapFragmentContract.View view, Context context) {
        this.view = view;
        roadManager = new OSRMRoadManager(context);
        this.view.defaultSettings();
        markers = new ArrayList<>();
        roadsOnMap = new ArrayList<>();
        days = 1;
        type = 0;
    }

    @Override
    public void clearMap() {
        this.view.removeAllMarkers(markers);
        this.view.removeAllRoads(roadsOnMap);
        if (!markers.isEmpty()) {
            markers.clear();
        }
        if (!roadsOnMap.isEmpty()) {
            roadsOnMap.clear();
        }
    }

    @Override
    public void removeMarker(Marker marker) {
        this.view.removeMarker(marker);
        markers.remove(marker);
    }

    @Override
    public void addMarker(GeoPoint geoPoint, MapView map, Drawable icon) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setTitle("Element");
        if (icon != null) {
            marker.setIcon(icon);
        }
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            removeMarker(marker1);
            return false;
        });
        markers.add(marker);
        this.view.addMarker(marker);
        this.mapView = map;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void calculateRoad() {
        /*int equal = markers.size() / days;
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        this.view.removeAllRoads(roadsOnMap);
        roadsOnMap.clear();
        for (int i = 0; i < days; i++) {
            waypoints.clear();
            for (int j = i * equal; j < (i + 1) * equal; j++) {
                waypoints.add(markers.get(j).getPosition());
            }
            roads = roadManager.getRoads(waypoints);

            for (Road singleRoad : roads
            ) {
                if (singleRoad.mStatus != Road.STATUS_OK) {
                    Log.d("Road Status", "" + singleRoad.mStatus);
                } else {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                    roadOverlay.setColor(Color.GREEN);
                    roadOverlay.setWidth(7);
                    roadsOnMap.add(roadOverlay);
                }
            }
        }

        this.view.drawRoads(roadsOnMap);*/
        this.view.removeAllRoads(roadsOnMap);
        roadsOnMap.clear();
        //kMeansAlgorithm();

        //fuzzyCMeans();
        possiblisticCMeans();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void kMeansAlgorithm() {
        int k = days;
        int temp = markers.size() / days;
        ArrayList<Centroid> centroids = new ArrayList<>();
        Map<GeoPoint, ArrayList<Marker>> test = new HashMap<>();
        for (int i = 0; i < k; i++) {
            centroids.add(new Centroid(markers.get(i * temp).getPosition()));
            //System.out.println(markers.get(i * temp).getPosition());
        }
        Random rnd = new Random();

        for (int i = 0; i < 1000; i++) {
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
                            roadsOnMap.add(roadOverlay);
                        }
                    }
                    this.view.drawRoads(roadsOnMap);
                }
            }
        }
    }


    public void fuzzyCMeans() {
        int d = days;
        int m = 2;
        Random rand = new Random();
        Vector<Vector<Double>> membershipMatrix = new Vector<>();
        DecimalFormat df2 = new DecimalFormat("#.##");

        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = new Vector<>();
            double sum = 0;
            for (int j = 0; j < d; j++) {
                r.add(rand.nextDouble());
                sum += r.get(j);
            }
            for (int j = 0; j < d; j++) {
                r.set(j, r.get(j) / sum * 1d);
            }
            membershipMatrix.add(r);
        }

        /*System.out.println("---Initial Markers---");
        for (int i = 0; i < markers.size(); i++) {
            System.out.println(markers.get(i).getPosition());
        }*/
        System.out.println("---Initial Matrix---");
        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = membershipMatrix.get(i);
            for (int j = 0; j < d; j++) {
                System.out.print(df2.format(r.get(j)) + ", ");
            }
            System.out.println();
        }

        int temp = markers.size() / days;
        ArrayList<Centroid> centroids = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            centroids.add(new Centroid(markers.get(i * temp).getPosition()));
        }

        for (int test = 0; test < 1000; test++) {
            for (int j = 0; j < d; j++) {
                double licznik1 = 0;
                double licznik2 = 0;
                double mianownik = 0;
                for (int i = 0; i < markers.size(); i++) {
                    licznik1 += Math.pow(membershipMatrix.get(i).get(j), m) * markers.get(i).getPosition().getLatitude();
                    licznik2 += Math.pow(membershipMatrix.get(i).get(j), m) * markers.get(i).getPosition().getLongitude();
                    mianownik += Math.pow(membershipMatrix.get(i).get(j), m);
                }
                centroids.get(j).position = new GeoPoint(licznik1 / mianownik, licznik2 / mianownik);
                //System.out.println(centroids.get(j).position);
            }

            for (int i = 0; i < markers.size(); i++) {
                for (int j = 0; j < d; j++) {
                    double mian = 0;
                    double distance = markers.get(i).getPosition().distanceToAsDouble(centroids.get(j).position);
                    for (int k = 0; k < d; k++) {
                        //mian += Math.pow(membershipMatrix.get(i).get(j) / membershipMatrix.get(i).get(k), 2d / (m - 1d));
                        mian += distance / markers.get(i).getPosition().distanceToAsDouble(centroids.get(k).position);
                        //mian += Math.pow(distance / markers.get(i).getPosition().distanceToAsDouble(centroids.get(k).position), 2/ (m - 1));
                    }
                    mian = Math.pow(mian, 2);
                    mian = Math.pow(mian, 1 / (m - 1));
                    //mian = Math.pow(mian, 1 / (m - 1));
                    membershipMatrix.get(i).set(j, 1 / mian);
                }
            }
        }

        System.out.println("---Last---");
        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = membershipMatrix.get(i);
            for (int j = 0; j < d; j++) {
                System.out.print(r.get(j) + ", ");
            }
            System.out.println();
        }

        for (int i = 0; i < centroids.size(); i++) {
            addMarker(centroids.get(i).position, this.mapView, null);
        }
    }

    public void possiblisticCMeans() {
        int d = days;
        int m = 2;
        Random rand = new Random();
        Vector<Vector<Double>> membershipMatrix = new Vector<>();
        DecimalFormat df2 = new DecimalFormat("#.##");

        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = new Vector<>();
            for (int j = 0; j < d; j++) {
                r.add(rand.nextDouble());
            }
            membershipMatrix.add(r);
        }

        /*System.out.println("---Initial Markers---");
        for (int i = 0; i < markers.size(); i++) {
            System.out.println(markers.get(i).getPosition());
        }*/
        System.out.println("---Initial Matrix---");
        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = membershipMatrix.get(i);
            for (int j = 0; j < d; j++) {
                System.out.print(df2.format(r.get(j)) + ", ");
            }
            System.out.println();
        }

        int temp = markers.size() / days;
        ArrayList<Centroid> centroids = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            centroids.add(new Centroid(markers.get(i * temp).getPosition()));
        }

        for (int test = 0; test < 1000; test++) {
            for (int j = 0; j < d; j++) {
                double licznik1 = 0;
                double licznik2 = 0;
                double mianownik = 0;
                for (int i = 0; i < markers.size(); i++) {
                    licznik1 += Math.pow(membershipMatrix.get(i).get(j), m) * markers.get(i).getPosition().getLatitude();
                    licznik2 += Math.pow(membershipMatrix.get(i).get(j), m) * markers.get(i).getPosition().getLongitude();
                    mianownik += Math.pow(membershipMatrix.get(i).get(j), m);
                }
                centroids.get(j).position = new GeoPoint(licznik1 / mianownik, licznik2 / mianownik);
                System.out.println(centroids.get(j).position);
            }

            for (int i = 0; i < markers.size(); i++) {
                for (int j = 0; j < d; j++) {
                    double njlicznik = 0;
                    double njmian = 0;
                    for (int k = 0; k < markers.size(); k++) {
                        njlicznik += Math.pow(membershipMatrix.get(k).get(j), m) * Math.pow(centroids.get(j).position.distanceToAsDouble(markers.get(k).getPosition()), 2);
                        njmian += Math.pow(membershipMatrix.get(k).get(j), m);
                    }
                    double nj = njlicznik / njmian;
                    double mian;
                    double distance = markers.get(i).getPosition().distanceToAsDouble(centroids.get(j).position);
                    //mian = Math.pow(distance, 2)/nj;
                    //mian = Math.pow(distance / nj, 2);

                    //mian = Math.pow(distance, 2);
                    //mian = Math.pow(mian, 1 / (m - 1));
                    //mian = mian / nj;

                    mian = Math.pow(distance, 2);
                    mian = mian / nj;

                    mian = Math.pow(mian, 1 / m - 1) + 1;

                    membershipMatrix.get(i).set(j, 1 / mian);
                }
            }
        }

        System.out.println("---Last---");
        for (int i = 0; i < markers.size(); i++) {
            Vector<Double> r = membershipMatrix.get(i);
            for (int j = 0; j < d; j++) {
                System.out.print(r.get(j) + ", ");
            }
            System.out.println();
        }

        for (int i = 0; i < centroids.size(); i++) {
            addMarker(centroids.get(i).position, this.mapView, null);
        }
    }
}
