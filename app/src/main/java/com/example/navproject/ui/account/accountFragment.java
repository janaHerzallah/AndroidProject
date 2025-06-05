package com.example.navproject.ui.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;
import com.squareup.picasso.Picasso;

public class accountFragment extends Fragment {

    private EditText firstNameInput, lastNameInput, phoneInput, passwordInput, imageUrlInput;
    private ImageView profileImageView;
    private Button updateButton;
    private int userId;
    private String countryCode = "+1"; // Default fallback

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firstNameInput = view.findViewById(R.id.editFirstName);
        lastNameInput = view.findViewById(R.id.editLastName);
        phoneInput = view.findViewById(R.id.editPhone);
        passwordInput = view.findViewById(R.id.editPassword);
        imageUrlInput = view.findViewById(R.id.editImageUrl);
        profileImageView = view.findViewById(R.id.profileImage);
        updateButton = view.findViewById(R.id.btnUpdateProfile);

        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", 0);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            UserDataBaseHelper dbHelper = new UserDataBaseHelper(requireContext());
            var cursor = dbHelper.getUserById(userId);
            if (cursor.moveToFirst()) {
                firstNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                lastNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                phoneInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));

                // ✅ Get country code from DB
                countryCode = cursor.getString(cursor.getColumnIndexOrThrow("country_code"));
                if (TextUtils.isEmpty(countryCode)) {
                    countryCode = "+1"; // fallback
                }

                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_url"));
                if (!TextUtils.isEmpty(imageUrl)) {
                    imageUrlInput.setText(imageUrl);
                    Picasso.get().load(imageUrl).into(profileImageView);
                }
            }
            cursor.close();
        }

        updateButton.setOnClickListener(v -> {
            String fname = firstNameInput.getText().toString().trim();
            String lname = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String imageUrl = imageUrlInput.getText().toString().trim();

            // ✅ Validate phone number using dynamic country code
            String digitsOnly = phone.replaceAll("[^\\d]", "");

            if (!digitsOnly.startsWith(countryCode.replace("+", ""))) {
                digitsOnly = countryCode.replace("+", "") + digitsOnly;
                phone = "+" + digitsOnly;
                phoneInput.setText(phone); // update UI
            }

            String numberAfterCode = digitsOnly.substring(countryCode.replace("+", "").length());
            if (numberAfterCode.length() != 5) {
                phoneInput.setError("Phone number must be exactly 5 digits after " + countryCode);
                return;
            }

            UserDataBaseHelper dbHelper = new UserDataBaseHelper(requireContext());
            dbHelper.updateUserProfile(userId, fname, lname, phone, imageUrl);

            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).into(profileImageView);
            }

            if (!TextUtils.isEmpty(password)) {
                boolean success = dbHelper.updateUserPassword(userId, password);
                if (!success) {
                    Toast.makeText(getContext(), "Password must contain at least 6 characters, 1 uppercase, and 1 digit.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
