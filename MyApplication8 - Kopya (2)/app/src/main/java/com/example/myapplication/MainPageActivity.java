package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView searchView;
    RecyclerView recyclerView;

    List<Movie> fullMovieList;
    MovieAdapter adapter;

    int userId = -1;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        db = new Database(this);

        // ------------------ Get userId from Intent ------------------
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Log.e("MainPageActivity", "No valid userId found! Returning to login.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Log.d("MainPageActivity", "Logged in userId=" + userId);

        // ------------------ Toolbar & Drawer ------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ------------------ Search ------------------
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        // ------------------ RecyclerView ------------------
        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load all current user's movie states in bulk
        Map<String, Database.MovieState> movieStatesMap = db.getAllMovieStatesForUser(userId);

        // Load movies from CSV and apply states
        fullMovieList = loadMoviesFromCSV(movieStatesMap);

        // Adapter setup
        adapter = new MovieAdapter(fullMovieList, movie -> {
            Log.d("MainPageActivity", "Opening MoviePageActivity: " + movie.getTitle());
            Intent intent = new Intent(MainPageActivity.this, MoviePageActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("genres", movie.getGenres());
            intent.putExtra("userId", userId); // pass current userId
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Search filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMovieList(newText);
                return true;
            }
        });
    }

    // ------------------ Filter Movies ------------------
    private void filterMovieList(String query) {
        List<Movie> filtered = new ArrayList<>();
        for (Movie m : fullMovieList) {
            if (m.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(m);
            }
        }
        adapter.updateMovieList(filtered);
    }

    // ------------------ Load Movies from CSV ------------------
    private List<Movie> loadMoviesFromCSV(Map<String, Database.MovieState> statesMap) {
        List<Movie> movies = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("movies.csv"))
            );

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length >= 3) {
                    String title = tokens[1];
                    String genres = tokens[2];

                    Movie movie = new Movie(title, genres);

                    // Apply current user's state from the map
                    Database.MovieState state = statesMap.get(title);
                    if (state != null) {
                        movie.setWatched(state.watched);
                        movie.setLiked(state.liked);
                        movie.setRating(state.rating);
                    }

                    movies.add(movie);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // ------------------ Drawer Navigation ------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            // Open ProfileActivity with current userId
            Intent intent = new Intent(MainPageActivity.this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.nav_logout) {
            startActivity(new Intent(MainPageActivity.this, MainActivity.class));
            finish();
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}
