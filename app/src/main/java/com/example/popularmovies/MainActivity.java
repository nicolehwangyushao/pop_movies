package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.moviesinfo.MovieDetailsData;
import com.example.popularmovies.moviesinfo.MoviePosterAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    int currentPage = 1;
    static int lastPage = -1;
    MoviePosterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        execMovieDataTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        execMovieDataTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter = null;
        executor.shutdown();
    }

    private void execMovieDataTask() {
        currentPage = 1;
        MoviePosterLoader.MovieDataTask initDataTask = new MoviePosterLoader.MovieDataTask(this, currentPage);
        Future<ArrayList<MovieDetailsData>> arrayList = executor.submit(initDataTask);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.clearOnScrollListeners();
        recyclerView.clearOnChildAttachStateChangeListeners();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        try {
            ArrayList<MovieDetailsData> movieDataArrayList = (ArrayList<MovieDetailsData>) arrayList.get();
            adapter = new MoviePosterAdapter(movieDataArrayList, this);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(getListener(gridLayoutManager));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private RecyclerView.OnScrollListener getListener(GridLayoutManager gridLayoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int onScreenItem = recyclerView.getChildCount();
                int visibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                int totalItem = gridLayoutManager.getItemCount();
                if (currentPage <= lastPage && (totalItem - onScreenItem) <= (visibleItem + 4)) {
                    currentPage++;
                    Future<ArrayList<MovieDetailsData>> arrayListFuture = executor.submit(new MoviePosterLoader.MovieDataTask(getApplicationContext(), currentPage));
                    try {
                        adapter.insertMovieData((ArrayList<MovieDetailsData>) arrayListFuture.get());
                        adapter.notifyItemRangeInserted(currentPage * 20 - 1, 20);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
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


    public static class MoviePosterLoader {

        public static class MovieDataTask implements Callable<ArrayList<MovieDetailsData>> {
            private Context context;
            private int page;

            public MovieDataTask(Context context, int page) {
                this.context = context;
                this.page = page;
            }


            @Override
            public ArrayList<MovieDetailsData> call() {
                ArrayList<MovieDetailsData> movieDataArrayList = new ArrayList<>();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String sort = sharedPreferences.getString(context.getString(R.string.sort_key), context.getString(R.string.sort_by_default));
                String baseUrl = context.getString(R.string.movie_base_url, sort);
                apiRequest(baseUrl, page, movieDataArrayList);
                return movieDataArrayList;
            }


            private void apiRequest(String baseUrl, int page, ArrayList<MovieDetailsData> movieDataArrayList) {
                HttpURLConnection urlConnection;
                BufferedReader reader;
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.encodedPath(baseUrl);
                uriBuilder.appendQueryParameter("api_key", BuildConfig.API_KEY);
                uriBuilder.appendQueryParameter("page", String.valueOf(page));
                try {
                    urlConnection = (HttpURLConnection) new URL(uriBuilder.build().toString()).openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    urlConnection.disconnect();
                    reader.close();
                    getSimpleMovieDataFromJson(buffer.toString(), movieDataArrayList);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }


            private static void getSimpleMovieDataFromJson(String moviesJsonStr, ArrayList<MovieDetailsData> movieDataArrayList) throws JSONException {
                final String RESULT = "results";
                final String POSTER_PATH = "poster_path";
                final String BACK_DROP_PATH = "backdrop_path";
                final String ORIGINAL_TITLE = "original_title";
                final String OVERVIEW = "overview";
                final String OVER_AVERAGE = "vote_average";
                final String RELEASE_DATE = "release_date";
                final String MOVIE_ID = "id";
                JSONObject moviesJson = new JSONObject(moviesJsonStr);
                JSONArray movieArray = moviesJson.getJSONArray(RESULT);
                lastPage = Integer.parseInt(moviesJson.getString("total_pages"));


                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieInfo = movieArray.getJSONObject(i);
                    String posterPath = movieInfo.getString(POSTER_PATH);
                    String backDropPath = movieInfo.getString(BACK_DROP_PATH);
                    String movieId = movieInfo.getString(MOVIE_ID);
                    String title = movieInfo.getString(ORIGINAL_TITLE);
                    String overview = movieInfo.getString(OVERVIEW);
                    String rateAverage = movieInfo.getString(OVER_AVERAGE);
                    String releaseDate = movieInfo.has(RELEASE_DATE) ? movieInfo.getString(RELEASE_DATE) : "UNKNOWN";

                    movieDataArrayList.add(new MovieDetailsData(posterPath, movieId, title, overview, rateAverage, releaseDate, backDropPath));
                }

            }
        }
    }
}