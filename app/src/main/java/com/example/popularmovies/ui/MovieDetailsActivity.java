package com.example.popularmovies.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies.R;
import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.response.MovieVideoResult;
import com.example.popularmovies.service.MovieApiClient;
import com.example.popularmovies.service.RetrofitService;
import com.squareup.picasso.Picasso;

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

        Picasso.get().load(baseUrl + backdropSize + movieData.getBackDropPath()).placeholder(R.drawable.placeholder).resize(1100, 350).
                into((ImageView) findViewById(R.id.backDropPosterImageView));

        Picasso.get().load(baseUrl + posterSize + movieData.getPosterPath()).placeholder(R.drawable.placeholder).resize(300, 500).
                into((ImageView) findViewById(R.id.posterImageView));

        getMovieVideo(movieData.getMovieId());


    }

    private void getMovieVideo(int movieId) {

        Call<MovieVideoResult> call = client.getMovieVideo(movieId);
        call.enqueue(new Callback<MovieVideoResult>() {
            @Override
            public void onResponse(Call<MovieVideoResult> call, Response<MovieVideoResult> response) {
                System.out.println(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MovieVideoResult> call, Throwable t) {

            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}