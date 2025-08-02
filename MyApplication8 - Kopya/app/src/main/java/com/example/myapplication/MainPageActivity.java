package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView searchView;
    RecyclerView recyclerView;

    List<Movie> fullMovieList;
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

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

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // CSV dosyasından filmler yüklenir
        fullMovieList = loadMoviesFromCSV();

        adapter = new MovieAdapter(fullMovieList, movie -> {
            Intent intent = new Intent(MainPageActivity.this, MoviePageActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("genres", movie.getGenres());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Arama çubuğunda metin değiştikçe liste filtrelenir
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMovieList(newText);
                return true;
            }
        });
    }

    // Girilen arama kelimesine göre film listesini filtreler
    private void filterMovieList(String query) {
        List<Movie> filtered = new ArrayList<>();
        for (Movie m : fullMovieList) {
            if (m.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(m);
            }
        }
        adapter.updateMovieList(filtered);
    }

    // assets klasöründeki movies.csv dosyasını okuyup film listesini oluşturur
    private List<Movie> loadMoviesFromCSV() {
        List<Movie> movies = new ArrayList<>();
        try {
            InputStream is = getAssets().open("movies.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length >= 3) {
                    String title = tokens[1];
                    String genres = tokens[2];
                    movies.add(new Movie(title, genres));
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Navigation drawer menüsünden bir öğe seçildiğinde çağrılır
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // TODO: Profil sayfası
            return true;
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(MainPageActivity.this, MainActivity.class));
            finish();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}