package com.example.tripper.repository;

import com.example.tripper.model.User;
import com.google.gson.JsonObject;

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
}
