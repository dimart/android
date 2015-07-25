package com.example.dimart.popularmoviesapp.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.dimart.popularmoviesapp.R;
import com.example.dimart.popularmoviesapp.model.Movie;
import com.example.dimart.popularmoviesapp.presenter.MoviesPresenter;
import com.example.dimart.popularmoviesapp.presenter.adapter.MoviesAdapter;
import com.example.dimart.popularmoviesapp.presenter.impl.MoviesPresenterImpl;
import com.example.dimart.popularmoviesapp.util.Utils;
import com.example.dimart.popularmoviesapp.view.MoviesView;
import com.example.dimart.popularmoviesapp.view.activity.DetailActivity;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements MoviesView {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    private View mRootView;
    private MoviesAdapter mMoviesAdapter;
    private MoviesPresenter mMoviesPresenter;
    private String mSortOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoviesPresenter = new MoviesPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSortOrder = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_most_popular));

        ArrayList<Movie> movies = new ArrayList<>();
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(Utils.MOVIES);
            mSortOrder = savedInstanceState.getString(Utils.SORT_ORDER);
        }

        mMoviesAdapter = new MoviesAdapter(getActivity(), movies);
        GridView moviesGrid = (GridView) mRootView.findViewById(R.id.movies_gridview);
        moviesGrid.setAdapter(mMoviesAdapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Utils.EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean orderChanged = !mSortOrder.equals(PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_most_popular)));

        // If the bundle's empty or user just changes sort order,
        // we'll try to update movies immediately.
        if (orderChanged || mMoviesAdapter.getCount() == 0) {
            tryUpdateMovies();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Movie> movies = new ArrayList<>();
        int moviesCount = mMoviesAdapter.getCount();
        for (int i = 0; i < moviesCount; i++) {
            movies.add(mMoviesAdapter.getItem(i));
        }
        outState.putParcelableArrayList(Utils.MOVIES, movies);
        outState.putString(Utils.SORT_ORDER, mSortOrder);
    }

    @Override
    public void displayMovies(Movie[] movies) {
        if (movies != null) {
            mMoviesAdapter.clear();
            mMoviesAdapter.addAll(movies);
        }
    }

    public void tryUpdateMovies() {
        if (!isNetworkAvailable()) {
            showNoConnectionWarning();
            return;
        }
        mSortOrder = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_most_popular));
        mMoviesPresenter.loadMovies(mSortOrder);
    }

    public void showNoConnectionWarning() {
        int color = getResources().getColor(R.color.no_connection_warning);
        String msg = getString(R.string.no_connection);
        Snackbar snackbar = Snackbar.make(mRootView, msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }
}
