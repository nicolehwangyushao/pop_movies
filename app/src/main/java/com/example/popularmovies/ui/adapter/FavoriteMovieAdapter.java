package com.example.popularmovies.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.data.FavoriteMovieData;
import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.ui.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class FavoriteMovieAdapter extends ListAdapter<FavoriteMovieData, FavoriteMovieAdapter.FavoriteMovieViewHolder> {
    Context mContext;

    public FavoriteMovieAdapter(@NonNull DiffUtil.ItemCallback<FavoriteMovieData> diffCallback, Context context) {
        super(diffCallback);
        mContext = context;
    }

    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie_poster, parent, false);
        return new FavoriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, int position) {
        FavoriteMovieData current = getItem(position);
        String baseUrl = mContext.getString(R.string.poster_base_url);
        String size = mContext.getString(R.string.default_size);
        Picasso.get().load(baseUrl + size + current.getPosterPath()).placeholder(R.drawable.placeholder).resize(185, 900).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            MovieResult.MovieData movieData = new MovieResult.MovieData();
            movieData.setMovieId(current.getMovieId());
            movieData.setTitle(current.getTitle());
            movieData.setPosterPath(current.getPosterPath());
            movieData.setBackDropPath(current.getBackdropPath());
            movieData.setVoteAverage(current.getVoteAverage());
            movieData.setReleaseDate(current.getReleaseDate());
            movieData.setOverview(current.getOverview());
            Intent intent = new Intent(mContext.getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE_DATA_KEY, movieData);
            mContext.startActivity(intent);
        });
    }

    public static class FavoriteMovieDiff extends DiffUtil.ItemCallback<FavoriteMovieData> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull FavoriteMovieData oldItem, @NonNull @NotNull FavoriteMovieData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull FavoriteMovieData oldItem, @NonNull @NotNull FavoriteMovieData newItem) {
            return oldItem.getMovieId() == newItem.getMovieId();
        }
    }


    public class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public FavoriteMovieViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }

}
