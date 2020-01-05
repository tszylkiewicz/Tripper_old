package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.TripAdapter;
import com.example.tripper.model.Trip;
import com.example.tripper.model.User;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripsFragment extends Fragment {

    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;

    private ListView listView;

    ArrayList<Trip> dataModels;
    private static TripAdapter adapter;

    private NavController navController;

    public static TripsFragment newInstance() {
        return new TripsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.trips_list);

        MainActivity.getDisposables().add(tripViewModel.getAllUserTrips(userViewModel.getCurrentUser().getId()).subscribe(user1 -> {
                    System.out.println("POBRANO WSZYSTKIE WYCIECZKI");
                    displayTrips(user1);
                }, Throwable::printStackTrace)
        );

        navController = Navigation.findNavController(view);

    }

    private void displayTrips(List<Trip> user1) {

        dataModels = new ArrayList<>();
        dataModels.addAll(user1);

        adapter = new TripAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trip currentTrip = dataModels.get(position);
                tripViewModel.setCurrentTrip(currentTrip);
                navController.navigate(R.id.action_nav_your_trips_to_singleTripFragment, null);

            }
        });
    }
}


