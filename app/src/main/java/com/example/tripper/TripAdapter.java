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

import com.example.tripper.model.Trip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class TripAdapter extends ArrayAdapter<Trip> implements View.OnClickListener {

    private ArrayList<Trip> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView description;
        //TextView txtVersion;
        ImageView info;
    }

    public TripAdapter(ArrayList<Trip> data, Context context) {
        super(context, R.layout.list_item_trip, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Trip dataModel = (Trip) object;

        switch (v.getId()) {
            case R.id.name:
                Snackbar.make(v, "Release date " + dataModel.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Trip dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_trip, parent, false);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.description = convertView.findViewById(R.id.description);
            //viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = convertView.findViewById(R.id.picture);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.name.setText(dataModel.getName());
        viewHolder.description.setText(dataModel.getDescription());
        //viewHolder.txtVersion.setText(dataModel.getVersion_number());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}