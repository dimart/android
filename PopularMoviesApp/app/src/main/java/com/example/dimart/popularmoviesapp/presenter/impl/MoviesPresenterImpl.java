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
        new FetchMoviesTask().execute(sortOrder);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            final String TMDB_BASE_URL = "http://api.themoviedb.org";
            final String POSTER_SIZE = "w185";
            final String BACKDROP_SIZE = "w500";
            final String SORT_ORDER = params[0];

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(TMDB_BASE_URL)
                    .build();
            TMDBService service = restAdapter.create(TMDBService.class);

            List<TMDBResponse.Movie> response =
                    service.call(SORT_ORDER, BuildConfig.THE_MOVIE_DB_KEY).results;
            List<Movie> movies = new ArrayList<>();
            for (TMDBResponse.Movie x : response) {
                movies.add(new Movie.Builder(x.title, x.overview)
                        .posterUrl(getImgUrlFor(x.poster_path, POSTER_SIZE))
                        .rating(x.vote_average)
                        .releaseDate(toDate(x.release_date))
                        .backdropUrl(getImgUrlFor(x.backdrop_path, BACKDROP_SIZE))
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

    private URL getImgUrlFor(String id, String imgSize) {
        if (id == null) {
            return null;
        }

        final String IMG_TMDB_BASE_URL = "http://image.tmdb.org/t/p/";
        try {
            Uri posterUri = Uri.parse(IMG_TMDB_BASE_URL).buildUpon()
                    .appendPath(imgSize)
                    .appendEncodedPath(id)
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_KEY)
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
        TMDBResponse call(@Path("sort_order") String sortOrder, @Query("api_key") String apiKey);
    }

    private class TMDBResponse {
        String page;
        List<Movie> results = new ArrayList<>();

        class Movie {
            String title;
            String poster_path;
            String overview;
            String release_date;
            String backdrop_path;
            float vote_average;
        }
    }
}
