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
                "phone TEXT, " +
                "profile_image_url TEXT, " +
                "country TEXT, " +                //
                "country_code TEXT, " +           //
                COLUMN_ROLE + " TEXT);";

        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_PROPERTIES_TABLE = "CREATE TABLE IF NOT EXISTS properties (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "type TEXT, " +
                "price REAL, " +
                "location TEXT, " +
                "area TEXT, " +
                "bedrooms INTEGER, " +
                "bathrooms INTEGER, " +
                "image_url TEXT, " +
                "description TEXT, " +
                "featured INTEGER DEFAULT 0);"; // ✅ New featured column
        db.execSQL(CREATE_PROPERTIES_TABLE);

        String CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "property_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(_id), " +
                "FOREIGN KEY(property_id) REFERENCES properties(property_id), " +
                "UNIQUE(user_id, property_id));";
        db.execSQL(CREATE_FAVORITES_TABLE);

        String CREATE_RESERVATIONS_TABLE = "CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "property_id INTEGER, " +
                "timestamp TEXT, " +  // Save date & time of reservation
                "FOREIGN KEY(user_id) REFERENCES users(_id), " +
                "FOREIGN KEY(property_id) REFERENCES properties(property_id), " +
                "UNIQUE(user_id, property_id));";
        db.execSQL(CREATE_RESERVATIONS_TABLE);


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
        db.execSQL("DROP TABLE IF EXISTS reservations");

        onCreate(db);
    }

    public void insertUser(String email, String firstName, String lastName, String password, String phone, String country, String countryCode, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put("phone", phone);
        values.put("country", country);             // ✅ Save country
        values.put("country_code", countryCode);    // ✅ Save code
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

        // First, check if it already exists
        Cursor cursor = db.rawQuery(
                "SELECT * FROM favorites WHERE user_id = ? AND property_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(propertyId)}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) {
            Log.d("DB_FAVORITES", "Favorite already exists: user " + userId + ", property " + propertyId);
            return; // Do not insert again
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("property_id", propertyId);

        db.insert("favorites", null, values);
        Log.d("DB_FAVORITES", "Favorite added: user " + userId + ", property " + propertyId);
    }

    public void insertReservation(int userId, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM reservations WHERE user_id = ? AND property_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(propertyId)}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) return;

        String timestamp = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("property_id", propertyId);
        values.put("timestamp", timestamp);  // Save date/time

        db.insert("reservations", null, values);
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

    public void removeFavorite(int userId, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "user_id=? AND property_id=?", new String[]{
                String.valueOf(userId), String.valueOf(propertyId)
        });
        db.close();
    }

    public void logReservationsTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT r.id AS res_id, r.user_id, r.property_id, p.title " +
                        "FROM reservations r " +
                        "LEFT JOIN properties p ON r.property_id = p.id", null);

        Log.d("RESERVATIONS_TABLE", "---- Reservations ----");
        while (cursor.moveToNext()) {
            int resId = cursor.getInt(cursor.getColumnIndexOrThrow("res_id"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));

            Log.d("RESERVATIONS_TABLE", "Res ID: " + resId +
                    " | User ID: " + userId +
                    " | Property ID: " + propertyId +
                    " | Title: " + (title != null ? title : "N/A"));
        }
        cursor.close();
        db.close();
    }

    public void updateUserProfile(int userId, String firstName, String lastName, String phone, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("phone", phone);
        values.put("profile_image_url", imageUrl);

        db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        if (!isValidPassword(newPassword)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d).{6,}$"); // 1 uppercase, 1 digit, 6+ chars
    }

    // get user by ID
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

    }

    public Cursor getFeaturedProperties() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM properties WHERE featured = 1", null);
    }

    public void markFirstTwoPropertiesAsFeatured() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE properties SET featured = 1 WHERE id IN (SELECT id FROM properties LIMIT 2)");
    }

    public void markPropertyAsFeatured(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("featured", 1);
        db.update("properties", values, "id = ?", new String[]{String.valueOf(propertyId)});
        db.close();
    }


    public Cursor getAllReservationsWithDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT r.timestamp, u.first_name, u.last_name, p.title " +
                        "FROM reservations r " +
                        "JOIN users u ON r.user_id = u._id " +
                        "JOIN properties p ON r.property_id = p.id", null);
    }


    // Count of all users
    public int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE role = 'user'", null);
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    // Count of unique reserved properties
    public int getReservedPropertyCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT property_id) FROM reservations", null);
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    // Top countries by number of reservations
    public Cursor getTopCountriesByReservations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT u.country, COUNT(*) AS count " +
                        "FROM reservations r " +
                        "JOIN users u ON r.user_id = u._id " +
                        "GROUP BY u.country " +
                        "ORDER BY count DESC LIMIT 3", null
        );
    }

    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _id, first_name, last_name, email FROM users WHERE role = 'user'", null);
    }

    public void deleteUserById(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }


}
