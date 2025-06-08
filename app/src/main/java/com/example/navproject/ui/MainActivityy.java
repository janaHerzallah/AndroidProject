package com.example.navproject.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

    private static final String TAG = "API_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityy_main);

        connectButton = findViewById(R.id.connectButton);
        errorMessage = findViewById(R.id.errorMessage);
        progressBar = findViewById(R.id.progressBar);

        connectButton.setOnClickListener(v -> connectToApi());
    }

    private void connectToApi() {
        progressBar.setVisibility(View.VISIBLE);
        connectButton.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        new ConnectionAsyncTask().execute("https://mocki.io/v1/b3735c04-0279-4ced-a88b-3c53876d7b86");
    }

    private class ConnectionAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            connectButton.setText("Connecting...");
            progressBar.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                return result.toString();
            } catch (Exception e) {
                Log.e(TAG, "Connection Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            connectButton.setVisibility(View.VISIBLE);

            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray properties = jsonResponse.getJSONArray("properties");

                    int apartments = 0, villas = 0, lands = 0;

                    // Use your helper class to manage schema correctly
                    UserDataBaseHelper dbHelper = new UserDataBaseHelper(MainActivityy.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.markFirstTwoPropertiesAsFeatured();
                    // Clear old data
                    db.delete("properties", null, null);

                    for (int i = 0; i < properties.length(); i++) {
                        JSONObject prop = properties.getJSONObject(i);
                        String type = prop.getString("type");

                        switch (type) {
                            case "Apartment":
                                apartments++;
                                break;
                            case "Villa":
                                villas++;
                                break;
                            case "Land":
                                lands++;
                                break;
                        }

                        ContentValues values = new ContentValues();
                        values.put("id", prop.getInt("id"));  // note: "property_id" matches your schema
                        values.put("title", prop.getString("title"));
                        values.put("type", type);
                        values.put("price", prop.getInt("price"));
                        values.put("location", prop.getString("location"));
                        values.put("area", prop.getString("area"));
                        values.put("bedrooms", prop.getInt("bedrooms"));
                        values.put("bathrooms", prop.getInt("bathrooms"));
                        values.put("image_url", prop.getString("image_url"));
                        values.put("description", prop.getString("description"));
                        values.put("featured", 0); // Default to not featured

                        db.insert("properties", null, values);
                    }

                    Log.d(TAG, "Apartments: " + apartments);
                    Log.d(TAG, "Villas: " + villas);
                    Log.d(TAG, "Lands: " + lands);

                    // Optionally mark first two as featured
                    //dbHelper.markFirstTwoPropertiesAsFeatured();

                    // Continue to login
                    Intent intent = new Intent(MainActivityy.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                    errorMessage.setVisibility(View.VISIBLE);
                }
            } else {
                errorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
