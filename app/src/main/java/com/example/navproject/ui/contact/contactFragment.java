package com.example.navproject.ui.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

import com.example.navproject.R;

public class contactFragment extends Fragment {

    private ContactViewModel mViewModel;

    public static contactFragment newInstance() {
        return new contactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        // Initialize the ViewModel (optional in this case if you are not using it)
        mViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Set click listeners for the buttons
        Button buttonCallUs = rootView.findViewById(R.id.buttonCallUs);
        Button buttonLocateUs = rootView.findViewById(R.id.buttonLocateUs);
        Button buttonEmailUs = rootView.findViewById(R.id.buttonEmailUs);

        // Call Us Button
        buttonCallUs.setOnClickListener(v -> onCallUsClick());

        // Locate Us Button
        buttonLocateUs.setOnClickListener(v -> onLocateUsClick());

        // Email Us Button
        buttonEmailUs.setOnClickListener(v -> onEmailUsClick());

        return rootView;
    }

    // Method to handle "Call Us" button click
    public void onCallUsClick() {
        // Create an intent to dial the phone number
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+970599000000"));
        startActivity(intent);
    }

    // Method to handle "Locate Us" button click
    public void onLocateUsClick() {
        // Create an intent to open Google Maps with a predefined location (Agency's location)
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Agency+Location+Address"); // Replace with your real address
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    // Method to handle "Email Us" button click
    public void onEmailUsClick() {
        // Create an intent to send an email with the "to" field pre-filled
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:RealEstateHub@agency.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Customer");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I am interested in learning more about your properties.");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Use the ViewModel (if needed, otherwise this can be omitted)
    }
}
