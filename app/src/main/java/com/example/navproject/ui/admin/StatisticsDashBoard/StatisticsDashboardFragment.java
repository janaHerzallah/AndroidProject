package com.example.navproject.ui.admin.StatisticsDashBoard;

import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;

public class StatisticsDashboardFragment extends Fragment {

    private TextView userCountTextView, reservedPropertyCountTextView, topCountriesTextView;
    private UserDataBaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_dashboard, container, false);
        userCountTextView = view.findViewById(R.id.userCountTextView);
        reservedPropertyCountTextView = view.findViewById(R.id.reservedPropertyCountTextView);
        topCountriesTextView = view.findViewById(R.id.topCountriesTextView);
        dbHelper = new UserDataBaseHelper(requireContext());

        loadStats();
        return view;
    }

    private void loadStats() {
        int userCount = dbHelper.getUserCount();
        int reservedPropertyCount = dbHelper.getReservedPropertyCount();
        Cursor topCountriesCursor = dbHelper.getTopCountriesByReservations();

        userCountTextView.setText("Users: " + userCount);
        reservedPropertyCountTextView.setText("Reserved Properties: " + reservedPropertyCount);

        StringBuilder countriesText = new StringBuilder("Top Countries:\n");
        while (topCountriesCursor.moveToNext()) {
            String country = topCountriesCursor.getString(topCountriesCursor.getColumnIndexOrThrow("country"));
            int count = topCountriesCursor.getInt(topCountriesCursor.getColumnIndexOrThrow("count"));
            countriesText.append("- ").append(country).append(": ").append(count).append("\n");
        }
        topCountriesTextView.setText(countriesText.toString());

        topCountriesCursor.close();
    }
}
