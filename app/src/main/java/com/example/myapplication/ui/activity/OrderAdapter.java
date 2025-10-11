package com.example.myapplication.ui.activity;

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

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.title.setText(order.getTitle());
        holder.regNumber.setText(order.getRegNumber());
        holder.status.setText(order.getStatus());

        // Load image with Glide
        String imgUrl = order.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.pfp_2) // your placeholder
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.pfp_2);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView title, regNumber;
        Button status;
        ImageView image;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.order_title);
            regNumber = itemView.findViewById(R.id.order_reg);
            status = itemView.findViewById(R.id.order_status);
            image = itemView.findViewById(R.id.order_avatar); // new ImageView
        }
    }
}
