package com.example.tripper;

public class MapFragmentPresenter implements MapFragmentContract.Presenter {

    public static int days;
    public static int type;
    public MapFragmentContract.View view;

    public MapFragmentPresenter(MapFragmentContract.View view) {
        this.view = view;
    }
}
