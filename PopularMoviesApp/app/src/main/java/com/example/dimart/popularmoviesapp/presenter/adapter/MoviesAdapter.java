package com.example.dimart.popularmoviesapp.presenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dimart.popularmoviesapp.R;
import com.example.dimart.popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Dmitrii Petukhov on 7/18/15.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_movie, parent, false);

            // Configure view holder.
            holder = new MovieHolder();
            holder.mPosterView = (ImageView) convertView.findViewById(R.id.grid_item_poster);
            holder.mTitle = (TextView) convertView.findViewById(R.id.grid_item_title);
            holder.mBar = (LinearLayout) convertView.findViewById(R.id.grid_item_bar);
            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }

        Movie movie = getItem(position);

        holder.mPosterView.setImageDrawable(null);

        Picasso.with(getContext())
                .load(movie.getPosterUrl())
                .into(holder.target);

        holder.mTitle.setText(movie.getTitle());

        return convertView;
    }

    static class MovieHolder {
        private ImageView mPosterView;
        private TextView mTitle;
        private LinearLayout mBar;

        Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int color = palette.getVibrantColor(R.color.grid_item_bar);
                int red = (color >> 16) & 0xFF;
                int green = (color >> 8) & 0xFF;
                int blue = color & 0xFF;
                int alpha = 200;
                mBar.setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        };

        Target target = new Target() {
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
    }
}
