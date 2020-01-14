package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.tripper.R;
import com.example.tripper.viewmodel.MapViewModel;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class MapSettingsFragment extends Fragment {

    private MapViewModel mapViewModel;

    private NumberPicker days;
    private Spinner transportType;
    private Spinner tileSource;


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
        mapViewModel = ViewModelProviders.of(requireActivity()).get(MapViewModel.class);
        mapViewModel.getDays().observe(requireActivity(), day -> {
            //System.out.println("Zmieniono z ustawienia na: " + msg);
            //if (msg != null && days != null) {
            //    days.setValue(msg);
            //}
        });
        mapViewModel.getTransportType().observe(requireActivity(), transportType -> {
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        days = view.findViewById(R.id.days);
        transportType = view.findViewById(R.id.transportType);
        tileSource = view.findViewById(R.id.tileSource);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> mapViewModel.setDays(days.getValue());

        days.setMinValue(1);
        days.setMaxValue(30);
        days.setValue(mapViewModel.getDays().getValue());
        days.setOnValueChangedListener(onValueChangeListener);

        ArrayList<String> navigationType = new ArrayList<>();
        navigationType.add("Fastest");
        navigationType.add("Shortest");
        navigationType.add("Pedestrian");
        navigationType.add("Bicycle");
        navigationType.add("Multimodal");


        transportType.setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, navigationType));
        transportType.setSelection(navigationType.indexOf(mapViewModel.getNavigationType()));


        transportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("CHOSEN NAVIGATION TYPE:" + transportType.getSelectedItem());
                mapViewModel.setNavigationType((String) transportType.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        HashMap<String, ITileSource> mapLayers = new HashMap<>();
        mapLayers.put("Base map", TileSourceFactory.MAPNIK);
        mapLayers.put("Bicycle map", TileSourceFactory.HIKEBIKEMAP);
        mapLayers.put("Map of public transport", TileSourceFactory.PUBLIC_TRANSPORT);
        mapLayers.put("Topographic map", TileSourceFactory.OpenTopo);

        tileSource.setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, mapLayers.keySet().toArray()));

        ITileSource currentTileSource = mapViewModel.getTileSource();
        if (TileSourceFactory.MAPNIK.equals(currentTileSource)) {
            tileSource.setSelection(0);
        } else if (TileSourceFactory.HIKEBIKEMAP.equals(currentTileSource)) {
            tileSource.setSelection(1);
        } else if (TileSourceFactory.PUBLIC_TRANSPORT.equals(currentTileSource)) {
            tileSource.setSelection(2);
        } else {
            tileSource.setSelection(3);
        }

        tileSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("CHOSEN SOURCE:" + tileSource.getSelectedItem());
                mapViewModel.setTileSource(mapLayers.get(tileSource.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
