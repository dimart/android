package com.example.dimart.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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

    private void updateMovies() {
        new FetchMoviesTask().execute("popular");
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {
            final String TMDB_LIST = "results";
            final String TMDB_TITLE = "title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_DATE = "release_date";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_VOTE = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_LIST);

            Movie[] movies = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieData = moviesArray.getJSONObject(i);

                movies[i] = new Movie.Builder(movieData.getString(TMDB_TITLE),
                        movieData.getString(TMDB_OVERVIEW))
                        .releaseDate(toDate(movieData.getString(TMDB_DATE)))
                        .posterUrl(getPosterUrlFor(movieData.getString(TMDB_POSTER)))
                        .rating(((float) movieData.getDouble(TMDB_VOTE)))
                        .build();
            }

            return movies;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String QUERY_TYPE = params[0]; // popular or top_rated
                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(QUERY_TYPE)
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline won't affect parsing
                    // But it does make debugging a *lot* easier.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null; // No point in parsing.
                }

                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
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
