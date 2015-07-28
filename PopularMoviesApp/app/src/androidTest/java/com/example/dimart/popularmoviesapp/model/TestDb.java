package com.example.dimart.popularmoviesapp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dmitrii Petukhov on 7/27/15.
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // Build a HashSet of all of the table names we wish to look for.
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.GenreEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieGenreEntry.TABLE_NAME);

        SQLiteDatabase db = new MovieDbHelper(mContext).getReadableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
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

    private void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertTrue("Column '" + columnName + "' not found. " + error, idx != -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    private ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "12 Angry Men");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Overview");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_ID, "qcL1YfkCxfhsdO6sDDJ0P");
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_ID, "qcL1YfkCxfhsdO6");
        movieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_ID, "qcL1YfkCxfhsdO6sDD");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, System.currentTimeMillis());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8.1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 42);
        return movieValues;
    }

    private ContentValues createGenreValues() {
        ContentValues genreValues = new ContentValues();
        genreValues.put(MovieContract.GenreEntry.COLUMN_GENRE_NAME, "Action");
        return  genreValues;
    }

    public void testMovieTable() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        ContentValues movieValues = createMovieValues();

        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Error: Unable to insert values in the movie table", id != -1);

        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: Unable to query the movie database for table information.",
                c.moveToFirst());

        validateCurrentRecord("Error: Movie Query Validation Failed", c, movieValues);

        assertFalse("Error: More than one record returned from movie query", c.moveToNext());

        c.close();
        db.close();
    }

    public void testGenreTable() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        ContentValues genreValues = createGenreValues();

        long id = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, genreValues);
        assertTrue("Error: Unable to insert values in the genre table", id != -1);

        Cursor c = db.query(MovieContract.GenreEntry.TABLE_NAME,
                null, null, null, null, null, null);
        assertTrue("Error: Unable to query the genre database for table information.",
                c.moveToFirst());

        validateCurrentRecord("Error: Genre Query Validation Failed", c, genreValues);

        assertFalse("Error: More than one record returned from genre query", c.moveToNext());

        c.close();
        db.close();
    }

    public void testMovieGenreTable() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        ContentValues movieValues = createMovieValues();
        ContentValues genreValues = createGenreValues();

        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Error: Unable to insert values in the movie table", movieId != -1);
        long genreId = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, genreValues);
        assertTrue("Error: Unable to insert values in the genre table", genreId != -1);

        ContentValues movieGenreValues = new ContentValues();
        movieGenreValues.put(MovieContract.MovieGenreEntry.COLUMN_MOVIE_KEY, movieId);
        movieGenreValues.put(MovieContract.MovieGenreEntry.COLUMN_GENRE_KEY, genreId);

        long movieGenreId = db.insert(MovieContract.MovieGenreEntry.TABLE_NAME, null, movieGenreValues);
        assertTrue("Error: Unable to insert values in the movie_genre table", movieGenreId != -1);

        Cursor c = db.query(MovieContract.MovieGenreEntry.TABLE_NAME,
                null, null, null, null, null, null);
        assertTrue("Error: Unable to query the movie_genre database for table information.",
                c.moveToFirst());

        validateCurrentRecord("Error: MovieGenre Query Validation Failed", c, movieGenreValues);

        assertFalse("Error: More than one record returned from movie_genre query", c.moveToNext());

        c.close();
        db.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
