package com.example.dimart.popularmoviesapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dimart.popularmoviesapp.R;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Dmitrii Petukhov on 7/22/15.
 */
public class DetailFragment extends Fragment implements ObservableScrollViewCallbacks {

    private TextView mTitleView;
    private View mFlexibleSpaceView;
    private View mToolbarView;

    private int mFlexibleSpaceHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Activity activity = getActivity();

        ((AppCompatActivity) activity).setSupportActionBar((Toolbar) rootView.findViewById(R.id.detail_toolbar));
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFlexibleSpaceView = rootView.findViewById(R.id.detail_flexible_space);
        mToolbarView = rootView.findViewById(R.id.detail_toolbar);

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        activity.setTitle(null);

        final ObservableScrollView scrollView =
                (ObservableScrollView) rootView.findViewById(R.id.detail_scroll);
        scrollView.setScrollViewCallbacks(this);

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        int flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + getActionBarSize();

        rootView.findViewById(R.id.detail_body).setPadding(0, flexibleSpaceAndToolbarHeight, 0, 0);
        mFlexibleSpaceView.getLayoutParams().height = flexibleSpaceAndToolbarHeight;

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String title = intent.getStringExtra(Intent.EXTRA_TEXT);
            String overview = intent.getStringExtra("overview");
            mTitleView.setText(title);
            ((TextView) rootView.findViewById(R.id.detail_textview)).setText(overview);
        }

        return rootView;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        ViewHelper.setTranslationY(mFlexibleSpaceView, -scrollY);
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
