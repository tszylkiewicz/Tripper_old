package com.example.tripper.viewmodel;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.example.tripper.MainActivity;
import com.example.tripper.model.Point;
import com.example.tripper.model.Trip;
import com.example.tripper.repository.PointRepository;
import com.example.tripper.repository.TripRepository;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TripViewModel extends ViewModel {

    private HashMap<Polyline, ArrayList<Marker>> createdRoutes;

    private PointRepository pointRepository = new PointRepository();
    private TripRepository tripRepository = new TripRepository();

    public void savePoints(Polyline polyline) {

        if (createdRoutes != null) {
            ArrayList<Marker> points = createdRoutes.get(polyline);
            System.out.println("MARKER COUNT: " + points.size());

            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            for (Marker point : points
            ) {
                geoPoints.add(point.getPosition());
                System.out.println(point.getPosition());
            }
            System.out.println("Zapisano trasę");
            MainActivity.getDisposables().add(pointRepository.addPoints(geoPoints).observeOn(mainThread()).subscribeOn(Schedulers.io()).subscribe(user1 -> {
                System.out.println("ZAPISANO PUNKTY");
                saveTrip(user1);
            }, Throwable::printStackTrace));
        } else {
            System.out.println("Nie ma tras s TripViewModel");
        }
    }

    public void saveTrip(List<Point> points) {
        MainActivity.getDisposables().add(tripRepository.createTrip(1, "Test", "Description", 123.412, "car", false).observeOn(mainThread()).subscribeOn(Schedulers.io()).subscribe(user1 -> {
            System.out.println("ZAPISANO WYCIECZKE");
            System.out.println(user1.getAllData());
            combineTripWithPoints(user1, points);
        }, Throwable::printStackTrace));
    }

    private void combineTripWithPoints(Trip trip, List<Point> points) {
        System.out.println("COMBINE TRIP WITH POINTS");
        int tripId = trip.getId();

        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();

        for (Point point : points) {
            pairs.add(new Pair<>(tripId, point.getId()));
        }
        MainActivity.getDisposables().add(tripRepository.addTripPoints(pairs).observeOn(mainThread()).subscribeOn(Schedulers.io()).subscribe(user1 -> {
            System.out.println("ZAPISANO POLACZENIA");
        }, Throwable::printStackTrace));
    }

    public void addCreatedRoute(Polyline roadOverlay, ArrayList<Marker> markerList) {
        if (createdRoutes == null) {
            createdRoutes = new HashMap<>();
        }
        createdRoutes.put(roadOverlay, markerList);
    }
}
