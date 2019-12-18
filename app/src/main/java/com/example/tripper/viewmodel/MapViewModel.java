package com.example.tripper.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tripper.model.enums.TransportType;

public class MapViewModel extends ViewModel {

    private int days;
    private TransportType transportType;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }
}
