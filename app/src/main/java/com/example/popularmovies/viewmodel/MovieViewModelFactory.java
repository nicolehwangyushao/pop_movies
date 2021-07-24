package com.example.popularmovies.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.popularmovies.repository.MovieRepository;

import org.jetbrains.annotations.NotNull;

public class MovieViewModelFactory implements ViewModelProvider.Factory {
    private static MovieRepository movieRepository;

    public MovieViewModelFactory (MovieRepository repository) {
        movieRepository = repository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieViewModel.class)) {
            return (T) new MovieViewModel(movieRepository);
        }
        return null;
    }
}
