package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoviePageActivity extends AppCompatActivity {

    private static final String TAG = "MoviePageActivity";

    TextView titleTextView, genresTextView;
    TextView watchLabel, likeLabel, rateLabel;
    ImageView watchIcon, likeIcon;
    ImageView[] stars;

    boolean isWatched = false;
    boolean isLiked = false;
    float currentRating = 0f;

    Database db;
    int userId = -1;
    String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        db = new Database(this);

        // ------------------ Get Intent Data ------------------
        movieTitle = getIntent().getStringExtra("title");
        String genres = getIntent().getStringExtra("genres");
        userId = getIntent().getIntExtra("userId", -1);

        // Safety check: if userId is invalid, return to login
        if (userId == -1) {
            Log.e(TAG, "Invalid userId. Returning to login.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        if (movieTitle == null) movieTitle = "Unknown Movie";
        if (genres == null) genres = "Unknown Genre";

        Log.d(TAG, "Received userId=" + userId + ", title=" + movieTitle + ", genres=" + genres);

        // ------------------ Views ------------------
        titleTextView = findViewById(R.id.detailTitle);
        genresTextView = findViewById(R.id.detailGenres);
        watchLabel = findViewById(R.id.watchLabel);
        watchIcon = findViewById(R.id.watchIcon);
        likeLabel = findViewById(R.id.likeLabel);
        likeIcon = findViewById(R.id.likeIcon);
        rateLabel = findViewById(R.id.rateLabel);

        stars = new ImageView[]{
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };

        titleTextView.setText(movieTitle);
        genresTextView.setText(genres);

        // ------------------ Load current user's movie state ------------------
        Database.MovieState state = db.getMovieState(userId, movieTitle);
        if (state != null) {
            isWatched = state.watched;
            isLiked = state.liked;
            currentRating = state.rating;
        }

        updateWatchUI();
        updateLikeUI();
        updateStars();
        updateRateLabel();

        // ------------------ Watch toggle ------------------
        watchIcon.setOnClickListener(v -> {
            isWatched = !isWatched;
            updateWatchUI();
            saveState();
        });

        // ------------------ Like toggle ------------------
        likeIcon.setOnClickListener(v -> {
            isLiked = !isLiked;
            updateLikeUI();
            saveState();
        });

        // ------------------ Star rating touch ------------------
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i;
            stars[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float width = v.getWidth();
                    currentRating = (x < width / 2) ? starIndex + 0.5f : starIndex + 1f;
                    updateStars();
                    updateRateLabel();
                    saveState();
                }
                return true;
            });
        }
    }

    // ------------------ Save state to DB for this user ------------------
    private void saveState() {
        if (userId != -1 && movieTitle != null) {
            db.saveMovieState(userId, movieTitle, isWatched, isLiked, currentRating);
            Log.d(TAG, "Saved state: userId=" + userId + ", movie=" + movieTitle +
                    ", watched=" + isWatched + ", liked=" + isLiked + ", rating=" + currentRating);
        } else {
            Log.e(TAG, "Cannot save state. Invalid userId or movieTitle");
        }
    }

    // ------------------ Update UI ------------------
    private void updateWatchUI() {
        watchLabel.setText(isWatched ? "Watched" : "Watch");
        watchIcon.setImageResource(isWatched ? R.drawable.ic_eye_filled_green : R.drawable.ic_eye_empty);
    }

    private void updateLikeUI() {
        likeLabel.setText(isLiked ? "Liked" : "Like");
        likeIcon.setImageResource(isLiked ? R.drawable.ic_heart_filled_red : R.drawable.ic_heart_empty);
    }

    private void updateStars() {
        for (int i = 0; i < stars.length; i++) {
            if (currentRating >= i + 1) stars[i].setImageResource(R.drawable.ic_star_filled);
            else if (currentRating >= i + 0.5) stars[i].setImageResource(R.drawable.ic_star_half);
            else stars[i].setImageResource(R.drawable.ic_star_empty);
        }
    }

    private void updateRateLabel() {
        if (currentRating > 0) rateLabel.setText(String.format("Rated: %.1f", currentRating));
        else rateLabel.setText("Rate this movie");
    }
}
