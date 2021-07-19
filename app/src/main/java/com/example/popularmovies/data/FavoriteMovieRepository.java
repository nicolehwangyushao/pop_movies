package com.example.popularmovies.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
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
//    LiveData<List<Integer>> allMovies() {
//        List<Integer> list = new ArrayList<>();
//        FavoriteMovieDatabase.databaseWriteExecutor.execute(() -> {
//            list.addAll(mFavoriteMovieDao.getAllMoviesId());
//        });
//        return list;
//    }

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
