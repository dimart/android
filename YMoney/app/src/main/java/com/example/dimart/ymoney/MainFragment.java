package com.example.dimart.ymoney;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dimart.ymoney.model.Category;
import com.example.dimart.ymoney.provider.CategoriesContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitrii Petukhov on 8/19/15.
 * Displays categories as a simple list.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    SwipeRefreshLayout mSwipeRefreshLayout;
    CategoriesAdapter mCategoriesAdapter;
    boolean isRoot = true;

    /**
     * Bundle keys.
     */
    private final String CATEGORIES = "Categories";
    private final String ROOT = "Root";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Category> categories = new ArrayList<>();
        if (savedInstanceState != null) {
            categories = savedInstanceState.getParcelableArrayList(CATEGORIES);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            categories = bundle.getParcelableArrayList(CATEGORIES);
            isRoot = bundle.getBoolean(ROOT);
        }
        mCategoriesAdapter = new CategoriesAdapter(getActivity(), categories);

        final ListView listView = (ListView) rootView.findViewById(R.id.categories_list);
        listView.setAdapter(mCategoriesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Category> subCategories = mCategoriesAdapter.getItem(i).subs;
                if (subCategories != null && !subCategories.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(CATEGORIES, ((ArrayList<Category>) subCategories));
                    bundle.putBoolean(ROOT, false);

                    Fragment fragment = new MainFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
                } else {
                    showSnackbar(getResources().getString(R.string.warn_empty_category));
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.swipe_to_refresh_circle));
        if (isRoot) {
            mSwipeRefreshLayout.setClickable(true);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateCategoriesList(true);
                }
            });
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCategoriesAdapter.isEmpty()) {
            updateCategoriesList(false);
        }
    }

    private void updateCategoriesList(boolean forced) {
        if (!isNetworkAvailable()) {
            showSnackbar(getResources().getString(R.string.warn_no_connection));
            mSwipeRefreshLayout.setRefreshing(false);
            if (!isDbEmpty()) {
                new FetchCategoriesListTask(getActivity(), mCategoriesAdapter, mSwipeRefreshLayout)
                        .execute(false);
            }
            return;
        }
        new FetchCategoriesListTask(getActivity(), mCategoriesAdapter, mSwipeRefreshLayout)
                .execute(forced);
    }

    private boolean isDbEmpty() {
        Cursor c = getActivity().getContentResolver().query(
                CategoriesContract.CategoryEntry.CONTENT_URI,
                null, null, null, null);
        c.moveToFirst(); // root category is always there
        boolean isEmpty = !c.moveToNext();
        c.close();
        return isEmpty;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showSnackbar(String msg) {
        int snackColor = getResources().getColor(R.color.warning_empty_category);
        Snackbar snack = Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT);
        snack.getView().setBackgroundColor(snackColor);
        snack.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Category> categories = new ArrayList<>();
        int categoriesCount = mCategoriesAdapter.getCount();
        for (int i = 0; i < categoriesCount; i++) {
            categories.add(mCategoriesAdapter.getItem(i));
        }
        outState.putParcelableArrayList(CATEGORIES, categories);
    }
}
