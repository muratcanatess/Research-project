package com.example.movierecommendationapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<Film> filmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        filmList = new ArrayList<>();
        // Dummy Data Ekliyoruz
        filmList.add(new Film("Inception", "A mind-bending thriller", "https://picsum.photos/300/200?random=1"));
        filmList.add(new Film("Interstellar", "Exploring space and time", "https://picsum.photos/300/200?random=2"));
        filmList.add(new Film("The Dark Knight", "Gotham's hero rises", "https://picsum.photos/300/200?random=3"));

        filmAdapter = new FilmAdapter(this, filmList);
        recyclerView.setAdapter(filmAdapter);
    }
}
