package com.example.popularmovies.service;

import com.example.popularmovies.response.MovieResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiClient {
    @GET("{sort_by}")
    Call<MovieResult> getMoviesList(@Path("sort_by") String sortBy, @Query("page") int page);
}
