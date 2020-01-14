package com.example.tripper.repository;

import com.example.tripper.model.Trip;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Single;

public class TripRepository {

    private ApiService api = HttpClient.getApiService();

    public Single<Trip> createTrip(int userId, String name, String description, double distance, String transportType, int shared) {
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

    public Single<List<Trip>> getAllUserTrips(int userId) {
        return api.getAllUserTrips(userId);
    }

    public Single<List<Trip>> getAllPublicTrips() {
        return api.getAllPublicTrips();
    }

    public Single<Trip> update(Trip trip) {
        return api.updateTrip(trip.getId(), trip);
    }
}
