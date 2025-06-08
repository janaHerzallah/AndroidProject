package com.example.navproject.ui.admin.DeleteCustomers;

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

public class DeleteCustomersFragment extends Fragment {

    private LinearLayout customersContainer;
    private UserDataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_customers, container, false);
        customersContainer = view.findViewById(R.id.customersContainer);
        dbHelper = new UserDataBaseHelper(requireContext());

        loadCustomers();

        return view;
    }

    private void loadCustomers() {
        customersContainer.removeAllViews();  // Clear previous entries
        Cursor cursor = dbHelper.getAllCustomers();

        while (cursor.moveToNext()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_customer, customersContainer, false);
            TextView nameTextView = itemView.findViewById(R.id.nameTextView);
            TextView emailTextView = itemView.findViewById(R.id.emailTextView);
            Button deleteButton = itemView.findViewById(R.id.deleteButton);

            nameTextView.setText("Name: " + firstName + " " + lastName);
            emailTextView.setText("Email: " + email);

            deleteButton.setOnClickListener(v -> {
                dbHelper.deleteUserById(userId);
                Toast.makeText(getContext(), "Deleted " + firstName, Toast.LENGTH_SHORT).show();
                loadCustomers(); // Refresh list
            });

            customersContainer.addView(itemView);
        }

        cursor.close();
    }
}
