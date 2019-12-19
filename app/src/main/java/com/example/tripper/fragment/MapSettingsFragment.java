package com.example.tripper.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapViewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        mapViewModel.getDays().observe(getActivity(), days -> {
            //System.out.println("Zmieniono z ustawienia na: " + msg);
            //if (msg != null && days != null) {
            //    days.setValue(msg);
            //}
        });
        mapViewModel.getTransportType().observe(getActivity(), transportType -> {
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        days = view.findViewById(R.id.days);
        transportType = view.findViewById(R.id.transportType);

        //NumberPicker.OnValueChangeListener onValueChangeListener =
        //        (numberPicker, i, i1) -> Log.d("Properties", "selected number " + numberPicker.getValue());

        days.setMinValue(1);
        days.setMaxValue(30);
        days.setValue(mapViewModel.getDays().getValue());
        //days.setOnValueChangedListener(onValueChangeListener);

        transportType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TransportType.values()));
        transportType.setSelection(mapViewModel.getTransportType().getValue().ordinal());
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewModel.setDays(days.getValue());
        mapViewModel.setTransportType((TransportType) transportType.getSelectedItem());
    }
}
