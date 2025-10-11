package com.example.myapplication.main_func.omah_ride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private final List<TargetLocationActivity.LocationItem> locations;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TargetLocationActivity.LocationItem loc);
    }

    public LocationAdapter(List<TargetLocationActivity.LocationItem> locations, OnItemClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TargetLocationActivity.LocationItem loc = locations.get(position);
        holder.tvLocation.setText(loc.displayName);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(loc));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
