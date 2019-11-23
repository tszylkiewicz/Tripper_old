package com.example.tripper.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tripper.R;
import com.example.tripper.viewmodel.ResetPasswordViewModel;

public class ResetPasswordFragment extends Fragment {

    private ResetPasswordViewModel mViewModel;

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel.class);
        // TODO: Use the ViewModel
    }

}
