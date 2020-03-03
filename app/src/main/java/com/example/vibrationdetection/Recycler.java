package com.example.vibrationdetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public abstract class Recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "RecentTripsAdapter";
    private final ArrayList<Object> values;
    private int activeTripPosition = -1;
    private ViewGroup viewGroup = null;

    private static final int TRIP = 0, SHARE = 1, NO_TRIPS = 2;

    // Provide a suitable constructor (depends on the kind of data set)
    public Recycler(List<Integer> tripList) {
        //setHasStableIds(true);
        values = new ArrayList<>();
        if (tripList.size() == 0) {
            values.add(NO_TRIPS);
        }

        for (Integer trip : tripList) {
            values.add(trip);
        }

        values.add(SHARE);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewGroup == null) viewGroup = parent;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case SHARE:
        //        View v1 = inflater.inflate(R.layout.content_bottom_sheet_last_row, parent, false);
              //  viewHolder = new ShareViewHolder(v1);
                break;
            case NO_TRIPS:
            //    View v2 = inflater.inflate(R.layout.content_bottom_sheet_no_trips_row, parent, false);
               // viewHolder = new NoTripsViewHolder(v2);
                break;
            default: // TRIP
            //    View v = inflater.inflate(R.layout.content_bottom_sheet_row, parent, false);
             //   viewHolder = new TripViewHolder(v);
                break;
        }
        return null;
    }
}
