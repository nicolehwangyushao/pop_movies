package com.example.popularmovies.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.popularmovies.data.FavoriteMovieDao;
import com.example.popularmovies.data.FavoriteMovieData;

import java.util.List;

public class FavoriteMovieRepository {
    private final FavoriteMovieDao mFavoriteMovieDao;
    private final LiveData<List<FavoriteMovieData>> mFavoriteMovieList;

    FavoriteMovieRepository(Application application) {
        FavoriteMovieDatabase db = FavoriteMovieDatabase.getDatabase(application);
        mFavoriteMovieDao = db.favoriteMovieDao();
        mFavoriteMovieList = mFavoriteMovieDao.getAll();
    }

    LiveData<List<FavoriteMovieData>> getFavoriteMovieList() {
        return mFavoriteMovieList;
    }

    LiveData<FavoriteMovieData> isMovieExist(int movieId) {
        return mFavoriteMovieDao.findByMovieId(movieId);
    }

    void insert(FavoriteMovieData movieData) {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.insert(movieData);
        });
    }

    void delete(FavoriteMovieData movieData) {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.delete(movieData);
        });
    }

    void deleteAll() {
        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
            mFavoriteMovieDao.deleteAllFromTable();
        });
    }
}
