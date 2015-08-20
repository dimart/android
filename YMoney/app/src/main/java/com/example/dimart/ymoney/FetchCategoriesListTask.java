package com.example.dimart.ymoney;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import com.example.dimart.ymoney.model.Category;
import com.example.dimart.ymoney.provider.CategoriesContract.CategoryEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * AsyncTask fetches data either from the db OR from the cloud (saving results to db).
 */
public class FetchCategoriesListTask extends AsyncTask<Boolean, Void, Category[]> {

    private final String LOG_TAG = FetchCategoriesListTask.class.getSimpleName();

    private CategoriesAdapter mCategoriesAdapter;
    private final Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private long rootId;

    private interface YMoneyService {
        @GET("/categories-list")
        List<Category> categories();
    }

    public FetchCategoriesListTask(
            Context context,
            CategoriesAdapter categoriesAdapter,
            SwipeRefreshLayout swipeRefreshLayout) {
        mCategoriesAdapter = categoriesAdapter;
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;

        Cursor c = mContext.getContentResolver().query(
                CategoryEntry.ROOT_CATEGORY_URI,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        rootId = c.getLong(c.getColumnIndex(CategoryEntry._ID));
        c.close();
    }

    @Override
    protected Category[] doInBackground(Boolean... params) {
        if (params.length == 0) {
            return null;
        }

        final boolean forced = params[0];
        if (forced) {
            return fetchFromCloud();
        }

        Category[] result = fetchFromDb();
        if (result == null || result.length == 0) {
            result = fetchFromCloud();
        }
        return result;
    }

    private Category[] fetchFromCloud() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://money.yandex.ru/api")
                .build();
        YMoneyService service = restAdapter.create(YMoneyService.class);
        List<Category> response = service.categories();
        saveCategories(response, rootId);
        return response.toArray(new Category[response.size()]);
    }

    private Category[] fetchFromDb() {
        List<Category> result = getCategories(rootId);
        return result != null ? result.toArray(new Category[result.size()]) : null;
    }

    private List<Category> getCategories(long id) {
        ArrayList<Category> result = new ArrayList<>();

        // Get children of category with id
        Cursor c = mContext.getContentResolver().query(
                CategoryEntry.CONTENT_URI,
                null,
                CategoryEntry.COLUMN_PARENT_ID + " = " + id,
                null,
                null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        do {
            int cat_id = c.getInt(c.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_ID));
            String title = c.getString(c.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_TITLE));
            List<Category> subs = getCategories(c.getLong(c.getColumnIndex(CategoryEntry._ID)));
            result.add(new Category(cat_id, title, subs));
        } while (c.moveToNext());

        c.close();
        return result;
    }

    @Override
    protected void onPostExecute(Category[] categories) {
        if (categories != null) {
            mCategoriesAdapter.clear();
            mCategoriesAdapter.addAll(categories);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void saveCategories(List<Category> categories, long parentId) {
        if (categories == null) {
            return;
        }

        for (Category c : categories) {
            long childId;

            // First, check if the category with this title exists in the db
            Cursor cursor = mContext.getContentResolver().query(
                    CategoryEntry.CONTENT_URI,
                    new String[]{CategoryEntry._ID},
                    CategoryEntry.COLUMN_CATEGORY_TITLE + " = ?",
                    new String[]{c.title},
                    null);

            // If there is no such category.
            if (!cursor.moveToFirst()) {
                ContentValues categoryValues = new ContentValues();
                categoryValues.put(CategoryEntry.COLUMN_CATEGORY_ID, c.id);
                categoryValues.put(CategoryEntry.COLUMN_CATEGORY_TITLE, c.title);
                categoryValues.put(CategoryEntry.COLUMN_PARENT_ID, parentId);

                Uri insertedUri = mContext.getContentResolver().insert(
                        CategoryEntry.CONTENT_URI,
                        categoryValues);
                childId = ContentUris.parseId(insertedUri);
            } else {
                childId = cursor.getLong(cursor.getColumnIndex(CategoryEntry._ID));
            }

            cursor.close();
            saveCategories(c.subs, childId);
        }
    }
}
