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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;

    public PaymentAdapter(Context context, List<PaymentMethod> paymentMethods) {
        this.context = context;
        this.paymentMethods = paymentMethods;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentMethod method = paymentMethods.get(position);
        holder.name.setText(method.getName());

        Glide.with(context)
                .load(method.getImgUrl())
                .placeholder(R.drawable.ic_home_black_24dp)
                .into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.img_payment);
            name = itemView.findViewById(R.id.tv_payment_name);
        }
    }
}
