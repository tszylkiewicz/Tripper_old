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

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.TripAdapter;
import com.example.tripper.model.Trip;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;

    private ListView listView;

    ArrayList<Trip> dataModels;
    private static TripAdapter adapter;

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
        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.trips_list);

        MainActivity.getDisposables().add(tripViewModel.getAllPublicTrips().subscribe(user1 -> {
                    System.out.println("POBRANO WSZYSTKIE WYCIECZKI");
                    displayTrips(user1);
                }, throwable -> {
                    throwable.printStackTrace();
                })
        );

        //displayTrips(dataModels);
    }

    private void displayTrips(List<Trip> user1) {

        dataModels = new ArrayList<>();
        dataModels.addAll(user1);


        /*dataModels.add(new Trip(1, 1, "Apple Pie", "Android 1.0", 123.5, "car", 0, 0, false));
        dataModels.add(new Trip(1, 1, "Apple Pie2", "Android 2.0", 123.5, "car", 0, 0, false));
        dataModels.add(new Trip(1, 1, "Apple Pie3", "Android 3.0", 123.5, "car", 0, 0, false));
        dataModels.add(new Trip(1, 1, "Apple Pie4", "Android 4.0", 123.5, "car", 0, 0, false));
        dataModels.add(new Trip(1, 1, "Apple Pie5", "Android 5.0", 123.5, "car", 0, 0, false));
*/
        adapter = new TripAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trip dataModel = dataModels.get(position);

                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getDescription(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

}
