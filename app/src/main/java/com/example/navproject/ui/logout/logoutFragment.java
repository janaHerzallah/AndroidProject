package com.example.navproject.ui.logout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.navproject.R;
import com.example.navproject.ui.LoginActivity;

public class logoutFragment extends Fragment {

    private LogoutViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        // Perform logout actions immediately
        logout();

        return view;
    }

    private void logout() {
        // Clear SharedPreferences (Remember Me)
        SharedPreferences preferences = requireActivity().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // clear all stored data
        editor.apply();

        // Go to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Finish MainActivity (navigation drawer host)
        requireActivity().finish();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
    }
}
