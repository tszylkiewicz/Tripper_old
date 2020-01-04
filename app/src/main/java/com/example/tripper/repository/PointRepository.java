package com.example.tripper.repository;

import com.example.tripper.model.Point;
import com.example.tripper.model.User;
import com.google.gson.JsonObject;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;

public class PointRepository {

    private ApiService api = HttpClient.getApiService();

    public Single<List<Point>> addPoints(ArrayList<GeoPoint> points) {

        List<JsonObject> request = new ArrayList<>();

        for (GeoPoint point : points) {
            JsonObject pointRequest = new JsonObject();
            pointRequest.addProperty("latitude", point.getLatitude());
            pointRequest.addProperty("longitude", point.getLongitude());
            pointRequest.addProperty("altitude", point.getAltitude());
            request.add(pointRequest);
        }
        return api.addPoints(request);
    }
}
