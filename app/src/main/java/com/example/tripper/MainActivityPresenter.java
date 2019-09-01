package com.example.tripper;

public class MainActivityPresenter implements MainActivityContract.Presenter {
    private MainActivityContract.View view;

    public MainActivityPresenter(MainActivityContract.View view) {
        this.view = view;
    }

}
