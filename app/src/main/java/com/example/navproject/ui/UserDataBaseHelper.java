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

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";  // "admin" or "user"

    public UserDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT);";
        db.execSQL(CREATE_USERS_TABLE);

        // Insert pre-created admin account if not exists
        insertAdminAccount(db);
    }

    private void insertAdminAccount(SQLiteDatabase db) {
        // Check if the admin account already exists
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?", new String[]{"admin@admin.com"}, null, null, null);
        if (cursor.getCount() == 0) {
            // Admin account does not exist, insert it
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, "admin@admin.com");
            values.put(COLUMN_PASSWORD, "Admin123!");  // Ensure this is hashed for security in production!
            values.put(COLUMN_ROLE, "admin");
            db.insert(TABLE_USERS, null, values);
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert a new user into the database
    public void insertUser(String email, String firstName, String lastName, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);  // Role could be "admin" or "user"

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    // Get a user by email (used for login)
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
    }
}
