package com.example.myapplication.main_func.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(CheckoutItem item, boolean isChecked);
    }

    private ArrayList<CheckoutItem> items;
    private OnItemCheckedChangeListener listener;

    public CheckoutAdapter(ArrayList<CheckoutItem> items, OnItemCheckedChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckoutItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText("x" + item.getQuantity());
        holder.checkBox.setChecked(item.isSelected());

        Glide.with(holder.img.getContext())
                .load(item.getImg())
                .into(holder.img);

        if (item.hasDiscount()) {
            holder.price.setText("Rp " + String.format("%.0f", item.getPrice()));
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.originalPrice.setText("Rp " + String.format("%.0f", item.getOriginalPrice()));
            holder.discountTag.setVisibility(View.VISIBLE);
            int percent = (int) ((item.getOriginalPrice() - item.getPrice()) * 100 / item.getOriginalPrice());
            holder.discountTag.setText(percent + "%");
        } else {
            holder.price.setText("Rp " + String.format("%.0f", item.getPrice()));
            holder.originalPrice.setVisibility(View.GONE);
            holder.discountTag.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onItemCheckedChanged(item, isChecked);
        });
    }

    // Add method to safely add or increase quantity
    public void addItem(CheckoutItem newItem) {
        for (CheckoutItem item : items) {
            if (item.getId().equals(newItem.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                notifyDataSetChanged();
                return;
            }
        }
        items.add(newItem);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView img;
        TextView name, price, originalPrice, quantity, discountTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxSelect);
            img = itemView.findViewById(R.id.imgProduct);
            name = itemView.findViewById(R.id.tvProductName);
            price = itemView.findViewById(R.id.tvDiscountPrice);
            originalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            quantity = itemView.findViewById(R.id.tvQuantity);
            discountTag = itemView.findViewById(R.id.tvDiscountTag);
        }
    }

}
