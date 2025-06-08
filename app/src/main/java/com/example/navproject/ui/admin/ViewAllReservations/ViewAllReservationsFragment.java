package com.example.navproject.ui.admin.ViewAllReservations;


import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;

public class ViewAllReservationsFragment extends Fragment {

    private LinearLayout container;
    private UserDataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_reservations, parent, false);
        container = view.findViewById(R.id.reservationContainer);
        dbHelper = new UserDataBaseHelper(requireContext());

        loadReservations();
        return view;
    }

    private void loadReservations() {
        Cursor cursor = dbHelper.getAllReservationsWithDetails();

        while (cursor.moveToNext()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_reservation, container, false);
            ((TextView) itemView.findViewById(R.id.customerNameTextView)).setText("Customer: " + firstName + " " + lastName);
            ((TextView) itemView.findViewById(R.id.propertyTitleTextView)).setText("Property: " + title);
            ((TextView) itemView.findViewById(R.id.timestampTextView)).setText("Date: " + timestamp);

            container.addView(itemView);
        }

        cursor.close();
    }
}
