package com.example.tripper.repository;

import com.example.tripper.model.Model;

import io.reactivex.Single;

public interface Repository<T extends Model> {
    Single<T> create(T modelObject);

    Single<T> read(int id);

    Single<T> update(T modelObject);

    boolean delete(int id);
}
