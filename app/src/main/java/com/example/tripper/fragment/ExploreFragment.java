package com.example.tripper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.TripAdapter;
import com.example.tripper.model.Trip;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;
    private MapViewModel mapViewModel;

    private ListView listView;

    ArrayList<Trip> dataModels;
    private static TripAdapter adapter;

    private NavController navController;

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

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
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.trips_list);
        navController = Navigation.findNavController(view);

        MainActivity.getDisposables().add(tripViewModel.getAllPublicTrips().subscribe(user1 -> {
                    System.out.println("POBRANO WSZYSTKIE WYCIECZKI");
                    displayTrips(user1);
                }, Throwable::printStackTrace)
        );

        //displayTrips(dataModels);
    }

    private void displayTrips(List<Trip> user1) {

        dataModels = new ArrayList<>();
        dataModels.addAll(user1);

        adapter = new TripAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trip trip = dataModels.get(position);

                MainActivity.getDisposables().add(tripViewModel.getAllTripPoints(trip.getId()).subscribe(user1 -> {
                    System.out.println("POBRANO WSZYSTKIE PUNKTY WYCIECZKI");
                    mapViewModel.setCurrentPoints(user1);
                    navController.navigate(R.id.nav_map);
                }, Throwable::printStackTrace));

            }
        });
    }

}
