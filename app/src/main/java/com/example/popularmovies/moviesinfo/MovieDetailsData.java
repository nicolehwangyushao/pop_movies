package com.example.popularmovies.moviesinfo;

import java.io.Serializable;

public class MovieDetailsData implements Serializable {
    public String movieId;
    public String posterPath;
    public String backDropPath;
    public String title;
    public String overView;
    public String voteAverage;
    public String releaseDate;

    public MovieDetailsData(String posterPath, String movieId, String title, String overView,
                            String voteAverage, String releaseDate, String backDropPath) {
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.title = title;
        this.overView = overView;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backDropPath = backDropPath;
    }


}
