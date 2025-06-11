package com.example.navproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navproject.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckbox;
    private Button loginButton;
    private TextView registerLink;
    private ImageView loginImageView;


    // SharedPreferences key constants
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_USER_ID = "user_id"; // <-- NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        loginImageView = findViewById(R.id.loginImage);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        // Load the rotating animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        loginImageView.startAnimation(rotateAnimation); // Apply the animation to the image

        if (isRemembered) {
            emailEditText.setText(savedEmail);
            rememberMeCheckbox.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    UserDataBaseHelper dbHelper = new UserDataBaseHelper(LoginActivity.this);
                    Cursor cursor = dbHelper.getUserByEmail(email);

                    if (cursor.moveToFirst()) {
                        String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
                        String role = cursor.getString(cursor.getColumnIndex("role"));
                        int userId = cursor.getInt(cursor.getColumnIndex("_id")); // <-- get user ID

                        if (storedPassword.equals(password)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(KEY_USER_ID, userId); // <-- store user ID

                            if (rememberMeCheckbox.isChecked()) {
                                editor.putString(KEY_EMAIL, email);
                                editor.putBoolean(KEY_REMEMBER_ME, true);
                            } else {
                                editor.remove(KEY_EMAIL);
                                editor.remove(KEY_REMEMBER_ME);
                            }
                            editor.apply();

                            Intent intent;
                            if ("admin".equalsIgnoreCase(role)) {
                                intent = new Intent(LoginActivity.this, com.example.navproject.MainActivity_Admin.class);
                            } else {
                                intent = new Intent(LoginActivity.this, com.example.navproject.MainActivity.class);
                            }
                            startActivity(intent);


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

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
