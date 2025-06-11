package com.example.navproject.ui.admin.StatisticsDashBoard;

import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;

public class StatisticsDashboardFragment extends Fragment {

    private TextView userCountTextView, reservedPropertyCountTextView, topCountriesTextView,genderTextView;
    private UserDataBaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_dashboard, container, false);
        userCountTextView = view.findViewById(R.id.userCountTextView);
        reservedPropertyCountTextView = view.findViewById(R.id.reservedPropertyCountTextView);
        topCountriesTextView = view.findViewById(R.id.topCountriesTextView);
        genderTextView = view.findViewById(R.id.genderTextView);

        dbHelper = new UserDataBaseHelper(requireContext());

        loadStats();
        return view;
    }
    private void loadStats() {
        // Get the total number of users
        int userCount = dbHelper.getUserCount();

        // Get the number of reserved properties
        int reservedPropertyCount = dbHelper.getReservedPropertyCount();

        // Get the top countries by reservations
        Cursor topCountriesCursor = dbHelper.getTopCountriesByReservations();

        // Gender statistics
        int maleCount = dbHelper.getMaleUserCount();
        int femaleCount = dbHelper.getFemaleUserCount();

        // Log the counts
        Log.d("Statistics", "Total Users: " + userCount);
        Log.d("Statistics", "Male Users: " + maleCount);
        Log.d("Statistics", "Female Users: " + femaleCount);

        // Calculate gender percentages
        double malePercentage = (double) maleCount / userCount * 100;
        double femalePercentage = (double) femaleCount / userCount * 100;

        // Display user count and reserved properties
        userCountTextView.setText("Users: " + userCount);
        reservedPropertyCountTextView.setText("Reserved Properties: " + reservedPropertyCount);

        // Display top countries
        StringBuilder countriesText = new StringBuilder("Top Countries:\n");
        while (topCountriesCursor.moveToNext()) {
            String country = topCountriesCursor.getString(topCountriesCursor.getColumnIndexOrThrow("country"));
            int count = topCountriesCursor.getInt(topCountriesCursor.getColumnIndexOrThrow("count"));
            countriesText.append("- ").append(country).append(": ").append(count).append("\n");
        }
        topCountriesTextView.setText(countriesText.toString());

        // Display gender percentages
        genderTextView.setText("Male: " + String.format("%.2f", malePercentage) + "%\nFemale: " + String.format("%.2f", femalePercentage) + "%");

        topCountriesCursor.close();
    }


}
