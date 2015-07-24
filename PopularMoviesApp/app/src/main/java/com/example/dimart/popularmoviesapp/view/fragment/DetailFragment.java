package com.example.dimart.popularmoviesapp.view.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Dmitrii Petukhov on 7/22/15.
 */
public class DetailFragment extends Fragment implements ObservableScrollViewCallbacks {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private ImageView mPosterView;
    private View mGradientView;
    private View mOverlayView;

    private int mFlexibleSpaceImageHeight;
    private int mToolbarBarHeight;

    private Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
        public void onGenerated(Palette palette) {
            int darkVibrantColor = palette.getDarkVibrantColor(R.color.grid_item_bar);
            int vibrantColor = palette.getVibrantColor(R.color.grid_item_bar);
            mOverlayView.setBackgroundColor(vibrantColor);

            if (Build.VERSION.SDK_INT >= 21) {
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
        activity.setTitle(null);

        /**
         * Get the views and save them.
         */
        Toolbar mToolbarView = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        mOverlayView = rootView.findViewById(R.id.detail_overlay);
        mGradientView = rootView.findViewById(R.id.detail_gradient);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mRatingView = (TextView) rootView.findViewById(R.id.rating);
        mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_poster);
        final ObservableScrollView scrollView =
                (ObservableScrollView) rootView.findViewById(R.id.detail_scroll);

        /**
         * Setup Toolbar.
         */
        activity.setSupportActionBar(mToolbarView);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarView.getBackground().setAlpha(0);

        /**
         * Get the dimension values and save them to fields (to simplify animation code).
         */
        mToolbarBarHeight = getActionBarSize();
        mFlexibleSpaceImageHeight = getResources()
                .getDimensionPixelSize(R.dimen.flexible_space_image_height);

        /**
         * Setup ScrollView.
         */
        scrollView.setScrollViewCallbacks(this);
        ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
            @Override
            public void run() {
                onScrollChanged(scrollView.getCurrentScrollY(), false, false);
            }
        });

        /**
         * Intent handling.
         */
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Utils.EXTRA_MOVIE)) {
            Movie movie = intent.getParcelableExtra(Utils.EXTRA_MOVIE);
            showMovieDetails(movie);
        }

        mGradientView.setBackgroundResource(R.drawable.gradient);
        return rootView;
    }

    private void showMovieDetails(Movie movie) {
        Picasso.with(getActivity()).load(movie.getBackdropUrl()).into(target);
        mTitleView.setText(movie.getTitle());
        mOverviewView.setText(movie.getOverview());
        mRatingView.setText(movie.getRating() + "/10");
        mReleaseDateView.setText(movie.getReleaseDate());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mToolbarBarHeight;
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, -flexibleRange, 0));
        ViewHelper.setTranslationY(mPosterView, ScrollUtils.getFloat(-scrollY, -flexibleRange, 0));
        ViewHelper.setTranslationY(mGradientView, ScrollUtils.getFloat(-scrollY, -flexibleRange, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, 0.3f);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        float titleTranslationY = ScrollUtils.getFloat(maxTitleTranslationY-scrollY, 0, maxTitleTranslationY);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}
