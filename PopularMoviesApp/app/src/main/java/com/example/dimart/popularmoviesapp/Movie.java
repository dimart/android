package com.example.dimart.popularmoviesapp;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dmitrii Petukhov on 7/17/15.
 */
public class Movie {

    private final String mTitle;
    private final String mOverview;
    private final float mRating;
    private final String mReleaseDate;
    private final String mPosterUrl;

    public static class Builder {
        private final String mTitle;
        private final String mOverview;

        private float mRating = -1;
        private String mReleaseDate = null;
        private String mPosterUrl = null;

        public Builder(String title, String overview) {
            mTitle = title;
            mOverview = overview;
        }

        public Builder rating(float x) {
            mRating = x;
            return this;
        }

        public Builder releaseDate(Date date) {
            mReleaseDate = new SimpleDateFormat("MMM, d yyyy").format(date);
            return this;
        }

        public Builder posterUrl(URL url) {
            mPosterUrl = url.toString();
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
    }

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
}
