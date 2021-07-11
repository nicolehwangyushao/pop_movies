package com.example.popularmovies.moviesinfo;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoviePosterLoader {

    public void MoviePosterImageApiCall(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String url = context.getString(R.string.base_url);
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.encodedPath(url);
            uriBuilder.appendQueryParameter("api_key", BuildConfig.API_KEY);
            uriBuilder.appendQueryParameter("sort_by", "popularity.desc");

            try {
                urlConnection = (HttpURLConnection) new URL(uriBuilder.build().toString()).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                getMovieDataFromJson(buffer.toString(), 1);

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }


            } catch (ProtocolException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });

    }

    private MovieData[] getMovieDataFromJson(String moviesJsonStr, int position)
            throws JSONException {
        final String RESULT = "results";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String OVER_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(RESULT);
        int totalResults = Integer.parseInt(moviesJson.getString("total_results"));
        int totalPage = Integer.parseInt(moviesJson.getString("total_pages"));

        MovieData[] resultData = new MovieData[totalResults/totalPage];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieInfo = movieArray.getJSONObject(i);
            String posterPath = movieInfo.getString(POSTER_PATH);
            String originalTitle = movieInfo.getString(ORIGINAL_TITLE);
            String overview = movieInfo.getString(OVERVIEW);
            String rateAverage = movieInfo.getString(OVER_AVERAGE);
            String releaseDate = movieInfo.getString(RELEASE_DATE);
            resultData[i] = new MovieData(posterPath, originalTitle, overview, rateAverage, releaseDate);
        }

        return resultData;
    }
}
