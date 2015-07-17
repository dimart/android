package com.example.dimart.popularmoviesapp;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Dmitrii Petukhov on 7/17/15.
 */
public class Movie {

    private final String mTitle;
    private final String mOverview;
    private final float mRating;
    private final Date mReleaseDate;
    private final Uri mPosterUri;

    public static class Builder {
        private final String mTitle;
        private final String mOverview;

        private float mRating = -1;
        private Date mReleaseDate = null;
        private Uri mPosterUri = Uri.EMPTY;

        public Builder(String title, String overview) {
            mTitle = title;
            mOverview = overview;
        }

        public Builder rating(float x) {
            mRating = x;
            return this;
        }

        public Builder releaseDate(Date x) {
            mReleaseDate = x;
            return this;
        }

        public Builder posterUri(Uri x) {
            mPosterUri = x;
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
        mPosterUri = builder.mPosterUri;
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

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public Uri getPosterUri() {
        return mPosterUri;
    }
}
