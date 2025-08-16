package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername;
    RecyclerView recyclerViewWatched;
    MovieAdapter adapter;
    Database db;
    int userId = -1;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewUsername = findViewById(R.id.textViewUsername);
        recyclerViewWatched = findViewById(R.id.recyclerViewWatched);
        recyclerViewWatched.setLayoutManager(new LinearLayoutManager(this));

        db = new Database(this);

        // Get userId from intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            finish();
            return;
        }

        // Get username from DB
        username = db.getUsernameById(userId);
        textViewUsername.setText(username);

        // Load watched movies (titles only)
        List<String> watchedTitles = db.getWatchedMoviesForUser(userId);

        // Convert to Movie objects for adapter
        List<Movie> watchedMovies = new ArrayList<>();
        for (String title : watchedTitles) {
            watchedMovies.add(new Movie(title, "")); // "" for genres if unknown
        }

        // Set adapter
        adapter = new MovieAdapter(watchedMovies, movie -> {
            Intent intent = new Intent(ProfileActivity.this, MoviePageActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("genres", movie.getGenres());
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        recyclerViewWatched.setAdapter(adapter);
    }
}
