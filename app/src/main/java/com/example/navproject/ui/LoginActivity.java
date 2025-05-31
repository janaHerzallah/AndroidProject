package com.example.navproject.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navproject.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckbox;
    private Button loginButton;
    private TextView registerLink;

    // SharedPreferences key constants
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER_ME = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);


        // checking the shared preferences
        // Load saved email and set "Remember Me" checkbox if it's checked

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // get shared preferences with the name "LoginPrefs" and mode private (its only for this app)

        String savedEmail = sharedPreferences.getString(KEY_EMAIL, ""); // empty string if no email is stored

        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        // If "Remember Me" was checked, get the email in edittext and check the checkbox
        if (isRemembered) {
            emailEditText.setText(savedEmail);
            rememberMeCheckbox.setChecked(true);
        }

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Simple validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the email and password exist in the database
                    UserDataBaseHelper dbHelper = new UserDataBaseHelper(LoginActivity.this);
                    Cursor cursor = dbHelper.getUserByEmail(email);

                    if (cursor.moveToFirst()) {
                        String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
                        String role = cursor.getString(cursor.getColumnIndex("role"));

                        // Validate password
                        if (storedPassword.equals(password)) {
                            // Save email and "Remember Me" status in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (rememberMeCheckbox.isChecked()) {
                                editor.putString(KEY_EMAIL, email);
                                editor.putBoolean(KEY_REMEMBER_ME, true);
                            } else {
                                editor.remove(KEY_EMAIL);
                                editor.remove(KEY_REMEMBER_ME);
                            }
                            editor.apply();

                            // Redirect to the corresponding activity based on role
                            if (role.equals("admin")) {
                                Intent intent = new Intent(LoginActivity.this, com.example.navproject.MainActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, com.example.navproject.MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
            }
        });

        // Handle "Register" link click
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
