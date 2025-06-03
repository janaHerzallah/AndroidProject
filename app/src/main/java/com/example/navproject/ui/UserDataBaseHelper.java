package com.example.navproject.ui;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";  // "admin" or "user"

    // Properties table
    public static final String TABLE_PROPERTIES = "properties";

    public UserDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT);";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_PROPERTIES_TABLE = "CREATE TABLE IF NOT EXISTS properties (" +
                "property_id INTEGER PRIMARY KEY, " +
                "title TEXT, " +
                "type TEXT, " +
                "price REAL, " +
                "location TEXT, " +
                "area TEXT, " +
                "bedrooms INTEGER, " +
                "bathrooms INTEGER, " +
                "image_url TEXT, " +
                "description TEXT);";
        db.execSQL(CREATE_PROPERTIES_TABLE);

        String CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "property_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(_id), " +
                "FOREIGN KEY(property_id) REFERENCES properties(property_id));";
        db.execSQL(CREATE_FAVORITES_TABLE);

        insertAdminAccount(db);
    }

    private void insertAdminAccount(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?", new String[]{"admin@admin.com"}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, "admin@admin.com");
            values.put(COLUMN_PASSWORD, "Admin123!");
            values.put(COLUMN_ROLE, "admin");
            db.insert(TABLE_USERS, null, values);
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(db);
    }

    public void insertUser(String email, String firstName, String lastName, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
    }

    public void logPropertyTypeCounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] types = {"Apartment", "Villa", "Land"};
        for (String type : types) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM properties WHERE type = ?", new String[]{type});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d("PROPERTY_COUNT", type + "s: " + count);
            }
            cursor.close();
        }
        db.close();
    }

    public void insertFavorite(int userId, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("property_id", propertyId);
        db.insert("favorites", null, values);
        db.close();
    }

    public void logFavoritesTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT f.id AS fav_id, f.user_id, f.property_id, p.title " +
                        "FROM favorites f " +
                        "LEFT JOIN properties p ON f.property_id = p.id", null);

        Log.d("FAVORITES_TABLE", "---- Favorites ----");
        while (cursor.moveToNext()) {
            int favId = cursor.getInt(cursor.getColumnIndexOrThrow("fav_id"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));

            Log.d("FAVORITES_TABLE", "Fav ID: " + favId +
                    " | User ID: " + userId +
                    " | Property ID: " + propertyId +
                    " | Title: " + (title != null ? title : "N/A"));
        }
        cursor.close();
        db.close();
    }
}
