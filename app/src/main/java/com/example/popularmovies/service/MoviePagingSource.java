package com.example.popularmovies.service;

import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.popularmovies.response.MovieResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, MovieResult.MovieData> {

    private static final int STARTING_PAGE_INDEX = 1;
    private static String sortBy;
    private final RetrofitService retrofitService = new RetrofitService();

    public MoviePagingSource(String sort) {
        sortBy = sort;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, MovieResult.MovieData> pagingState) {
        Integer refreshKey;
        Integer anchorPosition = pagingState.getAnchorPosition();
        if (anchorPosition != null) {
            refreshKey = pagingState.closestPageToPosition(anchorPosition).getPrevKey();
            if (refreshKey != null) return refreshKey + 1;
            refreshKey = pagingState.closestPageToPosition(anchorPosition).getNextKey();
            if (refreshKey != null) return refreshKey - 1;
        }
        return null;
    }


    @NotNull
    @Override
    public Single<LoadResult<Integer, MovieResult.MovieData>> loadSingle(@NotNull LoadParams<Integer> loadParams) {
        try {
            // If page number is already there then init page variable with it otherwise we are loading fist page
            int page = null != loadParams.getKey() ? loadParams.getKey() : STARTING_PAGE_INDEX;
            return retrofitService.getMovieApiClient()
                    .getMoviesList(sortBy, page)
                    .subscribeOn(Schedulers.io())
                    .map(MovieResult::getResults)
                    .map(results -> toLoadResult(results, page))
                    .onErrorReturn(e -> new LoadResult.Error((Throwable) e));
        } catch (Exception e) {
            return Single.just(new LoadResult.Error(e));
        }
    }

    private LoadResult<Integer, MovieResult.MovieData> toLoadResult(List<MovieResult.MovieData> movies, int page) {
        return new LoadResult.Page(movies, page == STARTING_PAGE_INDEX ? null : page - 1, page + 1);
    }
}
