package com.example.tripper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tripper.model.Trip;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TripAdapter extends ArrayAdapter<Trip> {

    private static class ViewHolder {
        TextView name;
        TextView description;
        TextView distance;
        ImageView info;
    }

    public TripAdapter(ArrayList<Trip> data, Context context) {
        super(context, R.layout.list_item_trip, data);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Trip dataModel = getItem(position);
        ViewHolder viewHolder;
        DecimalFormat df2 = new DecimalFormat("#.##");

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_trip, parent, false);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.description = convertView.findViewById(R.id.description);
            viewHolder.distance = convertView.findViewById(R.id.distance);
            viewHolder.info = convertView.findViewById(R.id.picture);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel != null ? dataModel.getName() : "");
        viewHolder.description.setText(dataModel.getDescription());
        viewHolder.distance.setText(df2.format(dataModel.getDistance()) + " meters");
        viewHolder.info.setTag(position);

        return convertView;
    }
}