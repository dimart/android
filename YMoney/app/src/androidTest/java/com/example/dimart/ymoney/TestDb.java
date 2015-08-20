package com.example.dimart.ymoney;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.dimart.ymoney.provider.CategoriesContract;
import com.example.dimart.ymoney.provider.CategoriesContract.CategoryEntry;
import com.example.dimart.ymoney.provider.CategoriesDatabase;

import java.util.HashSet;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Basic tests of the db creation.
 */
public class TestDb extends AndroidTestCase {

    public void setUp() {
        deleteTheDatabase();
    }

    void deleteTheDatabase() {
        mContext.deleteDatabase(CategoriesDatabase.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(CategoryEntry.TABLE_NAME);

        SQLiteDatabase db = new CategoriesDatabase(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: The database has not been created correctly",
                c.moveToFirst());

        // verify that the table has been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while(c.moveToNext());
        assertTrue("Error: The database was created without the categories table",
                tableNameHashSet.isEmpty());

        // verify that the table contains the correct columns
        c = db.rawQuery("PRAGMA table_info(" + CategoryEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> columnHashSet = new HashSet<>();
        columnHashSet.add(CategoryEntry._ID);
        columnHashSet.add(CategoryEntry.COLUMN_CATEGORY_ID);
        columnHashSet.add(CategoryEntry.COLUMN_CATEGORY_TITLE);
        columnHashSet.add(CategoryEntry.COLUMN_PARENT_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required category entry columns",
                columnHashSet.isEmpty());

        c.close();
        db.close();
    }

    public void testCategoriesTable() {
        SQLiteDatabase db = new CategoriesDatabase(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        final String SQL_QUERY_ROOT_CATEGORY = "SELECT * FROM " +
                CategoriesContract.CategoryEntry.TABLE_NAME + " WHERE " +
                CategoriesContract.CategoryEntry.COLUMN_CATEGORY_TITLE + " = '" +
                CategoriesContract.CategoryEntry.ROOT_CATEGORY_TITLE + "'";
        Cursor c = db.rawQuery(SQL_QUERY_ROOT_CATEGORY, null);
        assertTrue("Error: The database has not been created correctly",
                c.moveToFirst());

        int rootIdIndex = c.getInt(c.getColumnIndex(CategoriesContract.CategoryEntry._ID));
        ContentValues testCategory = TestUtilities.getTestCategory(rootIdIndex);

        long id = db.insert(CategoryEntry.TABLE_NAME, null, testCategory);
        assertTrue("Error: Cannot insert in the category database",
                id != -1);

        c = db.query(CategoryEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No records returned from category query.",
                c.moveToLast());

        TestUtilities.validateCurrentRecord("testCategoriesTable", c, testCategory);

        assertFalse("Error: More than one record returned from category query",
                c.moveToNext() );

        c.close();
        db.close();
    }
}
