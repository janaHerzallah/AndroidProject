package com.example.navproject.ui.FeaturedProperties;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navproject.R;
import com.example.navproject.ui.PropertiesMenu.Property;
import com.example.navproject.ui.PropertiesMenu.PropertyAdapter;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FeaturedPropertiesFragment extends Fragment {

    private RecyclerView featuredRecyclerView;
    private PropertyAdapter adapter;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured_properties, container, false);

        featuredRecyclerView = view.findViewById(R.id.featuredRecyclerView);
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        List<Property> featuredList = loadFeaturedProperties();
        adapter = new PropertyAdapter(featuredList, getContext(), userId, false);
        featuredRecyclerView.setAdapter(adapter);

        return view;
    }

    private List<Property> loadFeaturedProperties() {
        List<Property> list = new ArrayList<>();
        UserDataBaseHelper dbHelper = new UserDataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM properties WHERE featured = 1", null);

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
}
