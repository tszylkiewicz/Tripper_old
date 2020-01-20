package com.example.tripper.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.model.Trip;
import com.example.tripper.viewmodel.TripViewModel;

import java.text.DecimalFormat;

import static android.text.InputType.TYPE_NULL;

public class SingleTripFragment extends Fragment {

    private TripViewModel tripViewModel;

    private EditText name;
    private EditText description;

    private CheckBox shared;

    private Button edit;
    private Button save;
    private Button cancel;

    private Trip trip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_trip, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trip = tripViewModel.getCurrentTrip();

        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        TextView distance = view.findViewById(R.id.distance);
        TextView transportType = view.findViewById(R.id.transportType);
        shared = view.findViewById(R.id.shared);
        edit = view.findViewById(R.id.edit);
        save = view.findViewById(R.id.save);
        cancel = view.findViewById(R.id.cancel);

        DecimalFormat df2 = new DecimalFormat("#.##");
        distance.setText(df2.format(trip.getDistance()) + " meters");
        transportType.setText(trip.getTransportType());

        disableEdition();
        setDefaultValues();

        edit.setOnClickListener(view1 -> enableEdition());

        save.setOnClickListener(view1 -> {
            trip.setName(name.getText().toString());
            trip.setDescription(description.getText().toString());

            if (shared.isChecked()) {
                trip.setShared(1);
            } else {
                trip.setShared(0);
            }

            MainActivity.getDisposables().add(tripViewModel.update(trip)
                    .subscribe(trip -> tripViewModel.setCurrentTrip(trip), Throwable::printStackTrace)
            );

            disableEdition();
        });

        cancel.setOnClickListener(view1 -> {
            setDefaultValues();
            disableEdition();
        });
    }

    private void setDefaultValues() {
        name.setText(trip.getName());
        if (trip.getDescription() != null) {
            description.setText(trip.getDescription());
        } else {
            description.setText("");
        }
        if (trip.isShared() == 1) {
            shared.setChecked(true);
        } else {
            shared.setChecked(false);
        }
    }

    private void disableEdition() {
        disableEditText(name);
        disableEditText(description);
        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        shared.setClickable(false);
    }

    private void enableEdition() {
        enableEditText(name);
        enableEditText(description);
        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        shared.setClickable(true);
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
