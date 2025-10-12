package com.example.myapplication.main_func.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.products.Product;
import com.example.myapplication.ui.home.products.ProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellerActivity extends AppCompatActivity {

    private ImageView sellerImage, btnBack;
    private TextView sellerName, sellerKota, sellerRating;
    private RecyclerView recyclerProducts;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private ImageButton btnChat;

    // For ChatActivity intent
    private String currentSellerKey;
    private String sellerNameGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        // Bind views
        sellerImage = findViewById(R.id.shop_logo);
        sellerName = findViewById(R.id.shop_name);
        sellerKota = findViewById(R.id.shop_location);
        sellerRating = findViewById(R.id.shop_rating);
        btnBack = findViewById(R.id.imageView4);
        btnChat = findViewById(R.id.btn_favorite);

        btnBack.setOnClickListener(v -> finish());

        recyclerProducts = findViewById(R.id.recycler_products);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerProducts.setAdapter(productAdapter);

        // Get seller name from Intent
        String sellerNameInput = getIntent().getStringExtra("seller_name");
        if (sellerNameInput == null || sellerNameInput.isEmpty()) {
            Toast.makeText(this, "Seller name not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firebase reference
        DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("seller");

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);

                    if (name != null && name.equalsIgnoreCase(sellerNameInput)) {
                        // ✅ Save seller ID and name for ChatActivity
                        currentSellerKey = child.getKey();
                        sellerNameGlobal = name;

                        // Load seller info
                        String imgUrl = child.child("img").getValue(String.class);
                        String kota = child.child("kota").getValue(String.class);
                        Double rating = child.child("rating").getValue(Double.class);

                        sellerName.setText(name);
                        sellerKota.setText(kota != null ? kota : "-");
                        sellerRating.setText(rating != null ? String.valueOf(rating) : "-");

                        if (imgUrl != null && !imgUrl.isEmpty()) {
                            Glide.with(SellerActivity.this).load(imgUrl).into(sellerImage);
                        }

                        // Load seller products
                        DataSnapshot itemsSnap = child.child("item");
                        if (itemsSnap.exists()) {
                            for (DataSnapshot itemRefSnap : itemsSnap.getChildren()) {
                                String itemId = itemRefSnap.getValue(String.class);
                                if (itemId != null) {
                                    DatabaseReference shopRef = FirebaseDatabase.getInstance()
                                            .getReference("shop")
                                            .child(itemId);

                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot productSnap) {
                                            if (productSnap.exists()) {
                                                String productName = productSnap.child("name").getValue(String.class);
                                                String img = productSnap.child("img").getValue(String.class);

                                                Double priceD = productSnap.child("price").getValue(Double.class);
                                                long price = priceD != null ? priceD.longValue() : 0;

                                                Double disc = productSnap.child("disc").getValue(Double.class);
                                                int discountPercent = disc != null ? (int) (disc * 100) : 0;
                                                long oldPrice = discountPercent > 0 ? (long) (price / (1 - disc)) : price;

                                                NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));

                                                Product product = new Product(
                                                        productName != null ? productName : "-",
                                                        "Rp " + nf.format(price),
                                                        discountPercent > 0 ? "Rp " + nf.format(oldPrice) : "",
                                                        discountPercent > 0 ? discountPercent + "%" : "",
                                                        img != null ? img : "",
                                                        0f
                                                );

                                                productList.add(product);
                                                productAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SellerActivity.this, "Error fetching product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(SellerActivity.this, "Seller not found!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // ✅ Set up chat button once seller data loaded
                    setupChatButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerActivity.this, "Firebase error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChatButton() {
        btnChat.setOnClickListener(v -> {
            if (sellerNameGlobal != null && currentSellerKey != null) {
                Intent intent = new Intent(SellerActivity.this, com.example.myapplication.system.ChatActivity.class);
                intent.putExtra("chat_with_name", sellerNameGlobal);
                intent.putExtra("chat_with_id", currentSellerKey);
                startActivity(intent);
            } else {
                Toast.makeText(SellerActivity.this, "Seller info not available yet!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
