package com.example.tripper.repository;

import com.example.tripper.model.Point;
import com.example.tripper.model.Trip;
import com.example.tripper.model.TripPoint;
import com.example.tripper.model.User;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Flowable;
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

    // =============================================================================================
    // Trip endpoints
    // =============================================================================================
    @POST("trip")
    Single<Trip> createTrip(@Body JsonObject request);

    // =============================================================================================
    // Trip point endpoints
    // =============================================================================================
    @POST("trip-point")
    Single<List<TripPoint>> addTripPoints(@Body List<JsonObject> request);
}
