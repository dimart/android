package com.example.dimart.ymoney;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by Dmitrii Petukhov on 8/19/15.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCategoriesList();
    }

    private void updateCategoriesList() {
        new FetchCategoriesListTask().execute();
    }

    private interface YMoneyService {
        @GET("/categories-list")
        List<Category> categories();
    }

    private class Category {
        Integer id;
        String title;
        List<Category> subs;

        @Override
        public String toString() {
            String result = "";
            result += "Category: '" + title + "' ";
            if (id != null) {
                result += "id=" + id + " ";
            }
            if (subs != null && !subs.isEmpty()) {
                for (Category c : subs) {
                    result = result + "\n\t";
                    result += c.toString();
                }
            }
            return result;
        }
    }

    private class FetchCategoriesListTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchCategoriesListTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://money.yandex.ru/api")
                    .build();
            YMoneyService service = restAdapter.create(YMoneyService.class);

            List<Category> response = service.categories();
            for (Category c : response) {
                Log.d(LOG_TAG, "" + c);
            }

            return null;
        }
    }
}
