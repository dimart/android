package com.example.dimart.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dmitrii Petukhov on 7/17/15.
 */
public class Movie implements Parcelable {

    private final String mTitle;
    private final String mOverview;
    private final float mRating;
    private final String mReleaseDate;
    private final String mPosterUrl;
    private final String mBackdropUrl;

    public static class Builder {
        private final String mTitle;
        private final String mOverview;

        private float mRating = -1;
        private String mReleaseDate = null;
        private String mPosterUrl = null;
        private String mBackdropUrl = null;

        public Builder(String title, String overview) {
            mTitle = title;
            mOverview = overview;
        }

        public Builder rating(float x) {
            mRating = x;
            return this;
        }

        public Builder releaseDate(Date date) {
            mReleaseDate = new SimpleDateFormat("MMMM d, yyyy").format(date);
            return this;
        }

        public Builder posterUrl(URL url) {
            if (url != null)
                mPosterUrl = url.toString();
            return this;
        }

        public Builder backdropUrl(URL url) {
            if (url != null)
                mBackdropUrl = url.toString();
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    private Movie(Builder builder) {
        mTitle = builder.mTitle;
        mOverview = builder.mOverview;
        mRating = builder.mRating;
        mReleaseDate = builder.mReleaseDate;
        mPosterUrl = builder.mPosterUrl;
        mBackdropUrl = builder.mBackdropUrl;
    }

    private Movie(Parcel in) {
        // FIFO.
        mTitle = in.readString();
        mOverview = in.readString();
        mRating = in.readFloat();
        mReleaseDate = in.readString();
        mPosterUrl = in.readString();
        mBackdropUrl = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // FIFO.
        out.writeString(mTitle);
        out.writeString(mOverview);
        out.writeFloat(mRating);
        out.writeString(mReleaseDate);
        out.writeString(mPosterUrl);
        out.writeString(mBackdropUrl);
    }

    // This is used to regenerate Movie object.
    // All Parcelables must have a CREATOR that implements these two methods.
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public float getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getBackdropUrl() {
        return mBackdropUrl;
    }
}
