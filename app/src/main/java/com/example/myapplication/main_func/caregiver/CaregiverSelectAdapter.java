package com.example.myapplication.main_func.caregiver;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class CaregiverSelectAdapter extends RecyclerView.Adapter<CaregiverSelectAdapter.ViewHolder> {

    private Context context;
    private List<CaregiverModel> caregiverList;

    // Extra data passed from previous activity
    private String lansiaId, name, age, gender, religion, relation, complaint;
    private int durationDays;

    public CaregiverSelectAdapter(Context context, List<CaregiverModel> caregiverList) {
        this.context = context;
        this.caregiverList = caregiverList;
    }

    // Allow adapter to receive extra data later
    public void setExtraData(String lansiaId, String name, String age, String gender,
                             String religion, String relation, String complaint, int durationDays) {
        this.lansiaId = lansiaId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.religion = religion;
        this.relation = relation;
        this.complaint = complaint;
        this.durationDays = durationDays;
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

        holder.tvName.setText(caregiver.getName());
        holder.tvExp.setText(caregiver.getExp() + " tahun pengalaman");
        holder.tvDist.setText(caregiver.getDist() + " km dari lokasi anda");
        holder.tvCost.setText("Rp " + caregiver.getCost());

        // Load image from Firebase URL
        Glide.with(context)
                .load(caregiver.getImg())
                .placeholder(R.drawable.ic_caregiver)
                .into(holder.ivPhoto);

        // 🔹 "Pesan" button → go to CaregiverDateActivity
        holder.btnPesan.setOnClickListener(v -> {
            Intent intent = new Intent(context, CaregiverDateActivity.class);
            intent.putExtra("lansiaId", lansiaId);
            intent.putExtra("name", name);
            intent.putExtra("age", age);
            intent.putExtra("gender", gender);
            intent.putExtra("religion", religion);
            intent.putExtra("relation", relation);
            intent.putExtra("complaint", complaint);
            intent.putExtra("durationDays", durationDays);

            // Add caregiver-specific data
            intent.putExtra("caregiverName", caregiver.getName());
            intent.putExtra("caregiverImg", caregiver.getImg());
            intent.putExtra("caregiverExp", caregiver.getExp());
            intent.putExtra("caregiverDist", caregiver.getDist());
            intent.putExtra("caregiverCost", caregiver.getCost());

            // ✅ Add missing keys
            intent.putExtra("caregiverId", caregiver.getCaregiverId()); // if exists
            intent.putExtra("familyKey", lansiaId); // family = the selected lansia


            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvExp, tvDist, tvCost;
        Button btnPesan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.imageCaregiver);
            tvName = itemView.findViewById(R.id.textName);
            tvExp = itemView.findViewById(R.id.textExperience);
            tvDist = itemView.findViewById(R.id.textDistance);
            tvCost = itemView.findViewById(R.id.textPrice);
            btnPesan = itemView.findViewById(R.id.buttonOrder);
        }
    }
}
