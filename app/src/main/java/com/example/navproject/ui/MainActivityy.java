package com.example.navproject.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityy extends AppCompatActivity {

    private Button connectButton;
    private TextView errorMessage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityy_main);

        connectButton = findViewById(R.id.connectButton);
        errorMessage = findViewById(R.id.errorMessage);
        progressBar = findViewById(R.id.progressBar);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToApi();
            }
        });
    }

    // this function is to connect to the API, it has the function execute that takes the URL
    private void connectToApi() {
        progressBar.setVisibility(View.VISIBLE);  // Show the progress bar
        connectButton.setVisibility(View.INVISIBLE);  // Hide the button while connecting
        errorMessage.setVisibility(View.INVISIBLE);  // Hide the error message

        new ConnectionAsyncTask().execute("https://mocki.io/v1/f208b41b-12f1-45d0-9b74-2a635f184a2d");
    }

    private class ConnectionAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // SHOW connecting on the button
            connectButton.setText("Connecting...");
            progressBar.setVisibility(View.VISIBLE);  // Show the progress bar before starting the task
            errorMessage.setVisibility(View.INVISIBLE);  // Hide any previous error messages
        }
        @Override
        protected String doInBackground(String... urls) {
            // here is the http manager get data functionality as in exp8
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);  // Set connection timeout
                urlConnection.setReadTimeout(5000);     // Set read timeout

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();  // Return the response from the API
            } catch (Exception e) {
                Log.e("API_ERROR", "Error connecting to API: " + e.getMessage());
                return null;  // Return null if there is an error
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);  // Hide the progress bar
            connectButton.setVisibility(View.VISIBLE);  // Show the button again

            if (result != null) {
                try {
                    // Parse the API response (assuming it's a JSON object)
                    JSONObject jsonResponse = new JSONObject(result);

                    // Extract categories and properties
                    JSONArray categories = jsonResponse.getJSONArray("categories");
                    JSONArray properties = jsonResponse.getJSONArray("properties");

                    // You can process these arrays as needed here.
                    // For example, you can log or display the category names
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject category = categories.getJSONObject(i);
                        Log.d("CATEGORY", category.getString("name"));
                    }

                    // If successful, go to the next screen (Login/Register)
                    Intent intent = new Intent(MainActivityy.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
                    errorMessage.setVisibility(View.VISIBLE);  // Show the error message if parsing fails
                }
            } else {
                errorMessage.setVisibility(View.VISIBLE);  // Show error message if connection fails
            }
        }
    }
}
