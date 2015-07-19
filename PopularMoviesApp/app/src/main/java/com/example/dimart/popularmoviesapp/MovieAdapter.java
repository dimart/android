package com.example.dimart.popularmoviesapp;

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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Dmitrii Petukhov on 7/18/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private static MovieHolder holder;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_movie, parent, false);

            holder = new MovieHolder();
            holder.posterView = (ImageView) convertView.findViewById(R.id.grid_item_poster);
            holder.title = (TextView) convertView.findViewById(R.id.grid_item_title);
            holder.bar = (LinearLayout) convertView.findViewById(R.id.grid_item_bar);

            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }

        Movie movie = getItem(position);

        Picasso.with(getContext()).setLoggingEnabled(true);
        Picasso.with(getContext())
                .load(movie.getPosterUrl())
                .into(holder.target);

        holder.title.setText(movie.getTitle());

        return convertView;
    }

    static class MovieHolder
    {
        ImageView posterView;
        TextView title;
        LinearLayout bar;

        Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                bar.setBackgroundColor(palette.getVibrantColor(Color.BLACK));
//                if (isTooLight(palette.getDarkVibrantSwatch().getHsl())) {
//                    title.setTextColor(Color.argb(130, 0, 0, 0));
//                } else {
//                    title.setTextColor(Color.argb(210, 255, 255, 255));
//                }
            }
        };

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                posterView.setImageDrawable(null);
                posterView.setImageBitmap(bitmap);
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
