package com.example.navproject.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.example.navproject.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Example: Show a welcome message to the admin
        Toast.makeText(AdminActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();

        // Add any specific admin features and UI here
    }
}
