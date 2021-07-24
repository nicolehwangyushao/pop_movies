package com.example.popularmovies.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.popularmovies.data.FavoriteMovieDao;
import com.example.popularmovies.data.FavoriteMovieData;

import java.util.List;

public class FavoriteMovieRepository {
    private final FavoriteMovieDao mFavoriteMovieDao;
    private final LiveData<List<FavoriteMovieData>> mFavoriteMovieList;

    public FavoriteMovieRepository(Application application) {
        FavoriteMovieDatabase db = FavoriteMovieDatabase.getDatabase(application);
        mFavoriteMovieDao = db.favoriteMovieDao();
        mFavoriteMovieList = mFavoriteMovieDao.getAll();
    }

    public LiveData<List<FavoriteMovieData>> getFavoriteMovieList() {
        return mFavoriteMovieList;
    }

    public LiveData<FavoriteMovieData> isMovieExist(int movieId) {
        return mFavoriteMovieDao.findByMovieId(movieId);
    }

    public void insert(FavoriteMovieData movieData) {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.insert(movieData);
        });
    }

    public void delete(FavoriteMovieData movieData) {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.delete(movieData);
        });
    }

    public void deleteAll() {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.deleteAllFromTable();
        });
    }
}
