package com.example.tripper.repository;

import com.example.tripper.model.User;
import com.google.gson.JsonObject;

import io.reactivex.Single;

public class UserRepository extends ModelRepository<User> {

    public Single<User> signUp(String email, String username, String password) {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("username", username);
        request.addProperty("password", password);
        return api.signUp(request);
    }

    public Single<User> signIn(String email, String password) {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("password", password);
        return api.signIn(request);
    }

    public Single<User> updateUser(User user) {
        return api.updateUser(user.getId(), user);

    }

    @Override
    public Single<User> create(User modelObject) {
        return api.create(modelObject);
    }

    @Override
    public Single<User> read(int id) {
        return api.readUser(id);
    }

    @Override
    public Single<User> update(User modelObject) {
        return api.update(modelObject);
    }

    @Override
    public boolean delete(int id) {
        return api.deleteUser(id);
    }
}
