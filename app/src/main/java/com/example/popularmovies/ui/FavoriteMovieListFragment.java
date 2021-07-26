package com.example.popularmovies.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.popularmovies.databinding.FragmentFavoriteMovieListBinding;
import com.example.popularmovies.ui.adapter.FavoriteMovieAdapter;
import com.example.popularmovies.viewmodel.FavoriteMovieViewModel;
import com.example.popularmovies.viewmodel.FavoriteMovieViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class FavoriteMovieListFragment extends Fragment {
    private final static String TAG = FavoriteMovieListFragment.class.getSimpleName();
    private FragmentFavoriteMovieListBinding binding;

    public static String getTAG() {
        return TAG;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteMovieListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FavoriteMovieAdapter favoriteMovieAdapter = new FavoriteMovieAdapter(new FavoriteMovieAdapter.FavoriteMovieDiff(), getContext());
        FavoriteMovieViewModel favoriteMovieViewModel = new ViewModelProvider(this, new FavoriteMovieViewModelFactory(getActivity().getApplication()))
                .get(FavoriteMovieViewModel.class);
        favoriteMovieViewModel.getFavoriteMovieList().observe(getViewLifecycleOwner(), movies -> {
            if (movies.isEmpty()) {
                showFavoriteEmpty();
            } else {
                hideFavoriteEmpty();
            }
            favoriteMovieAdapter.submitList(movies);
        });
        binding.recyclerView.setLayoutManager(getGridLayoutManager());
        binding.recyclerView.setAdapter(favoriteMovieAdapter);
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

    private void hideFavoriteEmpty() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.emptyFavorite.setVisibility(View.GONE);
    }

    private void showFavoriteEmpty() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.emptyFavorite.setVisibility(View.VISIBLE);
    }
}
