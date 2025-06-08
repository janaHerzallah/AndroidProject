package com.example.navproject.ui.admin.SpecialOffers;

import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;

public class SpecialOffersFragment extends Fragment {

    private LinearLayout container;
    private UserDataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_special_offers, container, false);
        this.container = view.findViewById(R.id.propertiesContainer);
        dbHelper = new UserDataBaseHelper(requireContext());
        loadProperties();
        return view;
    }

    private void loadProperties() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM properties", null);

        while (cursor.moveToNext()) {
            int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow("featured")); // 0 or 1

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_property_admin, container, false);
            TextView titleText = itemView.findViewById(R.id.propertyTitleTextView);
            Button featureButton = itemView.findViewById(R.id.featureButton);

            titleText.setText(title);

            if (isFeatured == 1) {
                featureButton.setText("Already Featured");
                featureButton.setEnabled(false);
            } else {
                featureButton.setText("Feature");
                featureButton.setEnabled(true);
                featureButton.setOnClickListener(v -> {
                    dbHelper.markPropertyAsFeatured(propertyId);
                    featureButton.setText("Already Featured");
                    featureButton.setEnabled(false);
                    Toast.makeText(getContext(), "Marked as Featured", Toast.LENGTH_SHORT).show();
                });
            }

            container.addView(itemView);
        }
        cursor.close();
    }

}
