package com.example.navproject.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

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

        // Set up the gender spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up the country spinner
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Disable city spinner until a country is selected
        citySpinner.setEnabled(false);

        // Set up country selection listener to update cities
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
                citySpinner.setEnabled(position != 0);  // Enable city spinner only if a valid country is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle "Nothing selected" case if needed
            }
        });

        // Set up the register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    String role = "user";  // Default role is "user"
                    String email = emailEditText.getText().toString();

                    // Check if the email is "admin@admin.com" to assign admin role
                    if (email.equals("admin@admin.com")) {
                        role = "admin";
                    }

                    // Insert the user into the database
                    UserDataBaseHelper dbHelper = new UserDataBaseHelper(RegisterActivity.this);
                    dbHelper.insertUser(email, firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                            passwordEditText.getText().toString(), role);

                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCitySpinner(int countryPosition) {
        List<String> cities = null;

        // Initialize cities list based on selected country
        switch (countryPosition) {
            case 1: // USA
                cities = usaCities;
                break;
            case 2: // Canada
                cities = canadaCities;
                break;
            case 3: // UK
                cities = ukCities;
                break;
            default:
                cities = new ArrayList<>(); // Initialize with an empty list for "Select Country"
                break;
        }

        // Log the selected cities for debugging
        Log.d("RegisterActivity", "Selected country: " + countries.get(countryPosition) + ", cities: " + cities);

        // Now set the adapter for the city spinner
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

        // Validate email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address");
            return false;
        }

        // Validate first name and last name (min length 3)
        if (firstName.length() < 3) {
            firstNameEditText.setError("First name must be at least 3 characters");
            return false;
        }
        if (lastName.length() < 3) {
            lastNameEditText.setError("Last name must be at least 3 characters");
            return false;
        }

        // Validate password (at least 6 characters, one letter, one number, one special character)
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\\$%^&*])[A-Za-z\\d!@#\\$%^&*]{6,}$")) {
            passwordEditText.setError("Password must contain at least 6 characters, one letter, one number, and one special character");
            return false;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        // Validate phone number (this is a simple validation, can be enhanced)
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            phoneNumberEditText.setError("Invalid phone number");
            return false;
        }

        return true;
    }
}
