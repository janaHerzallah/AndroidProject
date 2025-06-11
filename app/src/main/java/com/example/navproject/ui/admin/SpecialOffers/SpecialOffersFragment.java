package com.example.navproject.ui.admin.SpecialOffers;

import androidx.lifecycle.ViewModelProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.navproject.R;
import com.example.navproject.ui.FeaturedProperties.FeaturedPropertiesFragment;
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
        loadProperties(); // Load all properties, not just featured ones
        return view;
    }

    private void loadProperties() {
        // Admin query: Fetch all properties, both featured and non-featured
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
                    // Mark property as featured
                    dbHelper.markPropertyAsFeatured(propertyId);

                    // Log the property status after marking
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor checkCursor = db.rawQuery("SELECT featured FROM properties WHERE id = ?", new String[]{String.valueOf(propertyId)});
                    if (checkCursor.moveToFirst()) {
                        int featuredStatus = checkCursor.getInt(checkCursor.getColumnIndex("featured"));
                        Log.d("AdminFeaturedStatus", "Property ID: " + propertyId + " marked as Featured: " + (featuredStatus == 1 ? "Yes" : "No"));
                    }
                    checkCursor.close();

                    // Update the button text and state
                    featureButton.setText("Already Featured");
                    featureButton.setEnabled(false);
                    Toast.makeText(getContext(), "Marked as Featured", Toast.LENGTH_SHORT).show();

                    // Refresh the list in the FeaturedPropertiesFragment after marking the property as featured
                    refreshFeaturedProperties();
                });
            }

            container.addView(itemView);
        }
        cursor.close();
    }

    // Method to refresh the featured properties list on the user side
    private void refreshFeaturedProperties() {
        // Find the fragment where the featured properties are being displayed
        FeaturedPropertiesFragment featuredFragment = (FeaturedPropertiesFragment) getActivity().getSupportFragmentManager().findFragmentByTag("FeaturedPropertiesFragment");

        // If the fragment is found, call its loadFeaturedProperties() method to reload the list
        if (featuredFragment != null) {
            featuredFragment.loadFeaturedProperties(); // Reload the featured properties
            Log.d("AdminFeaturedStatus", "Featured properties list refreshed.");
        } else {
            Log.e("SpecialOffersFragment", "FeaturedPropertiesFragment not found. Cannot refresh list.");
        }
    }
}
