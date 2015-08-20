package com.example.dimart.ymoney;

import android.content.UriMatcher;
import android.test.AndroidTestCase;

import com.example.dimart.ymoney.provider.CategoriesContract;
import com.example.dimart.ymoney.provider.CategoriesProvider;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Basic tests of the URI matcher.
 */
public class TestUriMatcher extends AndroidTestCase {
    public void testUriMatcher() {
        UriMatcher testMatcher = CategoriesProvider.buildUriMatcher();

        assertEquals("Error: Root category uri matched incorrectly.",
                testMatcher.match(CategoriesContract.CategoryEntry.ROOT_CATEGORY_URI),
                CategoriesProvider.ROOT_CATEGORY);

        assertEquals("Error: Category uri matched incorrectly.",
                testMatcher.match(CategoriesContract.CategoryEntry.CONTENT_URI),
                CategoriesProvider.CATEGORY);
    }
}
