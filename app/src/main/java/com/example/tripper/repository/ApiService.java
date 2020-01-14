package com.example.tripper.repository;

import com.example.tripper.model.Point;
import com.example.tripper.model.Trip;
import com.example.tripper.model.User;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("user/1")
    Single<User> getFirstUser();

    // =============================================================================================
    // Authentication endpoints
    // =============================================================================================
    @POST("login")
    Single<User> signIn(@Body JsonObject request);

    @POST("register")
    Single<User> signUp(@Body JsonObject request);

    // =============================================================================================
    // User endpoints
    // =============================================================================================
    @PUT("user/{id}")
    Single<User> updateUser(@Path("id") int userId, @Body User user);

    // =============================================================================================
    // Point endpoints
    // =============================================================================================
    @POST("point")
    Single<List<Point>> addPoints(@Body List<JsonObject> request);

    @GET("point/trip/{id}")
    Single<List<Point>> getAllTripPoints(@Path("id") int tripId);

    // =============================================================================================
    // Trip endpoints
    // =============================================================================================
    @POST("trip")
    Single<Trip> createTrip(@Body JsonObject request);

    @GET("trip/user/{id}")
    Single<List<Trip>> getAllUserTrips(@Path("id") int userId);

    @GET("trip/public")
    Single<List<Trip>> getAllPublicTrips();

    @PUT("trip/{id}")
    Single<Trip> updateTrip(@Path("id") int tripId, @Body Trip trip);
}
