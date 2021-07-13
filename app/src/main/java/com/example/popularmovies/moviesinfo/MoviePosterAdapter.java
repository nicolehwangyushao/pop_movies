package com.example.popularmovies.moviesinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private ArrayList<MovieSimpleData> movieSimpleDataList;
    private Context context;

    public MoviePosterAdapter(ArrayList<MovieSimpleData> movieDataList, Context context) {
        this.movieSimpleDataList = movieDataList;
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
        String path = movieSimpleDataList.get(position).posterPath;
        Picasso.get().load(baseUrl + size + path).placeholder(R.drawable.placeholder).resize(185, 900).into(holder.img);
    }

    public void insertMovieData(ArrayList<MovieSimpleData> addData){
        movieSimpleDataList.addAll(addData);
    }

    @Override
    public int getItemCount() {
        return movieSimpleDataList.size();
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
