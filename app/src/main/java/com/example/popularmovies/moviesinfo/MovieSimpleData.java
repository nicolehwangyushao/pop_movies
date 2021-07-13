package com.example.popularmovies.moviesinfo;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieSimpleData implements Parcelable {
    String movieId;
    String posterPath;
    public MovieSimpleData(String posterPath, String movieId) {
        this.movieId = movieId;
        this.posterPath = posterPath;
    }

    protected MovieSimpleData(Parcel in) {
        movieId = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<MovieSimpleData> CREATOR = new Creator<MovieSimpleData>() {
        @Override
        public MovieSimpleData createFromParcel(Parcel in) {
            return new MovieSimpleData(in);
        }

        @Override
        public MovieSimpleData[] newArray(int size) {
            return new MovieSimpleData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(posterPath);
    }
}
