package com.example.dimart.ymoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dimart.ymoney.model.Category;

import java.util.List;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 * Custom array adapter for categories list view.
 */
public class CategoriesAdapter extends ArrayAdapter<Category> {

    public CategoriesAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_category, parent, false);

            // Configure view holder.
            holder = new CategoryHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.list_item_category_title);
            convertView.setTag(holder);
        } else {
            holder = (CategoryHolder) convertView.getTag();
        }

        Category c = getItem(position);
        holder.mTitle.setText(c.title);

        return convertView;
    }

    static class CategoryHolder {
        private TextView mTitle;
    }
}
