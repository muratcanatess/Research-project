package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users table
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        // Movies state table per user
        db.execSQL("CREATE TABLE IF NOT EXISTS movie_states (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "movieTitle TEXT," +
                "watched INTEGER," +
                "liked INTEGER," +
                "rating REAL," +
                "UNIQUE(userId, movieTitle))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future DB upgrade logic
    }

    // --- User Methods ---
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{username});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            cursor.close();
        }
        return userId;
    }

    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = null;
        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE id=?", new String[]{String.valueOf(userId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            }
            cursor.close();
        }
        return username;
    }

    // --- Movie state per user ---
    public static class MovieState {
        public boolean watched;
        public boolean liked;
        public float rating;
    }

    public void saveMovieState(int userId, String movieTitle, boolean watched, boolean liked, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", userId);
        cv.put("movieTitle", movieTitle);
        cv.put("watched", watched ? 1 : 0);
        cv.put("liked", liked ? 1 : 0);
        cv.put("rating", rating);

        // Insert or update
        db.insertWithOnConflict("movie_states", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public MovieState getMovieState(int userId, String movieTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        MovieState state = null;
        Cursor cursor = db.rawQuery(
                "SELECT watched, liked, rating FROM movie_states WHERE userId=? AND movieTitle=?",
                new String[]{String.valueOf(userId), movieTitle});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                state = new MovieState();
                state.watched = cursor.getInt(cursor.getColumnIndexOrThrow("watched")) == 1;
                state.liked = cursor.getInt(cursor.getColumnIndexOrThrow("liked")) == 1;
                state.rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
            }
            cursor.close();
        }
        return state;
    }

    // --- Bulk load all movie states for a user ---
    public Map<String, MovieState> getAllMovieStatesForUser(int userId) {
        Map<String, MovieState> map = new HashMap<>();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor cursor = dbRead.rawQuery(
                "SELECT movieTitle, watched, liked, rating FROM movie_states WHERE userId=?",
                new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("movieTitle"));
                boolean watched = cursor.getInt(cursor.getColumnIndexOrThrow("watched")) != 0;
                boolean liked = cursor.getInt(cursor.getColumnIndexOrThrow("liked")) != 0;
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));

                MovieState state = new MovieState();
                state.watched = watched;
                state.liked = liked;
                state.rating = rating;

                map.put(title, state);
            }
            cursor.close();
        }

        return map;
    }

    // --- Get all watched movies for profile page ---
    public List<String> getWatchedMoviesForUser(int userId) {
        List<String> watchedMovies = new ArrayList<>();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor cursor = dbRead.rawQuery(
                "SELECT movieTitle FROM movie_states WHERE userId=? AND watched=1",
                new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                watchedMovies.add(cursor.getString(cursor.getColumnIndexOrThrow("movieTitle")));
            }
            cursor.close();
        }

        return watchedMovies;
    }
}
