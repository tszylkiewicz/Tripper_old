package com.example.tripper.repository;

import com.example.tripper.model.Point;
import com.google.gson.JsonObject;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class PointRepository extends ModelRepository<Point> {

    public Single<List<Point>> addPoints(int tripId, ArrayList<GeoPoint> points) {

        List<JsonObject> request = new ArrayList<>();

        for (GeoPoint point : points) {
            JsonObject pointRequest = new JsonObject();
            pointRequest.addProperty("trip_id", tripId);
            pointRequest.addProperty("latitude", point.getLatitude());
            pointRequest.addProperty("longitude", point.getLongitude());
            pointRequest.addProperty("altitude", point.getAltitude());
            request.add(pointRequest);
        }
        return api.addPoints(request);
    }

    public Single<List<Point>> getAllTripPoints(int tripId) {
        return api.getAllTripPoints(tripId);
    }

    @Override
    public Single<Point> create(Point modelObject) {
        return api.create(modelObject);
    }

    @Override
    public Single<Point> read(int id) {
        return api.readPoint(id);
    }

    @Override
    public Single<Point> update(Point modelObject) {
        return api.update(modelObject);
    }

    @Override
    public boolean delete(int id) {
        return api.deletePoint(id);
    }
}
