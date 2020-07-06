package com.example.tripper.fragment;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripper.MainActivity;
import com.example.tripper.model.Trip;
import com.example.tripper.model.User;
import com.example.tripper.R;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;

import java.util.List;
import java.util.stream.Collectors;

import static android.text.InputType.TYPE_NULL;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private TripViewModel tripViewModel;

    private EditText username;
    private EditText firstName;
    private EditText lastName;

    private TextView email;
    private TextView tripsCreated;
    private TextView tripsPublished;

    private Button edit;
    private Button save;
    private Button cancel;

    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        if (userViewModel.getCurrentUser() == null) {
            navController.navigate(R.id.nav_sign_in);
        } else {

            user = userViewModel.getCurrentUser();

            username = view.findViewById(R.id.username);
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);

            email = view.findViewById(R.id.email);
            tripsCreated = view.findViewById(R.id.tripsCreated);
            tripsPublished = view.findViewById(R.id.tripsPublished);

            edit = view.findViewById(R.id.edit);
            save = view.findViewById(R.id.save);
            cancel = view.findViewById(R.id.cancel);

            disableEdition();
            setDefaultValues();

            MainActivity.getDisposables().add(tripViewModel.getAllUserTrips(user.getId())
                    .subscribe(this::DisplayTripsStats, Throwable::printStackTrace)
            );

            edit.setOnClickListener(view1 -> enableEdition());

            save.setOnClickListener(view1 -> {
                user.setUsername(username.getText().toString());
                user.setFirstName(firstName.getText().toString());
                user.setLastName(lastName.getText().toString());

                MainActivity.getDisposables().add(userViewModel.update(user)
                        .subscribe(user1 -> Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_LONG).show(), Throwable::printStackTrace)
                );
                userViewModel.setCurrentUser(user);
                disableEdition();
            });

            cancel.setOnClickListener(view1 -> {
                setDefaultValues();
                disableEdition();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void DisplayTripsStats(List<Trip> trips) {

        tripsCreated.setText(String.valueOf(trips.size()));

        List<Trip> published = trips.stream().filter(trip ->
                trip.isShared() == 1
        ).collect(Collectors.toList());

        tripsPublished.setText(String.valueOf(published.size()));
    }

    private void setDefaultValues() {
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        if (user.getFirstName() != null) {
            firstName.setText(user.getFirstName());
        } else {
            firstName.setText("");
        }

        if (user.getLastName() != null) {
            lastName.setText(user.getLastName());
        } else {
            lastName.setText("");
        }
    }

    private void disableEdition() {
        disableEditText(username);
        disableEditText(firstName);
        disableEditText(lastName);
        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    private void enableEdition() {
        enableEditText(username);
        enableEditText(firstName);
        enableEditText(lastName);
        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }

    private void disableEditText(EditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setInputType(TYPE_NULL);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setBackgroundColor(Color.LTGRAY);

    }
}
