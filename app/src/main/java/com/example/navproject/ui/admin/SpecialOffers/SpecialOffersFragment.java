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
import android.widget.EditText;
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
        loadProperties();
        return view;
    }

    private void loadProperties() {
        container.removeAllViews(); // clear existing items before reloading
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM properties", null);

        while (cursor.moveToNext()) {
            int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow("featured"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_property_admin, container, false);
            TextView titleText = itemView.findViewById(R.id.propertyTitleTextView);
            TextView priceText = itemView.findViewById(R.id.propertyPriceTextView);
            Button featureButton = itemView.findViewById(R.id.featureButton);
            Button updatePriceButton = itemView.findViewById(R.id.updatePriceButton);
            Button removeFeatureButton = itemView.findViewById(R.id.removeFeatureButton);

            titleText.setText(title);
            priceText.setText("$" + price);

            // Handle Feature Button
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
                    refreshFeaturedProperties();
                });
            }

            // Handle Update Price Button
            updatePriceButton.setOnClickListener(v -> {
                showPriceUpdateDialog(propertyId, price);
            });

            // Handle Remove from Featured
            if (isFeatured == 1) {
                removeFeatureButton.setVisibility(View.VISIBLE);
                removeFeatureButton.setOnClickListener(v -> {
                    dbHelper.unmarkPropertyAsFeatured(propertyId);
                    Toast.makeText(getContext(), "Removed from Special Offers", Toast.LENGTH_SHORT).show();
                    loadProperties(); // reload the list
                });
            } else {
                removeFeatureButton.setVisibility(View.GONE);
            }

            container.addView(itemView);
        }
        cursor.close();
    }

    private void showPriceUpdateDialog(int propertyId, double currentPrice) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Update Price");

        final EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(currentPrice));
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            double newPrice = Double.parseDouble(input.getText().toString());
            dbHelper.updatePropertyPrice(propertyId, newPrice);
            Toast.makeText(requireContext(), "Price updated!", Toast.LENGTH_SHORT).show();
            loadProperties();  // Refresh list after update
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void refreshFeaturedProperties() {
        FeaturedPropertiesFragment featuredFragment = (FeaturedPropertiesFragment) getActivity().getSupportFragmentManager().findFragmentByTag("FeaturedPropertiesFragment");

        if (featuredFragment != null) {
            featuredFragment.loadFeaturedProperties();
            Log.d("AdminFeaturedStatus", "Featured properties list refreshed.");
        } else {
            Log.e("SpecialOffersFragment", "FeaturedPropertiesFragment not found. Cannot refresh list.");
        }
    }
}
