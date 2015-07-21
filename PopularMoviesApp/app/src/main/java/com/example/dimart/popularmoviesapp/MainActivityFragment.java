package com.example.dimart.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieAdapter mMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_gridview);
        moviesGrid.setAdapter(mMoviesAdapter);

        return rootView;
    }

    private URL getPosterUrlFor(String posterId) {
        if (posterId == null) {
            return null;
        }

        final String IMG_TMDB_BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";

        try {
            Uri posterUri = Uri.parse(IMG_TMDB_BASE_URL).buildUpon()
                    .appendPath(IMG_SIZE)
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_KEY)
                    .appendPath(posterId.substring(1))
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

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public interface TMDBService {
        @GET("/3/movie/{sort_order}")
        TMDBResponse movies(@Path("sort_order") String sortOrder, @Query("api_key") String apiKey);
    }

    private class TMDBResponse {
        String page;
        List<Movie> results =  new ArrayList<>();

        class Movie {
            String title;
            String poster_path;
            String overview;
            String release_date;
        }
    }

    private void updateMovies() {
        String sort_order = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_most_popular));
        new FetchMoviesTask().execute(sort_order);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sortOrder = params[0];
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.themoviedb.org")
                    .build();
            TMDBService service = restAdapter.create(TMDBService.class);

            List<TMDBResponse.Movie> response = service.movies(sortOrder, BuildConfig.THE_MOVIE_DB_KEY).results;
            List<Movie> movies = new ArrayList<>();
            for (TMDBResponse.Movie x : response) {
                movies.add(new Movie.Builder(x.title, x.overview)
                        .posterUrl(getPosterUrlFor(x.poster_path))
                        .releaseDate(toDate(x.release_date))
                        .build());
            }
            return movies.toArray(new Movie[movies.size()]);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMoviesAdapter.clear();
                mMoviesAdapter.addAll(movies);
            }
        }
    }
}
