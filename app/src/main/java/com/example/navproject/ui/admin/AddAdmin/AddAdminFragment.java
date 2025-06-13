package com.example.navproject.ui.admin.AddAdmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;

import java.util.*;

public class AddAdminFragment extends Fragment {

    private EditText emailEditText, firstNameEditText, lastNameEditText, passwordEditText, confirmPasswordEditText, phoneNumberEditText;
    private Spinner genderSpinner, countrySpinner, citySpinner;
    private Button registerAdminButton;// button

    private static final List<String> genders = Arrays.asList("Select Gender", "Male", "Female", "Other");
    private static final List<String> countries = Arrays.asList("Select Country", "USA", "Canada", "UK");
    private static final List<String> usaCities = Arrays.asList("Select City", "New York", "Los Angeles", "Chicago");
    private static final List<String> canadaCities = Arrays.asList("Select City", "Toronto", "Vancouver", "Montreal");
    private static final List<String> ukCities = Arrays.asList("Select City", "London", "Manchester", "Birmingham");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        emailEditText = view.findViewById(R.id.emailEditText);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

        genderSpinner = view.findViewById(R.id.genderSpinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        registerAdminButton = view.findViewById(R.id.registerButton);

        setupSpinners();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
                citySpinner.setEnabled(position != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        registerAdminButton.setOnClickListener(v -> {
            if (validateForm()) {
                String email = emailEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String countryCode = getCountryCode(selectedCountry);
                String gender = genderSpinner.getSelectedItem().toString();

                String role = "admin"; // Only admins here

                UserDataBaseHelper dbHelper = new UserDataBaseHelper(requireContext());
                dbHelper.insertUser(email, firstName, lastName, password, phoneNumber, selectedCountry, countryCode, gender,role);

                Toast.makeText(getContext(), "Admin added successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            }
        });

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        citySpinner.setEnabled(false);
    }

    private void updateCitySpinner(int countryPosition) {
        List<String> cities = new ArrayList<>();
        switch (countryPosition) {
            case 1: cities = usaCities; break;
            case 2: cities = canadaCities; break;
            case 3: cities = ukCities; break;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private boolean validateForm() {
        String email = emailEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String selectedCountry = countrySpinner.getSelectedItem().toString();
        String countryCode = getCountryCode(selectedCountry);

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@admin.com")) {
            emailEditText.setError("Email must be valid and end with @admin.com");
            return false;
        }

        if (firstName.length() < 3) {
            firstNameEditText.setError("First name must be at least 3 characters");
            return false;
        }

        if (lastName.length() < 3) {
            lastNameEditText.setError("Last name must be at least 3 characters");
            return false;
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\\$%^&*])[A-Za-z\\d!@#\\$%^&*]{6,}$")) {
            passwordEditText.setError("Password must contain at least 6 characters, one letter, one number, and one special character");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        String rawPhone = phoneNumber.replaceAll("[^\\d]", "");
        if (!rawPhone.startsWith(countryCode.replace("+", ""))) {
            phoneNumber = countryCode + rawPhone;
            phoneNumberEditText.setText(phoneNumber);
            rawPhone = phoneNumber.replaceAll("[^\\d]", "");
        }

        String numberPart = rawPhone.substring(countryCode.replace("+", "").length());
        if (numberPart.length() != 5) {
            phoneNumberEditText.setError("Phone number must be exactly 5 digits after " + countryCode);
            return false;
        }

        return true;
    }

    private String getCountryCode(String country) {
        switch (country) {
            case "USA": return "+1";
            case "Canada": return "+1";
            case "UK": return "+44";
            default: return "";
        }
    }

    private void clearForm() {
        emailEditText.setText("");
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        phoneNumberEditText.setText("");
        genderSpinner.setSelection(0);
        countrySpinner.setSelection(0);
        citySpinner.setSelection(0);
        citySpinner.setEnabled(false);
    }
}
