package com.example.popularmovies.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.popularmovies.data.FavoriteMovieDao;
import com.example.popularmovies.data.FavoriteMovieData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FavoriteMovieData.class}, version = 1)
abstract class FavoriteMovieDatabase extends RoomDatabase {
    public abstract FavoriteMovieDao favoriteMovieDao();

    private static final int NUMBER_OF_THREADS = 4;
    final static ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile FavoriteMovieDatabase INSTANCE;

    static FavoriteMovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavoriteMovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoriteMovieDatabase.class, "favorite_movie_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
