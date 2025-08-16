package com.example.myapplication;

public class Movie {
    private String title;
    private String genres;
    private boolean watched;
    private boolean liked;
    private float rating;

    public Movie(String title, String genres) {
        this.title = title;
        this.genres = genres;
        this.watched = false;
        this.liked = false;
        this.rating = 0f;
    }

    public String getTitle() {
        return title;
    }

    public String getGenres() {
        return genres;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
