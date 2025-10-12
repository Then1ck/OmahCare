package com.example.myapplication.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PaymentMethod method);
    }

    public PaymentAdapter(Context context, List<PaymentMethod> paymentMethods, OnItemClickListener listener) {
        this.context = context;
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ViewHolder holder, int position) {
        PaymentMethod method = paymentMethods.get(position);

        holder.tvName.setText(method.getName());
        Glide.with(context).load(method.getImgUrl()).into(holder.ivLogo);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(method);
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.img_payment);
            tvName = itemView.findViewById(R.id.tv_payment_name);
        }
    }
}
