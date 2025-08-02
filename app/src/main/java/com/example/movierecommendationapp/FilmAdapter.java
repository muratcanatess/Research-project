package com.example.movierecommendationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private Context context;
    private List<Film> filmList;

    public FilmAdapter(Context context, List<Film> filmList) {
        this.context = context;
        this.filmList = filmList;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = filmList.get(position);
        holder.title.setText(film.getTitle());
        holder.description.setText(film.getDescription());
        Picasso.get().load(film.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvFilmTitle);
            description = itemView.findViewById(R.id.tvFilmDescription);
            image = itemView.findViewById(R.id.ivFilmImage);
        }
    }
}
