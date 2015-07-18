package com.example.dimart.popularmoviesapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Movie[] movies = {
                new Movie.Builder("12 Angry Men", "Overview")
                        .rating(8.1f)
                        .releaseDate(toDate("1957-04-10"))
                        .posterUrl(getPosterUrlFor("qcL1YfkCxfhsdO6sDDJ0PpzMF9n.jpg"))
                        .build(),
                new Movie.Builder("Partly Cloudy", "Overview")
                        .rating(8.0f)
                        .releaseDate(toDate("2009-05-28"))
                        .posterUrl(getPosterUrlFor("5M5bg79OV96Vb4O0fDjX5clxASG.jpg"))
                        .build(),
                new Movie.Builder("One Flew Over the Cuckoo's Nest", "Overview")
                        .rating(7.8f)
                        .releaseDate(toDate("1975-11-18"))
                        .posterUrl(getPosterUrlFor("2Sns5oMb356JNdBHgBETjIpRYy9.jpg"))
                        .build(),
                new Movie.Builder("Solaris", "Overview")
                        .rating(7.7f)
                        .releaseDate(toDate("1972-03-20"))
                        .posterUrl(getPosterUrlFor("pjarQzkcXDmNKi75m2FhXexvR6m.jpg"))
                        .build()
        };

        List<Movie> moviesList = new ArrayList<>(Arrays.asList(movies));
        MovieAdapter moviesAdapter = new MovieAdapter(getActivity(), moviesList);
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_gridview);
        moviesGrid.setAdapter(moviesAdapter);

        return rootView;
    }

    private URL getPosterUrlFor(String posterId) {
        final String IMG_TMDB_BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";

        try {
            Uri posterUri = Uri.parse(IMG_TMDB_BASE_URL).buildUpon()
                    .appendPath(IMG_SIZE)
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_KEY)
                    .appendPath(posterId)
                    .build();
            return new URL(posterUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return null;
    }

    private Date toDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-mm-dd").parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return null;
    }
}
