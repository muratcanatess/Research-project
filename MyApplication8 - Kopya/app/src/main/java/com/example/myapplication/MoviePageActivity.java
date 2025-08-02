package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MoviePageActivity extends AppCompatActivity {

    TextView titleTextView, genresTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        titleTextView = findViewById(R.id.detailTitle);
        genresTextView = findViewById(R.id.detailGenres);

        String title = getIntent().getStringExtra("title");
        String genres = getIntent().getStringExtra("genres");

        titleTextView.setText(title);
        genresTextView.setText(genres);
    }
}