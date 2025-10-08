package com.example.myapplication.main_func.caregiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class CaregiverAdapter extends RecyclerView.Adapter<CaregiverAdapter.ViewHolder> {

    private List<FamilyMember> members;
    private OnItemClickListener itemClickListener;
    private OnMoreOptionsClickListener moreOptionsClickListener;

    public CaregiverAdapter(List<FamilyMember> members) {
        this.members = members;
    }

    // -------------------------------
    // Define two interfaces
    // -------------------------------
    public interface OnItemClickListener {
        void onItemClick(FamilyMember member);
    }

    public interface OnMoreOptionsClickListener {
        void onMoreOptionsClick(FamilyMember member);
    }

    // Setters for each listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnMoreOptionsClickListener(OnMoreOptionsClickListener listener) {
        this.moreOptionsClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_family_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMember member = members.get(position);
        holder.nameText.setText(member.getName());
        holder.ageText.setText(member.getAge() + " Tahun");

        // Normal click → CaregiverDurationActivity
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(member);
            }
        });

        // More options click → Edit screen
        holder.moreOptions.setOnClickListener(v -> {
            if (moreOptionsClickListener != null) {
                moreOptionsClickListener.onMoreOptionsClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, ageText;
        ImageView moreOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            ageText = itemView.findViewById(R.id.age_text);
            moreOptions = itemView.findViewById(R.id.more_options);
        }
    }
}
