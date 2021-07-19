package com.example.popularmovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteMovieDao {

    @Query("SELECT * FROM favoritemoviedata")
    LiveData<List<FavoriteMovieData>> getAll();

    @Query("SELECT * FROM favoritemoviedata WHERE movie_id LIKE :id LIMIT 1")
    LiveData<FavoriteMovieData> findByMovieId(int id);

    @Insert
    void insert(FavoriteMovieData movieData);

    @Delete
    void delete(FavoriteMovieData movieData);

    @Query("DELETE FROM favoritemoviedata")
    void deleteAllFromTable();
}
