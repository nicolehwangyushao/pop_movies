package com.example.popularmovies.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieReviewResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {
    private final ArrayList<MovieReviewResult.MovieReview> movieReviewsArrayList;

    public MovieReviewAdapter(ArrayList<MovieReviewResult.MovieReview> movieReviewList) {
        this.movieReviewsArrayList = movieReviewList;
    }

    @Override
    public MovieReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie_review, parent, false);
        return new MovieReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.ViewHolder holder, int position) {
        MovieReviewResult.MovieReview movieReview = movieReviewsArrayList.get(position);
        holder.authorTextView.setText(movieReview.getAuthor());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        try {
            Date date = formatter.parse(movieReview.getCreatedDate().replaceAll("Z$", "+0000"));
            holder.dateTextView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.contentTextView.setText(movieReview.getContent());
    }

    public void insertMovieData(ArrayList<MovieReviewResult.MovieReview> addData) {
        movieReviewsArrayList.addAll(addData);
    }

    @Override
    public int getItemCount() {
        return movieReviewsArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView dateTextView;
        TextView contentTextView;

        public ViewHolder(View view) {
            super(view);
            authorTextView = view.findViewById(R.id.authorTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            contentTextView = view.findViewById(R.id.contentTextView);
        }
    }
}
