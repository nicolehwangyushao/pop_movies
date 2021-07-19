package com.example.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
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
import com.example.popularmovies.ui.adapter.MoviePosterAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_FAVORITE_MOVIE_ACTIVITY_REQUEST_CODE = 1;
    int currentPage = 1;
    MoviePosterAdapter adapter;
    int lastPage = -1;
    MovieApiClient client = new RetrofitService().getMovieApiClient();
    private FavoriteMovieViewModel mFavoriteMovieViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        mFavoriteMovieViewModel = new ViewModelProvider(this, factory).get(FavoriteMovieViewModel.class);
//        mFavoriteMovieViewModel.deleteAll();
        mFavoriteMovieViewModel.getFavoriteMovieList().observe(this, movie -> {
            movie.forEach(data -> System.out.println(data.getTitle() + data.getMovieId()));
        });
//        System.out.println(mFavoriteMovieViewModel.isMovieExist(459151));
        execInitMovieDataTask();
        Button retry = findViewById(R.id.retryButton);
        retry.setOnClickListener(v -> execInitMovieDataTask());

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == NEW_FAVORITE_MOVIE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            MovieResult.MovieData movieData = (MovieResult.MovieData) data.getSerializableExtra(MovieDetailsActivity.MOVIE_DATA_KEY);
//
//            FavoriteMovieData favoriteMovieData = new FavoriteMovieData(movieData.getMovieId(), movieData.getPosterPath(), movieData.getBackDropPath(),
//                    movieData.getTitle(), movieData.getOverview(), movieData.getVoteAverage(), movieData.getReleaseDate());
//            Toast.makeText(
//                    getApplicationContext(),
//                    String.valueOf(mFavoriteMovieViewModel.isMovieExist(movieData.getMovieId())),
//                    Toast.LENGTH_LONG).show();
//            mFavoriteMovieViewModel.insert(favoriteMovieData);
//            System.out.println(mFavoriteMovieViewModel.isMovieExist(movieData.getMovieId()));
//        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "noo",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

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
    protected void onResume() {
        super.onResume();
        execInitMovieDataTask();

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
                adapter = new MoviePosterAdapter(movieDataArrayList, context, mFavoriteMovieViewModel);
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

            int orientation = getResources().getConfiguration().orientation;
            int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
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


}