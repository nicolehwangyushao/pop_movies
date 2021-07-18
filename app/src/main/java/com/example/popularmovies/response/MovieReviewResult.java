package com.example.popularmovies.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieReviewResult {
    private ArrayList<MovieReviewResult.MovieReview> results;
    @SerializedName("total_pages")
    private int totalPage;

    public ArrayList<MovieReviewResult.MovieReview> getResults() {
        return results;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public class MovieReview {
        String author;
        String content;
        @SerializedName("created_at")
        String createdDate;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getCreatedDate() {
            return createdDate;
        }
    }
}
