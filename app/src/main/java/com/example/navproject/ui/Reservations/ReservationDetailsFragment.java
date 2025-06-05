package com.example.navproject.ui.Reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.navproject.R;
import com.example.navproject.ui.PropertiesMenu.Property;
import com.example.navproject.ui.UserDataBaseHelper;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class ReservationDetailsFragment extends Fragment {

    private Property selectedProperty;
    private int userId;

    public static ReservationDetailsFragment newInstance(Property property, int userId) {
        ReservationDetailsFragment fragment = new ReservationDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedProperty", (Serializable) property);
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedProperty = (Property) getArguments().getSerializable("selectedProperty");
            userId = getArguments().getInt("userId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_details, container, false);

        ImageView propertyImage = view.findViewById(R.id.propertyImage);
        TextView propertyTitle = view.findViewById(R.id.propertyTitle);
        TextView propertyLocation = view.findViewById(R.id.propertyLocation);
        TextView propertyPrice = view.findViewById(R.id.propertyPrice);
        TextView descriptionText = view.findViewById(R.id.descriptionText);
        Button confirmButton = view.findViewById(R.id.button);

        if (selectedProperty != null) {
            propertyTitle.setText(selectedProperty.getTitle());
            propertyLocation.setText(selectedProperty.getLocation() + ", " + selectedProperty.getArea());
            propertyPrice.setText("$" + selectedProperty.getPrice());
            descriptionText.setText(selectedProperty.getDescription());

            Picasso.get()
                    .load(selectedProperty.getImageUrl())
                    .into(propertyImage);
        }

        confirmButton.setOnClickListener(v -> {
            UserDataBaseHelper dbHelper = new UserDataBaseHelper(requireContext());
            dbHelper.insertReservation(userId, selectedProperty.id);

            Toast.makeText(getContext(), "Reservation successful!", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed(); // Go back
        });

        return view;
    }
}
