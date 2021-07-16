package com.example.popularmovies;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies.response.MovieResult;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    public final static String MOVIE_DATA_KEY = "movie_poster_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MovieResult.MovieData movieSimpleData = (MovieResult.MovieData) getIntent().getSerializableExtra(MOVIE_DATA_KEY);

        String baseUrl = getString(R.string.poster_base_url);
        String backdropSize = getString(R.string.backdrop_size);
        String posterSize = getString(R.string.default_size);
        String releaseDate = getString(R.string.release_date_text, movieSimpleData.getReleaseDate());
        String rate = getString(R.string.rate_text, movieSimpleData.getVoteAverage());
        String overviewText = getString(R.string.overview_text, movieSimpleData.getOverview());

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(movieSimpleData.getTitle());
        TextView releaseTextView = findViewById(R.id.releaseDateTextView);
        releaseTextView.setText(releaseDate);
        TextView rateTextView = findViewById(R.id.rateTextView);
        rateTextView.setText(rate);
        TextView overviewTextView = findViewById(R.id.overviewTextView);
        overviewTextView.setText(overviewText);

        Picasso.get().load(baseUrl + backdropSize + movieSimpleData.getBackDropPath()).placeholder(R.drawable.placeholder).resize(1100, 350).
                into((ImageView) findViewById(R.id.backDropPosterImageView));

        Picasso.get().load(baseUrl + posterSize + movieSimpleData.getPosterPath()).placeholder(R.drawable.placeholder).resize(300, 500).
                into((ImageView) findViewById(R.id.posterImageView));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}