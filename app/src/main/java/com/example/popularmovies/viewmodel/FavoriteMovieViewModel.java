package com.example.popularmovies.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.data.FavoriteMovieData;
import com.example.popularmovies.viewmodel.FavoriteMovieRepository;

import java.util.List;

public class FavoriteMovieViewModel extends AndroidViewModel {
    private final LiveData<List<FavoriteMovieData>> mFavoriteMovieList;
    private final FavoriteMovieRepository mFavoriteMovieRepository;

    public FavoriteMovieViewModel(Application application) {
        super(application);
        mFavoriteMovieRepository = new FavoriteMovieRepository(application);
        mFavoriteMovieList = mFavoriteMovieRepository.getFavoriteMovieList();
    }

    public LiveData<List<FavoriteMovieData>> getFavoriteMovieList() {
        return mFavoriteMovieList;
    }

    public LiveData<FavoriteMovieData> isMovieExist(int movieId) {
        return mFavoriteMovieRepository.isMovieExist(movieId);
    }

    public void insert(FavoriteMovieData movieData) {
        mFavoriteMovieRepository.insert(movieData);
    }

    public void delete(FavoriteMovieData movieData) {
        mFavoriteMovieRepository.delete(movieData);
    }

    public void deleteAll() {
        mFavoriteMovieRepository.deleteAll();
    }
}
