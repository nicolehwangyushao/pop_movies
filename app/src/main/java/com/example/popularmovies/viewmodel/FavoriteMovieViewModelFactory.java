package com.example.popularmovies.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class FavoriteMovieViewModelFactory implements ViewModelProvider.Factory {
    static Application application;

    public FavoriteMovieViewModelFactory(Application application) {
        FavoriteMovieViewModelFactory.application = application;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavoriteMovieViewModel.class)) {
            return (T) new FavoriteMovieViewModel(application);
        }
        return null;
    }
}
