package com.example.dimart.popularmoviesapp.model;

import android.provider.BaseColumns;

/**
 * Created by Dmitrii Petukhov on 7/26/15.
 * Defines table and column names for the movies database.
 */
public class MovieContract {

    public static final class GenreEntry implements BaseColumns {

        public static final String TABLE_NAME = "genre";

        public static final String COLUMN_GENRE_NAME = "genre_name";
    }

    public static final class MovieGenreEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_genre";

        // Column with the foreign key into the genre table.
        public static final String COLUMN_GENRE_KEY = "genre_id";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";
    }

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_TRAILER_URL = "trailer_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
    }
}
