package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.tripper.MapFragmentPresenter;
import com.example.tripper.R;
import com.example.tripper.model.enums.TransportType;
import com.example.tripper.viewmodel.MapViewModel;

public class MapSettingsFragment extends Fragment {

    private MapViewModel mapViewModel;

    private NumberPicker days;
    private Spinner transportType;


    public static MapSettingsFragment newInstance() {
        return new MapSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
        transportType = view.findViewById(R.id.transportType);
        days = view.findViewById(R.id.days);

        if (getActivity() == null) {
            System.out.println("nie ma activity");
        }
        transportType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TransportType.values()));

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> Log.d("Properties", "selected number " + numberPicker.getValue());

        days.setMinValue(1);
        days.setMaxValue(30);
        days.setOnValueChangedListener(onValueChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewModel.setDays(days.getValue());
        mapViewModel.setTransportType((TransportType) transportType.getSelectedItem());
        MapFragmentPresenter.days = days.getValue();
    }
}
