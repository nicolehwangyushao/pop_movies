package com.example.popularmovies.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieResult {

    private ArrayList<MovieData> results;
    @SerializedName("total_pages")
    private int totalPage;

    public ArrayList<MovieData> getResults() {
        return results;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public class MovieData implements Serializable {
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

        public String getTitle() {
            return title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getBackDropPath() {
            return backDropPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getVoteAverage() {
            return voteAverage;
        }

        public String getOverview() {
            return overview;
        }

    }
}
