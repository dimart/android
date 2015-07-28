package com.example.dimart.popularmoviesapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dimart.popularmoviesapp.model.MovieContract.MovieEntry;
import com.example.dimart.popularmoviesapp.model.MovieContract.GenreEntry;
import com.example.dimart.popularmoviesapp.model.MovieContract.MovieGenreEntry;

/**
 * Created by Dmitrii Petukhov on 7/27/15.
 * Manages a local database for movies data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME
                + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +

                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +

                MovieEntry.COLUMN_POSTER_ID + " TEXT, " +
                MovieEntry.COLUMN_TRAILER_ID + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_ID + " TEXT, " +

                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RATING + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL "
                + " )";

        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME +
                " (" +
                GenreEntry._ID + " INTEGER PRIMARY KEY," +

                GenreEntry.COLUMN_GENRE_NAME + " TEXT UNIQUE NOT NULL " +
                " )";

        final String SQL_CREATE_MOVIE_GENRE_TABLE = "CREATE TABLE " + MovieGenreEntry.TABLE_NAME +
                " (" +
                MovieGenreEntry._ID + " INTEGER PRIMARY KEY," +

                MovieGenreEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieGenreEntry.COLUMN_GENRE_KEY + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + MovieGenreEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +

                " FOREIGN KEY (" + MovieGenreEntry.COLUMN_GENRE_KEY + ") REFERENCES " +
                GenreEntry.TABLE_NAME + " (" + GenreEntry._ID + ") " +
                " )";


        db.execSQL(SQL_CREATE_WEATHER_TABLE);
        db.execSQL(SQL_CREATE_GENRE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply discard the data and start over.
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieGenreEntry.TABLE_NAME);
        onCreate(db);
    }
}
