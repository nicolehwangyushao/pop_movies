package com.example.popularmovies.repository;

import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.popularmovies.response.MovieResult;
import com.example.popularmovies.service.MoviePagingSource;
import com.example.popularmovies.viewmodel.MovieViewModel;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class MovieRepository {
    private static final int PAGE_SIZE = 20;
    private Flowable<PagingData<MovieResult.MovieData>> pagingDataFlow;

    public Flowable<PagingData<MovieResult.MovieData>> getResultMovieData(String sort) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(new MovieViewModel(this));
        PagingConfig pagingConfig = new PagingConfig(PAGE_SIZE, PAGE_SIZE * 3);
        Pager<Integer, MovieResult.MovieData> pager = new Pager<>(pagingConfig, () -> new MoviePagingSource(sort));
        pagingDataFlow = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(pagingDataFlow, viewModelScope);
        return pagingDataFlow;
    }

}
