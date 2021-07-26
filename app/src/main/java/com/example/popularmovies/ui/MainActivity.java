package com.example.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.ActivityMainBinding;
import com.example.popularmovies.repository.MovieRepository;
import com.example.popularmovies.service.NetworkManager;
import com.example.popularmovies.ui.adapter.FavoriteMovieAdapter;
import com.example.popularmovies.ui.adapter.MoviePosterAdapter;
import com.example.popularmovies.viewmodel.FavoriteMovieViewModel;
import com.example.popularmovies.viewmodel.FavoriteMovieViewModelFactory;
import com.example.popularmovies.viewmodel.MovieViewModel;
import com.example.popularmovies.viewmodel.MovieViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private static final String SAVE_MOVIE_STATE_KEY = "save_movie_state";
    private static final String SAVE_FAVORITE_STATE_KEY = "save_favorite_state";
    private String currentSort;
    private FavoriteMovieAdapter favoriteMovieAdapter;
    private MoviePosterAdapter adapter;
    private FavoriteMovieViewModel mFavoriteMovieViewModel;
    private MovieViewModel mMovieViewModel;
    private Parcelable mMovieState;
    private Parcelable mFavoriteState;
    private ActivityMainBinding binding;
    private Disposable disposable;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mFavoriteMovieViewModel = new ViewModelProvider(this, new FavoriteMovieViewModelFactory(getApplication()))
                .get(FavoriteMovieViewModel.class);
        mMovieViewModel = new ViewModelProvider(this, new MovieViewModelFactory(new MovieRepository()))
                .get(MovieViewModel.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        favoriteMovieAdapter = new FavoriteMovieAdapter(new FavoriteMovieAdapter.FavoriteMovieDiff(), this);
        adapter = new MoviePosterAdapter(new MoviePosterAdapter.MovieDiff(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getPrepend() instanceof LoadState.Error ||
                    loadStates.getAppend() instanceof LoadState.Error ||
                    loadStates.getRefresh() instanceof LoadState.Error) {
                showNetworkError();
            } else {
                hideNetworkError();
            }
            return null;
        });
        binding.recyclerView.setLayoutManager(getGridLayoutManager());
        mFavoriteMovieViewModel.getFavoriteMovieList().observe(this, movies -> {
            if (movies.isEmpty() && binding.bottomNavigation.getSelectedItemId() == R.id.favoritePage) {
                showFavoriteEmpty();
            } else {
                hideFavoriteEmpty();
            }
            favoriteMovieAdapter.submitList(movies);
        });

        //check initial bottom navigation selected item
        switch (binding.bottomNavigation.getSelectedItemId()) {
            case R.id.mainPage:
                currentSort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
                disposable = mMovieViewModel.getMovieResult(currentSort, 1).subscribe(result -> adapter.submitData(getLifecycle(), result));
                binding.recyclerView.setAdapter(adapter);
                break;
            case R.id.favoritePage:
                hideNetworkError();
                if (favoriteMovieAdapter.getCurrentList().isEmpty()) {
                    showFavoriteEmpty();
                }
                break;
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainPage:
                    mFavoriteState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
                    hideFavoriteEmpty();
                    String tempSort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
                    if (!currentSort.equals(tempSort)) {
                        disposable.dispose();
                        currentSort = tempSort;
                        disposable = mMovieViewModel.getMovieResult(currentSort, 1).subscribe(result -> adapter.submitData(getLifecycle(), result));
                    }
                    if (new NetworkManager(this).isNetworkAvailable()) {
                        hideNetworkError();
                        if (mMovieState != null) {
                            binding.recyclerView.getLayoutManager().onRestoreInstanceState(mMovieState);
                        }
                    } else {
                        showNetworkError();
                    }

                    binding.recyclerView.setAdapter(adapter);
                    break;
                case R.id.favoritePage:
                    mMovieState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
                    hideNetworkError();
                    if (favoriteMovieAdapter.getCurrentList().isEmpty()) {
                        showFavoriteEmpty();
                    } else {
                        if (mFavoriteState != null) {
                            binding.recyclerView.getLayoutManager().onRestoreInstanceState(mFavoriteState);
                        }
                    }
                    binding.recyclerView.setAdapter(favoriteMovieAdapter);
                    break;

            }
            return true;
        });

        binding.retryButton.setOnClickListener(v -> {
            String tempSort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
            if (!currentSort.equals(tempSort)) {
                currentSort = tempSort;
            }
            disposable = mMovieViewModel.getMovieResult(currentSort, 1).subscribe(
                    result -> adapter.submitData(getLifecycle(), result));
        });

    }

    private GridLayoutManager getGridLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
        return new GridLayoutManager(getApplicationContext(), spanCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tempSort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
        switch (binding.bottomNavigation.getSelectedItemId()) {
            case R.id.mainPage:
                binding.recyclerView.setAdapter(adapter);
                if (!currentSort.equals(tempSort)) {
                    disposable.dispose();
                    currentSort = tempSort;
                    disposable = mMovieViewModel.getMovieResult(currentSort, 1).subscribe(result -> adapter.submitData(getLifecycle(), result));
                } else {
                    if (mMovieState != null) {
                        binding.recyclerView.getLayoutManager().onRestoreInstanceState(mMovieState);
                    }
                }
                break;
            case R.id.favoritePage:
                binding.recyclerView.setAdapter(favoriteMovieAdapter);
                if (mFavoriteState != null) {
                    binding.recyclerView.getLayoutManager().onRestoreInstanceState(mFavoriteState);
                }
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.mainPage:
                mMovieState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
                break;
            case R.id.favoritePage:
                mFavoriteState = binding.recyclerView.getLayoutManager().onSaveInstanceState();
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable movie = savedInstanceState.getParcelable(SAVE_MOVIE_STATE_KEY);
            Parcelable favorite = savedInstanceState.getParcelable(SAVE_FAVORITE_STATE_KEY);
            if (movie != null) mMovieState = movie;
            if (favorite != null) mFavoriteState = favorite;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        switch (binding.bottomNavigation.getSelectedItemId()) {
            case R.id.mainPage:
                outState.putParcelable(SAVE_MOVIE_STATE_KEY, binding.recyclerView.getLayoutManager().onSaveInstanceState());
                outState.putParcelable(SAVE_FAVORITE_STATE_KEY, mFavoriteState);
                break;
            case R.id.favoritePage:
                outState.putParcelable(SAVE_FAVORITE_STATE_KEY, binding.recyclerView.getLayoutManager().onSaveInstanceState());
                outState.putParcelable(SAVE_MOVIE_STATE_KEY, mMovieState);
                break;
        }
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

    private void hideNetworkError() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.networkErrorConstrainLayout.setVisibility(View.GONE);
    }

    private void showNetworkError() {
        disposable.dispose();
        binding.recyclerView.setVisibility(View.GONE);
        binding.networkErrorConstrainLayout.setVisibility(View.VISIBLE);
    }

    private void hideFavoriteEmpty() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.emptyFavorite.setVisibility(View.GONE);
    }

    private void showFavoriteEmpty() {
        hideNetworkError();
        binding.recyclerView.setVisibility(View.GONE);
        binding.emptyFavorite.setVisibility(View.VISIBLE);
    }

}