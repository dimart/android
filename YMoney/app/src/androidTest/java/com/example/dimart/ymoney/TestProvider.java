package com.example.dimart.ymoney;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.dimart.ymoney.provider.CategoriesContract.CategoryEntry;
import com.example.dimart.ymoney.provider.CategoriesDatabase;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Note: This is not a complete set of tests of the YMoney ContentProvider.
 */
public class TestProvider extends AndroidTestCase {

    public void clearDB() {
        mContext.getContentResolver().delete(
                CategoryEntry.CONTENT_URI,
                null,
                null
        );

        Cursor c = mContext.getContentResolver().query(
                CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Only one record should stay  – the root
        assertEquals("Error: Records were not deleted from the category table.", 1, c.getCount());
        assertTrue("Error: Can't find root directory in the db.", c.moveToFirst());
        assertEquals("Error: The root category was deleted.", CategoryEntry.ROOT_CATEGORY_TITLE,
                c.getString(c.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_TITLE)));
        c.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearDB();
    }

    public void testGetType() {
        // content://com.example.dimart.ymoney.app/category/
        String type = mContext.getContentResolver().getType(CategoryEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.dimart.ymoney.app/
        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
                CategoryEntry.CONTENT_TYPE, type);
    }

    public void testBasicCategoryQuery() {
        SQLiteDatabase db = new CategoriesDatabase(mContext).getWritableDatabase();

        Cursor c = mContext.getContentResolver().query(
                CategoryEntry.ROOT_CATEGORY_URI,
                null,
                null,
                null,
                null
        );
        c.moveToFirst();
        long rootIdIndex = c.getLong(c.getColumnIndex(CategoryEntry._ID));

        ContentValues testCategory = TestUtilities.getTestCategory(rootIdIndex);
        long categoryRowId = db.insert(CategoryEntry.TABLE_NAME, null, testCategory);
        assertTrue("Error: Cannot insert in the category database",
                categoryRowId != -1);
        db.close();

        // Test the basic content provider query
        c = mContext.getContentResolver().query(
                CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicCategoryQuery", c, testCategory);
    }
}
