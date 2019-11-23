package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripper.R;
import com.example.tripper.viewmodel.SignInViewModel;
import com.example.tripper.viewmodel.SignUpViewModel;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signUp;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(SignUpViewModel.class);

        email = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        signUp = view.findViewById(R.id.signUp);

        final NavController navController = Navigation.findNavController(view);

        signUp.setOnClickListener(view1 -> {
            Toast.makeText(this.getContext(), "Sign up btn clicked", Toast.LENGTH_LONG).show();
            navController.navigate(R.id.nav_map, null);
        });
    }
}
