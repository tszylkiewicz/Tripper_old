package com.example.tripper;

public class PropertiesActivityPresenter implements PropertiesActivityContract.Presenter {

    public PropertiesActivityContract.View view;

    public PropertiesActivityPresenter(PropertiesActivityContract.View view) {
        this.view = view;
    }

}
