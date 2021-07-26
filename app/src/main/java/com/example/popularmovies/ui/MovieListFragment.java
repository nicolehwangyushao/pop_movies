package com.example.popularmovies.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.FragmentMovieListBinding;
import com.example.popularmovies.repository.MovieRepository;
import com.example.popularmovies.ui.adapter.MoviePosterAdapter;
import com.example.popularmovies.viewmodel.MovieViewModel;
import com.example.popularmovies.viewmodel.MovieViewModelFactory;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.disposables.Disposable;

public class MovieListFragment extends Fragment {

    private final static String TAG = MovieListFragment.class.getSimpleName();
    private FragmentMovieListBinding binding;
    private Disposable disposable;
    private String sort;
    private MoviePosterAdapter adapter;
    private MovieViewModel movieViewModel;

    public static String getTAG() {
        return TAG;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new MoviePosterAdapter(new MoviePosterAdapter.MovieDiff(), getContext());
        movieViewModel = new ViewModelProvider(this, new MovieViewModelFactory(new MovieRepository()))
                .get(MovieViewModel.class);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getPrepend() instanceof LoadState.Error ||
                    loadStates.getAppend() instanceof LoadState.Error ||
                    loadStates.getRefresh() instanceof LoadState.Error) {
                showNetworkError();
            } else {
                hideNetworkError();
            }
            return null;
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
        disposable = movieViewModel.getMovieResult(sort, 1).subscribe(result -> adapter.submitData(getLifecycle(), result));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(getGridLayoutManager());
        binding.retryButton.setOnClickListener(v -> {
            String newSort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
            disposable = movieViewModel.getMovieResult(newSort, 1).subscribe(
                    result -> adapter.submitData(getLifecycle(), result));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String newSort = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_by_default));
        if (newSort != sort) {
            sort = newSort;
            disposable = movieViewModel.getMovieResult(sort, 1).subscribe(result -> adapter.submitData(getLifecycle(), result));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private GridLayoutManager getGridLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
        return new GridLayoutManager(getContext(), spanCount);
    }

    private void hideNetworkError() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.networkErrorConstrainLayout.setVisibility(View.GONE);
    }

    private void showNetworkError() {
        disposable.dispose();
        binding.recyclerView.setVisibility(View.GONE);
        binding.networkErrorConstrainLayout.setVisibility(View.VISIBLE);
    }
}
