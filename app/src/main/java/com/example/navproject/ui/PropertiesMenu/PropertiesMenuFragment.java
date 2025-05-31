package com.example.navproject.ui.PropertiesMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.navproject.databinding.FragmentPropertiesmenuBinding;

public class PropertiesMenuFragment extends Fragment {

    private FragmentPropertiesmenuBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PropertiesMenuViewModel galleryViewModel =
                new ViewModelProvider(this).get(PropertiesMenuViewModel.class);

        // Inflate the correct binding class for the updated layout
        binding = FragmentPropertiesmenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Update the correct TextView reference based on the updated layout ID
        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
