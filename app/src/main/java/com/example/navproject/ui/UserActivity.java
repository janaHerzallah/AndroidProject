package com.example.navproject.ui;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.example.navproject.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Example: Show a welcome message to the regular user
        Toast.makeText(UserActivity.this, "Welcome User!", Toast.LENGTH_SHORT).show();

        // Add any specific user features and UI here
    }
}
