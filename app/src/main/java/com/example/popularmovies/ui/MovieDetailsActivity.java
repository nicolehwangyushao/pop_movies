package com.example.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.response.MovieReviewResult;
import com.example.popularmovies.response.MovieVideoResult;
import com.example.popularmovies.service.MovieApiClient;
import com.example.popularmovies.service.RetrofitService;
import com.example.popularmovies.ui.adapter.MovieReviewAdapter;
import com.example.popularmovies.ui.adapter.MovieVideoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    public final static String MOVIE_DATA_KEY = "movie_poster_key";
    MovieApiClient client = new RetrofitService().getMovieApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MovieResult.MovieData movieData = (MovieResult.MovieData) getIntent().getSerializableExtra(MOVIE_DATA_KEY);

        String baseUrl = getString(R.string.poster_base_url);
        String backdropSize = getString(R.string.backdrop_size);
        String posterSize = getString(R.string.default_size);
        String releaseDate = getString(R.string.release_date_text, movieData.getReleaseDate());
        String rate = getString(R.string.rate_text, movieData.getVoteAverage());
        String overviewText = getString(R.string.overview_text, movieData.getOverview());

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(movieData.getTitle());
        TextView releaseTextView = findViewById(R.id.releaseDateTextView);
        releaseTextView.setText(releaseDate);
        TextView rateTextView = findViewById(R.id.rateTextView);
        rateTextView.setText(rate);
        TextView overviewTextView = findViewById(R.id.overviewTextView);
        overviewTextView.setText(overviewText);
        RecyclerView videoRecyclerView = findViewById(R.id.videoRecyclerView);
        RecyclerView reviewRecyclerView = findViewById(R.id.reviewsRecyclerView);

        Picasso.get().load(baseUrl + backdropSize + movieData.getBackDropPath()).placeholder(R.drawable.placeholder).resize(1100, 350).
                into((ImageView) findViewById(R.id.backDropPosterImageView));

        Picasso.get().load(baseUrl + posterSize + movieData.getPosterPath()).placeholder(R.drawable.placeholder).resize(300, 500).
                into((ImageView) findViewById(R.id.posterImageView));

        getMovieVideo(movieData.getMovieId(), videoRecyclerView);
        getMovieReview(movieData.getMovieId(), reviewRecyclerView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }
        return false;
    }

    private void getMovieVideo(int movieId, RecyclerView recyclerView) {
        Context context = this;
        Call<MovieVideoResult> call = client.getMovieVideo(movieId);
        call.enqueue(new Callback<MovieVideoResult>() {
            @Override
            public void onResponse(Call<MovieVideoResult> call, Response<MovieVideoResult> response) {
                ArrayList<MovieVideoResult.MovieVideo> movieVideos = response.body().getResults();
                MovieVideoAdapter videoAdapter = new MovieVideoAdapter(movieVideos, context);
                recyclerView.setAdapter(videoAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onFailure(Call<MovieVideoResult> call, Throwable t) {

            }

        });
    }

    private void getMovieReview(int movieId, RecyclerView recyclerView) {
        Context context = this;
        int currentPage = 1;
        Call<MovieReviewResult> call = client.getMovieReview(movieId, currentPage);
        call.enqueue(new Callback<MovieReviewResult>() {
            @Override
            public void onResponse(Call<MovieReviewResult> call, Response<MovieReviewResult> response) {
                MovieReviewResult result = response.body();
                int lastPage = result.getTotalPage();
                ArrayList<MovieReviewResult.MovieReview> movieReviews = result.getResults();
                MovieReviewAdapter reviewAdapter = new MovieReviewAdapter(movieReviews);
                recyclerView.setAdapter(reviewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                if(lastPage > currentPage) {
                    getMovieReview(movieId, reviewAdapter, lastPage, currentPage);
                }
            }

            @Override
            public void onFailure(Call<MovieReviewResult> call, Throwable t) {

            }
        });
    }

    private void getMovieReview(int movieId, MovieReviewAdapter adapter, int lastPage, int currentPage) {
        if (lastPage > currentPage) {
            Call<MovieReviewResult> call = client.getMovieReview(movieId, currentPage);
            int nextPage = currentPage + 1;
            call.enqueue(new Callback<MovieReviewResult>() {
                @Override
                public void onResponse(Call<MovieReviewResult> call, Response<MovieReviewResult> response) {
                    MovieReviewResult result = response.body();
                    int lastPage = result.getTotalPage();
                    ArrayList<MovieReviewResult.MovieReview> movieReviews = result.getResults();
                    adapter.insertMovieData(movieReviews);
                    getMovieReview(movieId, adapter, lastPage, nextPage);
                }

                @Override
                public void onFailure(Call<MovieReviewResult> call, Throwable t) {

                }

            });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}