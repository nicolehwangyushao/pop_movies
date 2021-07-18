package com.example.popularmovies.service;

import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.response.MovieReviewResult;
import com.example.popularmovies.response.MovieVideoResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiClient {
    @GET("{sort}")
    Call<MovieResult> getMoviesList(@Path("sort") String sortBy, @Query("page") int page);

    @GET("{movieId}/videos")
    Call<MovieVideoResult> getMovieVideo(@Path("movieId") int movieId);

    @GET("{movieId}/reviews")
    Call<MovieReviewResult> getMovieReview(@Path("movieId") int movieId, @Query("page") int page);
}
