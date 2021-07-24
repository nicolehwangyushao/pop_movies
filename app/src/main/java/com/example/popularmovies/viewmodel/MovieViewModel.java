package com.example.popularmovies.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.popularmovies.repository.MovieRepository;
import com.example.popularmovies.response.MovieResult;

import io.reactivex.rxjava3.core.Flowable;

public class MovieViewModel extends ViewModel {
    private static MovieRepository movieRepository;
    private static String currentSort = null;
    private static Flowable<PagingData<MovieResult.MovieData>> currentResult = null;

    public MovieViewModel(MovieRepository repository) {
        movieRepository = repository;
    }

    public Flowable<PagingData<MovieResult.MovieData>> getMovieResult(String sort) {
        Flowable<PagingData<MovieResult.MovieData>> lastResult = currentResult;
        if (currentSort != null && currentSort.equals(sort) && lastResult != null) {
            return lastResult;
        }
        currentSort = sort;
        Flowable<PagingData<MovieResult.MovieData>> newResult = movieRepository.getResultMovieData(sort);
        currentResult = newResult;
        return newResult;
    }

}
