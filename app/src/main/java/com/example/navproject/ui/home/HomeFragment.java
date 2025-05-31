package com.example.navproject.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.navproject.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel (if necessary, this can be removed if not needed)
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout for this fragment and bind it to the views
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Reference to the TextViews for About Us and History sections
        TextView aboutUsContent = binding.aboutUsContent;
        TextView historyContent = binding.historyContent;

        // Set the content for the "About Us" section
        aboutUsContent.setText("Welcome to [Jana and Reem Agency], a trusted leader in the real estate industry. " +
                "We are dedicated to providing personalized property solutions and creating a seamless experience for our clients. " +
                "With an extensive portfolio of residential, commercial, and land properties, we offer a broad range of options to help you find the perfect investment or dream home.");

        // Set the content for the "Company History" section
        historyContent.setText("Founded in [2020], [Jana and Reem Agency] has grown from a small family-run business to a leader in the real estate industry. " +
                "Our success is built on a foundation of expertise, integrity, and an unwavering dedication to our clients. " +
                "Today, we continue to innovate and grow, always striving to offer the best properties and real estate opportunities.");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
