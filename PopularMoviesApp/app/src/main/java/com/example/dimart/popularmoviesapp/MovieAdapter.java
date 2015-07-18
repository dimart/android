package com.example.dimart.popularmoviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Dmitrii Petukhov on 7/18/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context context;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_movie, parent, false);

            holder = new MovieHolder();
            holder.posterView = (ImageView) convertView.findViewById(R.id.grid_item_poster);

            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }

        Movie movie = getItem(position);

        holder.posterView.setImageDrawable(null);
        Picasso.with(getContext())
                .load(movie.getPosterUrl())
                .into(holder.posterView);
        Log.d(LOG_TAG, movie.getPosterUrl());
        return convertView;
    }

    static class MovieHolder
    {
        ImageView posterView;
    }
}
