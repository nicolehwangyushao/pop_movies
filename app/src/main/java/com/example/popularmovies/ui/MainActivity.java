package com.example.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.data.FavoriteMovieViewModel;
import com.example.popularmovies.data.ViewModelFactory;
import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.service.MovieApiClient;
import com.example.popularmovies.service.NetworkManager;
import com.example.popularmovies.service.RetrofitService;
import com.example.popularmovies.ui.adapter.FavoriteMovieAdapter;
import com.example.popularmovies.ui.adapter.MoviePosterAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String SAVE_MOVIE_STATE_KEY = "save_movie_state";
    private static final String SAVE_FAVORITE_STATE_KEY = "save_favorite_state";
    private static final String SAVE_MOVIE_PAGE_KEY = "save_movie_page";
    private int currentPage = 1;
    private int lastPage = -1;
    private FavoriteMovieAdapter favoriteMovieAdapter;
    private MoviePosterAdapter adapter;
    private final MovieApiClient client = new RetrofitService().getMovieApiClient();
    private RecyclerView recyclerView;
    private GridLayoutManager favoriteGridLayoutManager;
    private GridLayoutManager movieGridLayoutManager;
    private String sort;
    private FavoriteMovieViewModel mFavoriteMovieViewModel;
    private Parcelable mMovieState;
    private Parcelable mFavoriteState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        mFavoriteMovieViewModel = new ViewModelProvider(this, factory).get(FavoriteMovieViewModel.class);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        favoriteMovieAdapter = new FavoriteMovieAdapter(new FavoriteMovieAdapter.FavoriteMovieDiff(), this);
        favoriteGridLayoutManager = getGridLayoutManager();
        movieGridLayoutManager = getGridLayoutManager();
        recyclerView = findViewById(R.id.recyclerView);
        mFavoriteMovieViewModel.getFavoriteMovieList().observe(this, movies -> {
            if (movies.isEmpty() && bottomNavigationView.getSelectedItemId() == R.id.favoritePage) {
                showFavoriteEmpty();
            } else {
                hideFavoriteEmpty();
            }
            favoriteMovieAdapter.submitList(movies);
        });

        //check initial bottom navigation selected item
        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.mainPage:
                recyclerView.setLayoutManager(movieGridLayoutManager);
                break;
            case R.id.favoritePage:
                hideNetworkError();
                if (favoriteMovieAdapter.getCurrentList().isEmpty()) {
                    showFavoriteEmpty();
                }
                recyclerView.setLayoutManager(favoriteGridLayoutManager);
                break;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainPage:
                    mFavoriteState = favoriteGridLayoutManager.onSaveInstanceState();
                    hideFavoriteEmpty();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String currentSort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
                    if (!currentSort.equals(sort)) {
                        sort = currentSort;
                        execInitMovieDataTask();
                    } else {
                        if (mMovieState != null) {
                            recyclerView.setLayoutManager(movieGridLayoutManager);
                            movieGridLayoutManager.onRestoreInstanceState(mMovieState);
                        }
                    }

                    recyclerView.addOnScrollListener(getListener());
                    recyclerView.setAdapter(adapter);
                    break;
                case R.id.favoritePage:
                    mMovieState = movieGridLayoutManager.onSaveInstanceState();
                    hideNetworkError();
                    if (favoriteMovieAdapter.getCurrentList().isEmpty()) {
                        showFavoriteEmpty();
                    } else {
                        if (mFavoriteState != null) {
                            recyclerView.setLayoutManager(favoriteGridLayoutManager);
                            favoriteGridLayoutManager.onRestoreInstanceState(mFavoriteState);
                        }
                    }
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setAdapter(favoriteMovieAdapter);
                    break;


            }
            return true;
        });


        Button retry = findViewById(R.id.retryButton);
        retry.setOnClickListener(v -> execInitMovieDataTask());

    }

    @NotNull
    private GridLayoutManager getGridLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
        return new GridLayoutManager(getApplicationContext(), spanCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String currentSort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.mainPage:
                if (!currentSort.equals(sort)) {
                    sort = currentSort;
                    execInitMovieDataTask();
                    mMovieState = null;
                } else {
                    if (mMovieState != null) {
                        movieGridLayoutManager.onRestoreInstanceState(mMovieState);
                        recyclerView.setLayoutManager(movieGridLayoutManager);
                    }
                }
                break;
            case R.id.favoritePage:
                recyclerView.setAdapter(favoriteMovieAdapter);
                if (mFavoriteState != null) {
                    favoriteGridLayoutManager.onRestoreInstanceState(mFavoriteState);
                    recyclerView.setLayoutManager(favoriteGridLayoutManager);
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
                mMovieState = movieGridLayoutManager.onSaveInstanceState();
                break;
            case R.id.favoritePage:
                mFavoriteState = favoriteGridLayoutManager.onSaveInstanceState();
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mMovieState = savedInstanceState.getParcelable(SAVE_MOVIE_STATE_KEY);
            mFavoriteState = savedInstanceState.getParcelable(SAVE_FAVORITE_STATE_KEY);
            int savePage = savedInstanceState.getInt(SAVE_MOVIE_PAGE_KEY);
//            if (currentPage < savePage ) {
//                for (int i = currentPage + 1; i <= savePage; i++){
//                    updateMovieDataTask(i);
//                }
//            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_MOVIE_STATE_KEY, movieGridLayoutManager.onSaveInstanceState());
        outState.putParcelable(SAVE_FAVORITE_STATE_KEY, favoriteGridLayoutManager.onSaveInstanceState());
        outState.putInt(SAVE_MOVIE_PAGE_KEY, currentPage);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initMovieDataTask() {
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
        Call<MovieResult> call = client.getMoviesList(sort, currentPage);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                ArrayList<MovieResult.MovieData> movieDataArrayList = result.getResults();
                hideNetworkError();
                adapter = new MoviePosterAdapter(movieDataArrayList, context);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnScrollListener(getListener());
                lastPage = result.getTotalPage();

            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                // the network call was a failure
                showNetworkError();
            }
        });
    }

    private void updateMovieDataTask(int page) {

        final int itemCount = 20;
        Call<MovieResult> call = client.getMoviesList(sort, page);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                ArrayList<MovieResult.MovieData> movieDataArrayList = result.getResults();
                hideNetworkError();
                adapter.insertMovieData(movieDataArrayList);
                adapter.notifyItemRangeInserted(page * itemCount - 1, itemCount);
                currentPage = page;
                lastPage = result.getTotalPage();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                // the network call was a failure
                showNetworkError();
            }
        });
    }

    private void execInitMovieDataTask() {
        currentPage = 1;
        recyclerView.clearOnScrollListeners();
        recyclerView.clearOnChildAttachStateChangeListeners();

        if (new NetworkManager(this).isNetworkAvailable()) {
            hideNetworkError();
//            recyclerView.setLayoutManager(movieGridLayoutManager);
            initMovieDataTask();

        } else {
            showNetworkError();
        }
    }


    private RecyclerView.OnScrollListener getListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (new NetworkManager(getApplicationContext()).isNetworkAvailable()) {
                    int onScreenItem = recyclerView.getChildCount();
                    int visibleItem = movieGridLayoutManager.findFirstVisibleItemPosition();
                    int totalItem = movieGridLayoutManager.getItemCount();
                    if (currentPage <= lastPage && (totalItem - onScreenItem) <= (visibleItem + 4)) {
                        int getPage = currentPage + 1;
                        updateMovieDataTask(getPage);
                    }
                } else {
                    showNetworkError();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    private void hideNetworkError() {
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.networkErrorConstrainLayout).setVisibility(View.GONE);
    }

    private void showNetworkError() {
        recyclerView.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.networkErrorConstrainLayout);
        constraintLayout.setVisibility(View.VISIBLE);
    }

    private void hideFavoriteEmpty() {
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyFavorite).setVisibility(View.GONE);
    }

    private void showFavoriteEmpty() {
        hideNetworkError();
        recyclerView.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.emptyFavorite);
        constraintLayout.setVisibility(View.VISIBLE);
    }

}