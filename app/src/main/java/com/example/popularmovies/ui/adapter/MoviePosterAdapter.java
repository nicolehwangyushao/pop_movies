package com.example.popularmovies.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.ui.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class MoviePosterAdapter extends PagingDataAdapter<MovieResult.MovieData, MoviePosterAdapter.ViewHolder> {
    //    private final ArrayList<MovieResult.MovieData> movieDataArrayList;
    private final Context context;

    public MoviePosterAdapter(@NonNull DiffUtil.ItemCallback<MovieResult.MovieData> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
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
        MovieResult.MovieData movieData = getItem(position);
        Picasso.get().load(baseUrl + size + movieData.getPosterPath()).placeholder(R.drawable.placeholder).resize(185, 900).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE_DATA_KEY, movieData);
            context.startActivity(intent);
        });
    }


    public static class MovieDiff extends DiffUtil.ItemCallback<MovieResult.MovieData> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull MovieResult.MovieData oldItem, @NonNull @NotNull MovieResult.MovieData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull MovieResult.MovieData oldItem, @NonNull @NotNull MovieResult.MovieData newItem) {
            return oldItem.getMovieId() == newItem.getMovieId();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }

}
