package com.example.dimart.ymoney;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.dimart.ymoney.provider.CategoriesContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Helpful functions which we use in tests.
 */
public class TestUtilities extends AndroidTestCase {
    public static final String testCategoryTitle = "Android Apps";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        valueCursor.moveToNext();
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues getTestCategory(long rootIdIndex) {
        ContentValues testCategory = new ContentValues();
        testCategory.put(CategoriesContract.CategoryEntry.COLUMN_CATEGORY_ID, 42);
        testCategory.put(CategoriesContract.CategoryEntry.COLUMN_CATEGORY_TITLE, testCategoryTitle);
        testCategory.put(CategoriesContract.CategoryEntry.COLUMN_PARENT_ID, rootIdIndex);

        return testCategory;
    }
}
