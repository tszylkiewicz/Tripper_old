package com.example.tripper.repository;

import android.util.Pair;

import com.example.tripper.model.Trip;
import com.example.tripper.model.TripPoint;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class TripRepository {

    private ApiService api = HttpClient.getApiService();

    public Single<Trip> createTrip(int userId, String name, String description, double distance, String transportType, boolean shared) {
        JsonObject request = new JsonObject();
        request.addProperty("user_id", userId);
        request.addProperty("name", name);
        request.addProperty("description", description);
        request.addProperty("distance", distance);
        request.addProperty("transport_type", transportType);
        request.addProperty("rating", 0);
        request.addProperty("rating_count", 0);
        request.addProperty("shared", shared);
        return api.createTrip(request);
    }

    public Single<List<TripPoint>> addTripPoints(ArrayList<Pair<Integer, Integer>> pairs) {

        List<JsonObject> request = new ArrayList<>();

        for (Pair<Integer, Integer> pair : pairs) {
            JsonObject pointRequest = new JsonObject();
            pointRequest.addProperty("trip_id", pair.first);
            pointRequest.addProperty("point_id", pair.second);
            request.add(pointRequest);
        }
        return api.addTripPoints(request);
    }
}
