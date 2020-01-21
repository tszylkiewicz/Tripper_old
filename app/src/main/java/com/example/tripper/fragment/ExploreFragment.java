package com.example.tripper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.adapter.TripAdapter;
import com.example.tripper.model.Trip;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private TripViewModel tripViewModel;
    private MapViewModel mapViewModel;
    private NavController navController;

    private ListView listView;

    private ArrayList<Trip> dataModels;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapViewModel = ViewModelProviders.of(requireActivity()).get(MapViewModel.class);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.trips_list);
        navController = Navigation.findNavController(view);

        MainActivity.getDisposables().add(tripViewModel.getAllPublicTrips()
                .subscribe(this::displayTrips, Throwable::printStackTrace)
        );
    }

    private void displayTrips(List<Trip> user1) {

        dataModels = new ArrayList<>();
        dataModels.addAll(user1);

        TripAdapter adapter = new TripAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Trip trip = dataModels.get(position);

            MainActivity.getDisposables().add(tripViewModel.getAllTripPoints(trip.getId())
                    .subscribe(points -> {
                mapViewModel.loadCurrentPoints(points);
                navController.navigate(R.id.nav_map);
            }, Throwable::printStackTrace));
        });
    }

}
