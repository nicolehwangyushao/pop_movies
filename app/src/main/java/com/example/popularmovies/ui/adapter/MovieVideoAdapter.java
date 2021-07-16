package com.example.popularmovies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieVideoResult;

import java.util.ArrayList;

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.ViewHolder> {
    private final ArrayList<MovieVideoResult.MovieVideo> movieVideosArrayList;
    private final Context context;

    public MovieVideoAdapter(ArrayList<MovieVideoResult.MovieVideo> movieVideosList, Context context) {
        this.movieVideosArrayList = movieVideosList;
        this.context = context;
    }

    @Override
    public MovieVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie_video, parent, false);
        return new MovieVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideoAdapter.ViewHolder holder, int position) {

        holder.videoTextView.setText(movieVideosArrayList.get(position).getName());
//        String baseUrl = context.getString(R.string.poster_base_url);
//        String size = context.getString(R.string.default_size);
//        String path = movieDataArrayList.get(position).getPosterPath();
//        Picasso.get().load(baseUrl + size + path).placeholder(R.drawable.placeholder).resize(185, 900).into(holder.img);
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context.getApplicationContext(), MovieDetailsActivity.class);
//            intent.putExtra(MovieDetailsActivity.MOVIE_DATA_KEY, movieDataArrayList.get(position));
//            context.startActivity(intent);
//        });
    }


    @Override
    public int getItemCount() {
        return movieVideosArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView videoTextView;

        public ViewHolder(View view) {
            super(view);
            videoTextView = view.findViewById(R.id.videoNameTextView);
        }
    }
}
