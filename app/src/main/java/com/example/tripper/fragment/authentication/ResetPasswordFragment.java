package com.example.tripper.fragment.authentication;

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

public class ResetPasswordFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextInputLayout oldPassword;
    private TextInputLayout newPassword;
    private TextInputLayout confirmPassword;
    private Button save;

    private TextWatcher textWatcher;

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);

        oldPassword = view.findViewById(R.id.oldPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);

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

        oldPassword.getEditText().addTextChangedListener(textWatcher);
        newPassword.getEditText().addTextChangedListener(textWatcher);
        confirmPassword.getEditText().addTextChangedListener(textWatcher);

        save = view.findViewById(R.id.save);
        save.setEnabled(false);

        final NavController navController = Navigation.findNavController(view);
        save.setOnClickListener(view1 -> {
            Toast.makeText(this.getContext(), "Password reset successfully", Toast.LENGTH_LONG).show();
            navController.navigate(R.id.nav_map, null);
        });
    }

    private void checkFields() {
        boolean emailValid = false;
        boolean passwordValid = false;
        if (!isValidPassword(oldPassword.getEditText().getText().toString())) {
            oldPassword.setError("Enter a valid address");
        } else {
            oldPassword.setError(null);
            emailValid = true;
        }
        if (!isValidPassword(newPassword.getEditText().getText().toString())) {
            newPassword.setError("Enter a valid password");
        } else {
            newPassword.setError(null);
            passwordValid = true;
        }
        if (emailValid && passwordValid) {
            save.setEnabled(true);
        } else {
            save.setEnabled(false);
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
}
