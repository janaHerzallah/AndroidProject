package com.example.navproject.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navproject.R;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, firstNameEditText, lastNameEditText, passwordEditText, confirmPasswordEditText, phoneNumberEditText;
    private Spinner genderSpinner, countrySpinner, citySpinner;
    private Button registerButton;

    private static final List<String> genders = Arrays.asList("Select Gender", "Male", "Female", "Other");
    private static final List<String> countries = Arrays.asList("Select Country", "USA", "Canada", "UK");
    private static final List<String> usaCities = Arrays.asList("Select City", "New York", "Los Angeles", "Chicago");
    private static final List<String> canadaCities = Arrays.asList("Select City", "Toronto", "Vancouver", "Montreal");
    private static final List<String> ukCities = Arrays.asList("Select City", "London", "Manchester", "Birmingham");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        genderSpinner = findViewById(R.id.genderSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);
        citySpinner = findViewById(R.id.citySpinner);
        registerButton = findViewById(R.id.registerButton);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        citySpinner.setEnabled(false);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
                citySpinner.setEnabled(position != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        registerButton.setOnClickListener(v -> {
            if (validateForm()) {
                String email = emailEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String countryCode = getCountryCode(selectedCountry);

                //String role = email.equals("admin@admin.com") ? "admin" : "user";
                String role = email.endsWith("@admin.com") ? "admin" : "user";

                UserDataBaseHelper dbHelper = new UserDataBaseHelper(RegisterActivity.this);
                dbHelper.insertUser(email, firstName, lastName, password, phoneNumber, selectedCountry, countryCode, role);

                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCitySpinner(int countryPosition) {
        List<String> cities = new ArrayList<>();
        switch (countryPosition) {
            case 1: cities = usaCities; break;
            case 2: cities = canadaCities; break;
            case 3: cities = ukCities; break;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
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

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address");
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
}
