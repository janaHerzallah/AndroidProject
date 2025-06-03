package com.example.navproject.ui.Reservations;

import static java.security.AccessController.getContext;

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

import com.example.navproject.databinding.FragmentReservationsBinding;
import com.example.navproject.ui.PropertiesMenu.Property;
import com.example.navproject.ui.Reservations.ReservationAdapter;
import com.example.navproject.ui.Reservations.ReservationItem;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private FragmentReservationsBinding binding;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences prefs = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            List<ReservationItem> reservations = loadReservations(userId);
            ReservationAdapter adapter = new ReservationAdapter(reservations, getContext());
            binding.recyclerViewReservations.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerViewReservations.setAdapter(adapter);
        }

        return root;
    }

    private List<ReservationItem> loadReservations(int userId) {
        List<ReservationItem> list = new ArrayList<>();
        UserDataBaseHelper dbHelper = new UserDataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p.*, r.timestamp FROM reservations r " +
                "JOIN properties p ON r.property_id = p.id " +
                "WHERE r.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            Property p = new Property(
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
            );
            String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            list.add(new ReservationItem(p, timestamp));
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
