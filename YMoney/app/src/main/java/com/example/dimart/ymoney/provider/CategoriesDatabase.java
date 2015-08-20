package com.example.dimart.ymoney.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dimart.ymoney.provider.CategoriesContract.CategoryEntry;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Manages a local database for categories data.
 */
public class CategoriesDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Categories.db";

    public CategoriesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                CategoryEntry.COLUMN_CATEGORY_ID + " INTEGER, " +
                CategoryEntry.COLUMN_CATEGORY_TITLE + " TEXT NOT NULL, " +

                CategoryEntry.COLUMN_PARENT_ID + " INTEGER, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + CategoryEntry.COLUMN_PARENT_ID + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "), " +

                // Make sure that the app have just one category entry per category id per title.
                // It's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + CategoryEntry.COLUMN_CATEGORY_ID + ", " +
                CategoryEntry.COLUMN_CATEGORY_TITLE + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ROOT_CATEGORY = "INSERT INTO " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry.COLUMN_CATEGORY_TITLE + ", " +
                CategoryEntry.COLUMN_PARENT_ID + ") " +
                "VALUES ('" + CategoryEntry.ROOT_CATEGORY_TITLE + "', NULL);" ;

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_ROOT_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data,
        // so its upgrade policy is to simply to discard the data and start over.
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
