package com.example.myapplication.main_func.shop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.products.Product;

import java.util.List;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;

    public ShopProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.title.setText(product.getName());
        holder.price.setText(product.getPrice());
        holder.oldPrice.setText(product.getOldPrice());
        holder.discount.setText(product.getDiscount());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_caregiver)
                .error(R.drawable.ic_dashboard_black_24dp)
                .centerCrop()
                .into(holder.image);

        // ✅ Click → open ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("oldPrice", product.getOldPrice());
            intent.putExtra("discount", product.getDiscount());
            intent.putExtra("imageUrl", product.getImageUrl());
            intent.putExtra("rating", product.getRating());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView title, price, oldPrice, discount;
        final ImageView image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            oldPrice = itemView.findViewById(R.id.product_old_price);
            discount = itemView.findViewById(R.id.product_discount);
            image = itemView.findViewById(R.id.product_image);
        }
    }
}
