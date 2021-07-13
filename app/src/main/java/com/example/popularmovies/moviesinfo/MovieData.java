package com.example.popularmovies.moviesinfo;

import android.os.Parcel;
import android.os.Parcelable;

public final class MovieData implements Parcelable {
    private final String posterPath;
    private final String originalTitle;
    private final String overview;
    private final String vote_average;
    private final String release_date;


    public MovieData(String posterPath, String originalTitle, String overview, String vote_average, String release_date) {
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    protected MovieData(Parcel in) {
        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(vote_average);
        dest.writeString(release_date);
    }
}
