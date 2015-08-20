package com.example.dimart.ymoney.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.dimart.ymoney.provider.CategoriesContract.CategoryEntry;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Custom content provider for categories database.
 */
public class CategoriesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CategoriesDatabase mDatabase;

    public static final int CATEGORY = 100;
    public static final int ROOT_CATEGORY = 101;

    @Override
    public boolean onCreate() {
        // Just create it for later use.
        mDatabase = new CategoriesDatabase(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CategoriesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CategoriesContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority,
                CategoriesContract.PATH_CATEGORY + "/" + CategoriesContract.PATH_ROOT,
                ROOT_CATEGORY);
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CATEGORY:
                return CategoryEntry.CONTENT_TYPE;
            case ROOT_CATEGORY:
                return CategoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public long getRootCategoryId() {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        final String SQL_QUERY_ROOT_CATEGORY = "SELECT * FROM " +
                CategoryEntry.TABLE_NAME + " WHERE " +
                CategoryEntry.COLUMN_CATEGORY_TITLE + " = '" +
                CategoryEntry.ROOT_CATEGORY_TITLE + "'";
        Cursor c = db.rawQuery(SQL_QUERY_ROOT_CATEGORY, null);
        c.moveToFirst(); // root
        long rootIdIndex = c.getLong(c.getColumnIndex(CategoryEntry._ID));
        c.close();
        return rootIdIndex;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                rowsUpdated = db.update(CategoriesContract.CategoryEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                long _id = db.insert(CategoriesContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = CategoriesContract.CategoryEntry.buildCategoryUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsDeleted;

        // if user wants to delete all rows, we delete all except the root
        if (null == selection) selection = CategoryEntry._ID + " != " + getRootCategoryId();

        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                rowsDeleted = db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CATEGORY: {
                retCursor = mDatabase.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ROOT_CATEGORY: {
                retCursor = mDatabase.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        CategoryEntry._ID + " = " + getRootCategoryId(),
                        null,
                        null,
                        null,
                        null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
}
