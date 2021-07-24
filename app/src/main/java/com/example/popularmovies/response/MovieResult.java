package com.example.popularmovies.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MovieResult {

    private List<MovieData> results;
    @SerializedName("total_pages")
    private int totalPage;


    public int getTotalPage() {
        return totalPage;
    }

    public void setResults(List<MovieData> results) {
        this.results = results;
    }

    public List<MovieData> getResults() {
        return results;
    }


    public static class MovieData implements Serializable {
        @SerializedName("id")
        private int movieId;
        @SerializedName("poster_path")
        private String posterPath;
        @SerializedName("backdrop_path")
        private String backDropPath;
        @SerializedName("original_title")
        private String title;
        @SerializedName("overview")
        private String overview;
        @SerializedName("vote_average")
        private String voteAverage;
        @SerializedName("release_date")
        private String releaseDate;

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public String getBackDropPath() {
            return backDropPath;
        }

        public void setBackDropPath(String backDropPath) {
            this.backDropPath = backDropPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(String voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

    }
}
