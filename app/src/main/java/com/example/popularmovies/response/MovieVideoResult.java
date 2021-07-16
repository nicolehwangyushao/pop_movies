package com.example.popularmovies.response;

import java.util.ArrayList;

public class MovieVideoResult {
    private ArrayList<MovieVideo> results;

    public ArrayList<MovieVideo> getResults() {
        return results;
    }

    public class MovieVideo {
        String key;
        String name;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }
    }
}
