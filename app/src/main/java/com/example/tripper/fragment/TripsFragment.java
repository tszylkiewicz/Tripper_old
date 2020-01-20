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
import com.example.tripper.TripAdapter;
import com.example.tripper.model.Trip;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends Fragment {

    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;
    private NavController navController;

    private ListView listView;

    private ArrayList<Trip> dataModels;

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
        navController = Navigation.findNavController(view);

        if (userViewModel.getCurrentUser() == null) {
            navController.navigate(R.id.nav_sign_in);
        } else {
            MainActivity.getDisposables().add(tripViewModel.getAllUserTrips(userViewModel.getCurrentUser().getId())
                    .subscribe(this::displayTrips, Throwable::printStackTrace)
            );
        }

    }

    private void displayTrips(List<Trip> trips) {

        dataModels = new ArrayList<>();
        dataModels.addAll(trips);

        TripAdapter adapter = new TripAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Trip currentTrip = dataModels.get(position);
            tripViewModel.setCurrentTrip(currentTrip);
            navController.navigate(R.id.action_nav_your_trips_to_singleTripFragment, null);
        });
    }
}


