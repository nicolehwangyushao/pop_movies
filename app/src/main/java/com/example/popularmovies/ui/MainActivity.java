package com.example.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
    int currentPage = 1;
    int lastPage = -1;
    MoviePosterAdapter adapter;
    MovieApiClient client = new RetrofitService().getMovieApiClient();
    private FavoriteMovieViewModel mFavoriteMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        mFavoriteMovieViewModel = new ViewModelProvider(this, factory).get(FavoriteMovieViewModel.class);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        FavoriteMovieAdapter favoriteMovieAdapter = new FavoriteMovieAdapter(new FavoriteMovieAdapter.FavoriteMovieDiff(), this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mFavoriteMovieViewModel.getFavoriteMovieList().observe(this, movies -> {
            if (movies.isEmpty() && bottomNavigationView.getSelectedItemId() == R.id.favoritePage) {
                showFavoriteEmpty(recyclerView);
            } else {
                hideFavoriteEmpty(recyclerView);
            }
            favoriteMovieAdapter.submitList(movies);
        });

        //check initial bottom navigation selected item
        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.mainPage:
                execInitMovieDataTask();
                break;
            case R.id.favoritePage:
                recyclerView.clearOnScrollListeners();
                recyclerView.setAdapter(favoriteMovieAdapter);
                break;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainPage:
                    hideFavoriteEmpty(recyclerView);
                    execInitMovieDataTask();
                    break;
                case R.id.favoritePage:
                    hideNetworkError(recyclerView);
                    if (favoriteMovieAdapter.getCurrentList().isEmpty()) {
                        showFavoriteEmpty(recyclerView);
                    } else {
                        recyclerView.clearOnScrollListeners();
                        recyclerView.setAdapter(favoriteMovieAdapter);
                        if (recyclerView.getLayoutManager() == null) {
                            GridLayoutManager gridLayoutManager = getGridLayoutManager();
                            recyclerView.setLayoutManager(gridLayoutManager);
                        }

                        break;
                    }


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
        adapter = null;
    }

    private void initMovieDataTask(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
        Context context = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
        Call<MovieResult> call = client.getMoviesList(sort, currentPage);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                ArrayList<MovieResult.MovieData> movieDataArrayList = result.getResults();
                hideNetworkError(recyclerView);
                adapter = new MoviePosterAdapter(movieDataArrayList, context);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnScrollListener(getListener(gridLayoutManager));
                lastPage = result.getTotalPage();

            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                // the network call was a failure
                showNetworkError(recyclerView);
            }
        });
    }

    private void updateMovieDataTask(RecyclerView recyclerView) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(this.getString(R.string.sort_key), this.getString(R.string.sort_by_default));
        final int itemCount = 20;
        Call<MovieResult> call = client.getMoviesList(sort, currentPage);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                ArrayList<MovieResult.MovieData> movieDataArrayList = result.getResults();
                hideNetworkError(recyclerView);
                adapter.insertMovieData(movieDataArrayList);
                adapter.notifyItemRangeInserted(currentPage * itemCount - 1, itemCount);
                lastPage = result.getTotalPage();

            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                // the network call was a failure
                showNetworkError(recyclerView);
            }
        });
    }

    private void execInitMovieDataTask() {
        currentPage = 1;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.clearOnScrollListeners();
        recyclerView.clearOnChildAttachStateChangeListeners();

        if (new NetworkManager(this).isNetworkAvailable()) {
            hideNetworkError(recyclerView);
            GridLayoutManager gridLayoutManager = getGridLayoutManager();
            recyclerView.setLayoutManager(gridLayoutManager);
            initMovieDataTask(recyclerView, gridLayoutManager);

        } else {
            showNetworkError(recyclerView);
        }
    }


    private RecyclerView.OnScrollListener getListener(GridLayoutManager gridLayoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (new NetworkManager(getApplicationContext()).isNetworkAvailable()) {
                    int onScreenItem = recyclerView.getChildCount();
                    int visibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                    int totalItem = gridLayoutManager.getItemCount();
                    if (currentPage <= lastPage && (totalItem - onScreenItem) <= (visibleItem + 4)) {
                        currentPage++;
                        updateMovieDataTask(recyclerView);
                    }
                } else {
                    showNetworkError(recyclerView);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    private void hideNetworkError(RecyclerView recyclerView) {
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.networkErrorConstrainLayout).setVisibility(View.GONE);
    }

    private void showNetworkError(RecyclerView recyclerView) {
        recyclerView.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.networkErrorConstrainLayout);
        constraintLayout.setVisibility(View.VISIBLE);
    }

    private void hideFavoriteEmpty(RecyclerView recyclerView) {
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyFavorite).setVisibility(View.GONE);
    }

    private void showFavoriteEmpty(RecyclerView recyclerView) {
        hideNetworkError(recyclerView);
        recyclerView.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.emptyFavorite);
        constraintLayout.setVisibility(View.VISIBLE);
    }

}