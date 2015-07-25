package com.example.dimart.popularmoviesapp.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dimart.popularmoviesapp.R;
import com.example.dimart.popularmoviesapp.model.Movie;
import com.example.dimart.popularmoviesapp.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Dmitrii Petukhov on 7/22/15.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private CollapsingToolbarLayout mCollapsingToolbar;
    private TextView mOverviewView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private ImageView mPosterView;

    private Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
        public void onGenerated(Palette palette) {
            int darkVibrantColor = palette.getDarkVibrantColor(R.color.grid_item_bar);
            int vibrantColor = palette.getVibrantColor(R.color.grid_item_bar);
            mCollapsingToolbar.setContentScrimColor(vibrantColor);

            if (Build.VERSION.SDK_INT >= 21 && getActivity() != null) {
                Window window = getActivity().getWindow();
                window.setStatusBarColor(darkVibrantColor);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mPosterView.setImageBitmap(bitmap);
            Palette.generateAsync(bitmap, listener);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        /**
         * Get the views and save them.
         */
        Toolbar mToolbarView = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        mCollapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mRatingView = (TextView) rootView.findViewById(R.id.rating);
        mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_poster);

        /**
         * Setup Toolbar.
         */
        activity.setSupportActionBar(mToolbarView);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /**
         * Intent handling.
         */
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Utils.EXTRA_MOVIE)) {
            Movie movie = intent.getParcelableExtra(Utils.EXTRA_MOVIE);
            showMovieDetails(movie);
        }

        return rootView;
    }

    private void showMovieDetails(Movie movie) {
        Picasso.with(getActivity()).load(movie.getBackdropUrl()).into(target);
        mCollapsingToolbar.setTitle(movie.getTitle());
        mOverviewView.setText(movie.getOverview());
        mRatingView.setText(movie.getRating() + "/10");
        mReleaseDateView.setText(movie.getReleaseDate());
    }
}
