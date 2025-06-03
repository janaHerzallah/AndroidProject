package com.example.navproject.ui.PropertiesMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.navproject.databinding.FragmentPropertiesmenuBinding;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PropertiesMenuFragment extends Fragment {

    private FragmentPropertiesmenuBinding binding;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPropertiesmenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 🔄 Get userId from SharedPreferences (not from arguments)
        SharedPreferences prefs = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);  // Default -1 if not found

        if (userId == -1) {
            // If not found, fallback or show a message
            // You could redirect or show error
        }

        List<Property> properties = loadPropertiesFromDatabase();

        PropertyAdapter adapter = new PropertyAdapter(properties, getContext(), userId,false);
        binding.recyclerViewProperties.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProperties.setAdapter(adapter);


        return root;
    }

    private List<Property> loadPropertiesFromDatabase() {
        List<Property> list = new ArrayList<>();
        UserDataBaseHelper dbHelper = new UserDataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM properties", null);
        String[] columnNames = cursor.getColumnNames();
        for (String col : columnNames) {
            android.util.Log.d("DB_COLUMNS", col);
        }

        while (cursor.moveToNext()) {
            list.add(new Property(
                    cursor.getInt(cursor.getColumnIndex("id")),  // ✅ Fixed
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
