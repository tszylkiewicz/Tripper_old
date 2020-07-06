package com.example.tripper.repository;

import com.example.tripper.model.Model;

public abstract class ModelRepository<T extends Model> implements Repository<T> {

    public ApiService api = com.example.tripper.repository.HttpClient.getApiService();
    
}
