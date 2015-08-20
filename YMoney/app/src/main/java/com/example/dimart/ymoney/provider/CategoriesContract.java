package com.example.dimart.ymoney.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Defines table and column names for the categories database.
 */
public class CategoriesContract {

    public static final String CONTENT_AUTHORITY = "com.example.dimart.ymoney.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CATEGORY = "category";
    public static final String PATH_ROOT = "root";

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final Uri ROOT_CATEGORY_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_ROOT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "categories";

        // Category id and category title as returned by Yandex Money API.
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_CATEGORY_TITLE = "title";

        // We use Adjacency List approach to store tree-structured data in the database.
        public static final String COLUMN_PARENT_ID = "parent_id";

        public static final String ROOT_CATEGORY_TITLE = "_root";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
