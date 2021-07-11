package com.example.popularmovies.moviesinfo;

import java.io.Serializable;

public final class MovieData implements Serializable {
    private final String posterPath;
    private final String originalTitle;
    private final String overview;
    private final String rate_average;
    private final String release_date;

    public MovieData(String posterPath, String originalTitle, String overview, String rate_average, String release_date) {
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.rate_average = rate_average;
        this.release_date = release_date;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getRate_average() {
        return rate_average;
    }

    public String getRelease_date() {
        return release_date;
    }
}
