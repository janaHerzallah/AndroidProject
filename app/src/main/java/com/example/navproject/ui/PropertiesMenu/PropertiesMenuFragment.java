package com.example.navproject.ui.PropertiesMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.navproject.R;
import com.example.navproject.databinding.FragmentPropertiesmenuBinding;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertiesMenuFragment extends Fragment {

    private FragmentPropertiesmenuBinding binding;
    private int userId;
    private List<Property> allProperties;

    private String selectedType = "Any";
    private int minPrice = Integer.MIN_VALUE;
    private int maxPrice = Integer.MAX_VALUE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPropertiesmenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences prefs = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        allProperties = loadPropertiesFromDatabase();

        updatePropertyList(allProperties);

        //  Basic search bar
        binding.editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            applyFilters();
            return true;
        });

        //  Open filter dialog
        binding.buttonOpenFilters.setOnClickListener(v -> showFilterDialog());

        return root;
    }

    private void updatePropertyList(List<Property> properties) {
        PropertyAdapter adapter = new PropertyAdapter(properties, getContext(), userId, false);
        binding.recyclerViewProperties.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProperties.setAdapter(adapter);
    }

    private void showFilterDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filters, null);

        Spinner spinnerType = dialogView.findViewById(R.id.spinnerPropertyType);
        EditText editMinPrice = dialogView.findViewById(R.id.editTextMinPrice);
        EditText editMaxPrice = dialogView.findViewById(R.id.editTextMaxPrice);
        Button btnReset = dialogView.findViewById(R.id.buttonResetFilters);

        List<String> types = Arrays.asList( "apartment", "land", "villa" , "Any");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        // Pre-select previously selected values
        spinnerType.setSelection(types.indexOf(selectedType));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Filter Properties")
                .setView(dialogView)
                .setPositiveButton("Apply", (d, which) -> {
                    selectedType = spinnerType.getSelectedItem().toString();

                    String min = editMinPrice.getText().toString().trim();
                    String max = editMaxPrice.getText().toString().trim();

                    minPrice = min.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(min);
                    maxPrice = max.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(max);

                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .create();

        // 🧼 Reset button logic
        btnReset.setOnClickListener(v -> {
            spinnerType.setSelection(types.indexOf("Any"));
            editMinPrice.setText("");
            editMaxPrice.setText("");

            selectedType = "Any";
            minPrice = Integer.MIN_VALUE;
            maxPrice = Integer.MAX_VALUE;

            applyFilters();
            dialog.dismiss();
        });

        dialog.show();
    }


    private void applyFilters() {
        String query = binding.editTextSearch.getText().toString().trim().toLowerCase();

        List<Property> filtered = new ArrayList<>();
        for (Property p : allProperties) {
            boolean matches = (query.isEmpty() ||
                    p.location.toLowerCase().contains(query) ||
                    p.title.toLowerCase().contains(query));

            if (!selectedType.equals("Any") && !p.type.equalsIgnoreCase(selectedType)) {
                matches = false;
            }

            if (p.price < minPrice || p.price > maxPrice) {
                matches = false;
            }

            if (matches) filtered.add(p);
        }

        updatePropertyList(filtered);
    }

    private List<Property> loadPropertiesFromDatabase() {
        List<Property> list = new ArrayList<>();
        UserDataBaseHelper dbHelper = new UserDataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM properties WHERE id NOT IN (SELECT property_id FROM reservations)", null);

        while (cursor.moveToNext()) {
            list.add(new Property(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("location")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getInt(cursor.getColumnIndex("price")),
                    cursor.getString(cursor.getColumnIndex("area")),
                    cursor.getInt(cursor.getColumnIndex("bedrooms")),
                    cursor.getInt(cursor.getColumnIndex("bathrooms")),
                    cursor.getString(cursor.getColumnIndex("image_url"))
            ));
        }

        cursor.close();
        db.close();
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
