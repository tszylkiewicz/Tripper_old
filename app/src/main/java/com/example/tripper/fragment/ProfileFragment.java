package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tripper.MainActivity;
import com.example.tripper.model.User;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.ProfileViewModel;
import com.example.tripper.R;
import com.example.tripper.viewmodel.UserViewModel;

import io.reactivex.disposables.CompositeDisposable;

import static android.text.InputType.TYPE_NULL;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private TextView email;

    private Button edit;
    private Button save;
    private Button cancel;

    private User user;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = userViewModel.getCurrentUser();
        System.out.println(user.getFormattedInfo());

        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        edit = view.findViewById(R.id.edit);
        save = view.findViewById(R.id.save);
        cancel = view.findViewById(R.id.cancel);

        disableEdition();
        setDefaultValues();

        edit.setOnClickListener(view1 -> {
            enableEdition();
        });

        save.setOnClickListener(view1 -> {
            System.out.println("Test1:" + username.getText().toString());
            System.out.println("Test1:" + firstName.getText().toString());
            System.out.println("Test1:" + lastName.getText().toString());

            user.setUsername(username.getText().toString());
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());

            MainActivity.getDisposables().add(userViewModel.update(user)
                    .subscribe(user1 -> {
                        System.out.println("LOL");
                        System.out.println(user1.getFormattedInfo());
                    }, throwable -> {
                        throwable.printStackTrace();
                    })
            );
            userViewModel.setCurrentUser(user);
            disableEdition();
        });

        cancel.setOnClickListener(view1 -> {
            setDefaultValues();
            disableEdition();
        });
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
