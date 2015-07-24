package com.example.dimart.popularmoviesapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private MoviesAdapter mMoviesAdapter;
    private MoviesPresenter mMoviesPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoviesPresenter = new MoviesPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_gridview);
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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String sortOrder = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_most_popular));
        mMoviesPresenter.loadMovies(sortOrder);
    }

    @Override
    public void displayMovies(Movie[] movies) {
        if (movies != null) {
            mMoviesAdapter.clear();
            mMoviesAdapter.addAll(movies);
        }
    }
}
