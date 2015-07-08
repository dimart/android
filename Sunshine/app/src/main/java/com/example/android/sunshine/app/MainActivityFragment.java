package com.example.android.sunshine.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {
                "Today - Rainy - 50/40",
                "Tomorrow - Sunny - 80/67"
        };

        List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));

        mForecastAdapter = new ArrayAdapter<>(
                // The current context (this fragment's parent activity)
                getActivity(),
                // ID of list item layout
                R.layout.list_item_forecast,
                // ID of the textView to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                weekForecast);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }
}
