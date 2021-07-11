package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.popularmovies.moviesinfo.MoviePosterLoader;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(R.drawable.baseline_theaters_black_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        new MoviePosterLoader().MoviePosterImageApiCall(this);
        ImageView image = findViewById(R.id.imageView);
        String path = "/tehpKMsls621GT9WUQie2Ft6LmP.jpg";
        Picasso.get().load(getString(R.string.poster_base_url) + getString(R.string.default_size)+path).into(image);
    }
}