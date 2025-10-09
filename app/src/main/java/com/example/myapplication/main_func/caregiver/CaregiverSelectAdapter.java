package com.example.myapplication.main_func.caregiver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class CaregiverSelectAdapter extends RecyclerView.Adapter<CaregiverSelectAdapter.ViewHolder> {

    private Context context;
    private List<CaregiverModel> caregiverList;
    private DecimalFormat priceFormat = new DecimalFormat("#,###");

    public CaregiverSelectAdapter(Context context, List<CaregiverModel> caregiverList) {
        this.context = context;
        this.caregiverList = caregiverList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_caregiver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CaregiverModel caregiver = caregiverList.get(position);

        holder.textName.setText(caregiver.getName());
        holder.textExperience.setText((int) caregiver.getExp() + " tahun pengalaman");
        holder.textDistance.setText(caregiver.getDist() + " km dari lokasi anda");
        holder.textPrice.setText("Rp " + priceFormat.format(caregiver.getCost()));

        Glide.with(context)
                .load(caregiver.getImg())
                .placeholder(R.drawable.ic_caregiver)
                .error(R.drawable.ic_caregiver)
                .into(holder.imageCaregiver);
    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCaregiver;
        TextView textName, textExperience, textDistance, textPrice;
        Button buttonOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCaregiver = itemView.findViewById(R.id.imageCaregiver);
            textName = itemView.findViewById(R.id.textName);
            textExperience = itemView.findViewById(R.id.textExperience);
            textDistance = itemView.findViewById(R.id.textDistance);
            textPrice = itemView.findViewById(R.id.textPrice);
            buttonOrder = itemView.findViewById(R.id.buttonOrder);
        }
    }
}
