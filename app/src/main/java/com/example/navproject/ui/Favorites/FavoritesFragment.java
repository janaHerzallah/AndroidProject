package com.example.navproject.ui.Favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.navproject.R;
import com.example.navproject.ui.PropertiesMenu.Property;
import com.example.navproject.ui.PropertiesMenu.PropertyAdapter;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load user ID from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            List<Property> favorites = loadFavoriteProperties(userId);
            adapter = new PropertyAdapter(favorites, getContext(), userId);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    private List<Property> loadFavoriteProperties(int userId) {
        List<Property> favorites = new ArrayList<>();
        UserDataBaseHelper dbHelper = new UserDataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p.* FROM properties p " +
                "INNER JOIN favorites f ON p.id = f.property_id " +
                "WHERE f.user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            favorites.add(new Property(
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
        return favorites;
    }
}
