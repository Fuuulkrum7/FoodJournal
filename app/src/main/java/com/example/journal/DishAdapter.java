package com.example.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class DishAdapter extends ArrayAdapter<Dish> {
    public DishAdapter(@NonNull Context context, @NonNull List<Dish> objects) {
        super(context, R.layout.dish_fragment, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Dish dish = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dish_fragment, null);
        ((TextView)convertView.findViewById(R.id.dishName)).setText(dish.getName());
        ((TextView)convertView.findViewById(R.id.mass)).setText(dish.getMass());
        ((TextView)convertView.findViewById(R.id.calories)).setText(dish.getCalories());

        return convertView;
    }
}
