package com.example.dimart.popularmoviesapp.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Dmitrii Petukhov on 7/27/15.
 */
public class TestDb extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateDb() throws Throwable {
        // Build a HashSet of all of the table names we wish to look for.
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.GenreEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieGenreEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        assertTrue("Error: Database was created without the movie entry, genre entry and movie_genre entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query the movie database for table information.",
                c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TRAILER_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The movie database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + MovieContract.GenreEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the genre database for table information.",
                c.moveToFirst());

        final HashSet<String> genreColumnHashSet = new HashSet<>();
        genreColumnHashSet.add(MovieContract.GenreEntry._ID);
        genreColumnHashSet.add(MovieContract.GenreEntry.COLUMN_GENRE_NAME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            genreColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The genre database doesn't contain all of the required genre entry columns",
                genreColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieGenreEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the movie_genre database for table information.",
                c.moveToFirst());

        final HashSet<String> movieGenreColumnHashSet = new HashSet<>();
        movieGenreColumnHashSet.add(MovieContract.MovieGenreEntry._ID);
        movieGenreColumnHashSet.add(MovieContract.MovieGenreEntry.COLUMN_MOVIE_KEY);
        movieGenreColumnHashSet.add(MovieContract.MovieGenreEntry.COLUMN_GENRE_KEY);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieGenreColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The movie_genre database doesn't contain all of the required entry columns",
                movieGenreColumnHashSet.isEmpty());

        c.close();
        db.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
