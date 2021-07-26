package com.example.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MovieListFragment movieListFragment = new MovieListFragment();
        FavoriteMovieListFragment favoriteMovieListFragment = new FavoriteMovieListFragment();
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .add(R.id.fragmentContainerView, movieListFragment, MovieListFragment.getTAG()).commit();


        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainPage:
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, movieListFragment, movieListFragment.getTag())
                            .addToBackStack(null).commit();
                    break;
                case R.id.favoritePage:
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, favoriteMovieListFragment, favoriteMovieListFragment.getTag())
                            .addToBackStack(null).commit();
                    break;

            }
            return true;
        });


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

}