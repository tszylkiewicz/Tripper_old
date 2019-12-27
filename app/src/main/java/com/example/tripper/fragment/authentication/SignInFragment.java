package com.example.tripper.fragment.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tripper.R;
import com.example.tripper.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextInputLayout email;
    private TextInputLayout password;
    private Button signIn;
    private Button signUp;

    private TextWatcher textWatcher;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        signIn = view.findViewById(R.id.signIn);
        signIn.setEnabled(false);
        signUp = view.findViewById(R.id.signUp);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkFields();
            }

        };

        email.getEditText().addTextChangedListener(textWatcher);
        password.getEditText().addTextChangedListener(textWatcher);

        final NavController navController = Navigation.findNavController(view);
        signIn.setOnClickListener(view1 -> {
            userViewModel.signIn(email.getEditText().getText().toString(), password.getEditText().getText().toString());
            Toast.makeText(this.getContext(), "Logged in successfully", Toast.LENGTH_LONG).show();
            navController.navigate(R.id.action_signInFragment_to_nav_map);
        });

        signUp.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }


    private void checkFields() {
        boolean emailValid = false;
        boolean passwordValid = false;
        if (!isValidEmail(email.getEditText().getText().toString())) {
            email.setError("Enter a valid address");
        } else {
            email.setError(null);
            emailValid = true;
        }
        if (!isValidPassword(password.getEditText().getText().toString())) {
            password.setError("Enter a valid password");
        } else {
            password.setError(null);
            passwordValid = true;
        }
        if (emailValid && passwordValid) {
            signIn.setEnabled(true);
        } else {
            signIn.setEnabled(false);
        }
    }

    private boolean isValidPassword(String target) {
        if (target == null || target.equals("")) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;

            final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%.,]).{6,20})";

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(target);
            System.out.println(matcher.matches());
            return matcher.matches();
        }
    }

    private boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
}
