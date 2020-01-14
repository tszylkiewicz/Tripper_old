package com.example.tripper.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tripper.MainActivity;
import com.example.tripper.model.Point;
import com.example.tripper.model.Trip;
import com.example.tripper.repository.PointRepository;
import com.example.tripper.repository.TripRepository;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TripViewModel extends ViewModel {

    private HashMap<Polyline, ArrayList<GeoPoint>> createdRoutes;

    private PointRepository pointRepository = new PointRepository();
    private TripRepository tripRepository = new TripRepository();

    private Trip currentTrip;

    private void savePoints(Polyline polyline, int tripId) {

        if (createdRoutes != null) {
            ArrayList<GeoPoint> points = createdRoutes.get(polyline);
            double distance = polyline.getDistance();
            System.out.println("MARKER COUNT: " + points.size());

            System.out.println("Zapisano trasę");
            MainActivity.getDisposables().add(pointRepository.addPoints(tripId, points).observeOn(mainThread()).subscribeOn(Schedulers.io()).subscribe(user1 -> {
                System.out.println("POINTS SAVED SUCCESSFULLY");
                //saveTrip(user1, userId, "Name", "Description", distance, "car");
            }, Throwable::printStackTrace));
        } else {
            System.out.println("Nie ma tras s TripViewModel");
        }
    }

    public void saveTrip(Polyline polyline, int userId, String name, String description, String transportType) {
        MainActivity.getDisposables().add(tripRepository.createTrip(userId, name, description, polyline.getDistance(), transportType, 0).observeOn(mainThread()).subscribeOn(Schedulers.io()).subscribe(user1 -> {
            System.out.println("TRIP SAVED SUCCESSFULLY");
            System.out.println(user1.getAllData());
            savePoints(polyline, user1.getId());
        }, Throwable::printStackTrace));
    }

    public void addCreatedRoute(Polyline roadOverlay, ArrayList<GeoPoint> markerList) {
        if (createdRoutes == null) {
            createdRoutes = new HashMap<>();
        }
        createdRoutes.put(roadOverlay, markerList);
    }

    public Single<List<Trip>> getAllUserTrips(int userId) {
        return tripRepository.getAllUserTrips(userId).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<List<Trip>> getAllPublicTrips() {
        return tripRepository.getAllPublicTrips().observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public Trip getCurrentTrip() {
        return currentTrip;
    }

    public void setCurrentTrip(Trip currentTrip) {
        this.currentTrip = currentTrip;
    }

    public Single<Trip> update(Trip trip) {
        return tripRepository.update(trip).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public HashMap<Polyline, ArrayList<GeoPoint>> getCreatedRoutes() {
        if (createdRoutes == null) {
            createdRoutes = new HashMap<>();
        }
        return createdRoutes;
    }

    public Single<List<Point>> getAllTripPoints(int tripId) {
        return pointRepository.getAllTripPoints(tripId).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

}
