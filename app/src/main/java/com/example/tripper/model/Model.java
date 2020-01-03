package com.example.tripper.model;

public abstract class Model {

    private int id;

    Model(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
