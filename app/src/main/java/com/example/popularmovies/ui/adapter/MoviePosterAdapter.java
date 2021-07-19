package com.example.popularmovies.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.data.FavoriteMovieViewModel;
import com.example.popularmovies.ui.MainActivity;
import com.example.popularmovies.ui.MovieDetailsActivity;
import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private final ArrayList<MovieResult.MovieData> movieDataArrayList;
    private final Context context;
    private final FavoriteMovieViewModel favoriteMovieViewModel;

    public MoviePosterAdapter(ArrayList<MovieResult.MovieData> movieDataList, Context context, FavoriteMovieViewModel favoriteMovieViewModel) {
        this.movieDataArrayList = movieDataList;
        this.context = context;
        this.favoriteMovieViewModel = favoriteMovieViewModel;
    }

    @Override
    public MoviePosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie_poster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterAdapter.ViewHolder holder, int position) {

        String baseUrl = context.getString(R.string.poster_base_url);
        String size = context.getString(R.string.default_size);
        MovieResult.MovieData movieData = movieDataArrayList.get(position);
        Picasso.get().load(baseUrl + size + movieData.getPosterPath()).placeholder(R.drawable.placeholder).resize(185, 900).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE_DATA_KEY, movieDataArrayList.get(position));
            ((Activity)context).startActivityForResult(intent, MainActivity.NEW_FAVORITE_MOVIE_ACTIVITY_REQUEST_CODE);
        });
    }


    public void insertMovieData(ArrayList<MovieResult.MovieData> addData) {
        movieDataArrayList.addAll(addData);
    }

    @Override
    public int getItemCount() {
        return movieDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }

}
