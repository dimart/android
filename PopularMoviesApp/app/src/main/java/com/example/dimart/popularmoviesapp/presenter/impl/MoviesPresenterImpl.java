package com.example.dimart.popularmoviesapp.presenter.impl;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dimart.popularmoviesapp.BuildConfig;
import com.example.dimart.popularmoviesapp.model.Movie;
import com.example.dimart.popularmoviesapp.presenter.MoviesPresenter;
import com.example.dimart.popularmoviesapp.view.MoviesView;

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
 * Created by Dmitrii Petukhov on 7/21/15.
 */
public class MoviesPresenterImpl implements MoviesPresenter {

    private final String LOG_TAG = MoviesPresenterImpl.class.getSimpleName();

    private MoviesView mMoviesView;

    public MoviesPresenterImpl(MoviesView moviesView) {
        mMoviesView = moviesView;
    }

    @Override
    public void loadMovies(String sortOrder) {
        Log.d(LOG_TAG, sortOrder);
        new FetchMoviesTask().execute(sortOrder);
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
                mMoviesView.displayMovies(movies);
            }
        }
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

    private interface TMDBService {
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
}
