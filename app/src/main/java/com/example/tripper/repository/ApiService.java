package com.example.tripper.repository;

import com.example.tripper.model.Point;
import com.example.tripper.model.Trip;
import com.example.tripper.model.User;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

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

    @POST("user")
    Single<User> create(@Body User user);

    @GET("user/{id}")
    Single<User> readUser(@Path("id") int userId);

    @PUT("user")
    Single<User> update(@Body User user);

    @DELETE("user/{id}")
    boolean deleteUser(@Path("id") int userId);

    // =============================================================================================
    // Point endpoints
    // =============================================================================================
    @POST("point")
    Single<List<Point>> addPoints(@Body List<JsonObject> request);

    @GET("point/trip/{id}")
    Single<List<Point>> getAllTripPoints(@Path("id") int tripId);

    @POST("point")
    Single<Point> create(@Body Point point);

    @GET("point/{id}")
    Single<Point> readPoint(@Path("id") int pointId);

    @PUT("point")
    Single<Point> update(@Body Point point);

    @DELETE("point/{id}")
    boolean deletePoint(@Path("id") int pointId);

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

    @POST("trip")
    Single<Trip> create(@Body Trip trip);

    @GET("trip/{id}")
    Single<Trip> readTrip(@Path("id") int tripId);

    @PUT("trip")
    Single<Trip> update(@Body Trip trip);

    @DELETE("trip/{id}")
    boolean deleteTrip(@Path("id") int tripId);
}
