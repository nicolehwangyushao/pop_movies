package com.example.popularmovies.response;

import java.util.ArrayList;
import java.util.List;

public class MovieVideoResult {
    private List<MovieVideo> results;

    public List<MovieVideo> getResults() {
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
