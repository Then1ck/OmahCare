package com.example.myapplication.main_func.shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack;
    private TextView tvProductName, tvProductPrice, tvRatingValue, tvReviewCount, tvProductDescription;
    private RecyclerView recyclerViewUlasan;
    private Button btnBeli;

    // Find the btnCart LinearLayout
    LinearLayout btnCart;

    private DatabaseReference dbShop, dbProfile, dbSeller;
    private String productName;

    // Seller views
    private LinearLayout sellerLayout;
    private ImageView sellerImage;
    private TextView sellerNameTv, sellerCityTv;
    private String sellerNameGlobal; // store seller name for passing to SellerActivity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        imgProduct = findViewById(R.id.imgProduct);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        recyclerViewUlasan = findViewById(R.id.recyclerViewUlasan);
        recyclerViewUlasan.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerViewUlasan.setNestedScrollingEnabled(false);

        sellerLayout = findViewById(R.id.seller);
        sellerImage = sellerLayout.findViewById(R.id.seller_img); // assign correct ImageView ID
        sellerNameTv = sellerLayout.findViewById(R.id.seller_name); // assign correct ID
        sellerCityTv = sellerLayout.findViewById(R.id.seller_loc); // assign correct ID


        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Set click listener
        btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });


        btnBeli = findViewById(R.id.btnBeli);
        btnBeli.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, OrderActivity.class);
            intent.putExtra("name", tvProductName.getText().toString());
            intent.putExtra("price", parsePrice(tvProductPrice.getText().toString()));
            intent.putExtra("img", getIntent().getStringExtra("img")); // or from Firebase variable
            intent.putExtra("details", tvProductDescription.getText().toString());
            startActivity(intent);
        });


        productName = getIntent().getStringExtra("name");

        if (productName == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbShop = FirebaseDatabase.getInstance().getReference("shop");
        dbProfile = FirebaseDatabase.getInstance().getReference("profile");
        dbSeller = FirebaseDatabase.getInstance().getReference("seller");

        loadProductDetails();
    }

    private long parsePrice(String text) {
        try {
            text = text.replaceAll("[^\\d]", "");
            return Long.parseLong(text);
        } catch (Exception e) {
            return 0;
        }
    }


    private void loadProductDetails() {
        dbShop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String name = itemSnap.child("name").getValue(String.class);
                    if (name != null && name.equals(productName)) {

                        // 🔹 Load product info
                        Long price = itemSnap.child("price").getValue(Long.class);
                        Double rating = itemSnap.child("rating").getValue(Double.class);
                        String imgUrl = itemSnap.child("img").getValue(String.class);
                        String details = itemSnap.child("details").getValue(String.class);
                        String sellerKey = itemSnap.child("seller").getValue(String.class);

                        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
                        tvProductName.setText(name);
                        tvProductPrice.setText("Rp " + nf.format(price));
                        tvProductDescription.setText(details);
                        tvRatingValue.setText(String.valueOf(rating != null ? rating : 0.0));

                        Glide.with(ProductDetailActivity.this)
                                .load(imgUrl)
                                .placeholder(R.drawable.ic_cart)
                                .into(imgProduct);

                        // 🔹 Load comments
                        loadComments(itemSnap.child("comments"));

                        // 🔹 Load seller
                        if (sellerKey != null)
                            loadSellerInfo(sellerKey);

                        return;
                    }
                }
                Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments(DataSnapshot commentsSnap) {
        List<Comment> commentsList = new ArrayList<>();

        if (commentsSnap.exists()) {
            for (DataSnapshot commentSnap : commentsSnap.getChildren()) {
                String actorKey = commentSnap.child("actor").getValue(String.class);
                String content = commentSnap.child("content").getValue(String.class);
                Double rating = commentSnap.child("rating").getValue(Double.class);

                // Default placeholder until we fetch profile
                Comment comment = new Comment("Anon", content != null ? content : "",
                        rating != null ? rating : 0, null);
                commentsList.add(comment);

                if (actorKey != null) {
                    dbProfile.child(actorKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot profileSnap) {
                            String name = profileSnap.child("name").getValue(String.class);
                            String pfp = profileSnap.child("pfp").getValue(String.class);

                            commentListUpdate(actorKey, name, pfp, commentsList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }
            tvReviewCount.setText("(" + commentsList.size() + " ulasan)");
        } else {
            tvReviewCount.setText("(Belum ada ulasan)");
        }

        recyclerViewUlasan.setAdapter(new CommentAdapter(ProductDetailActivity.this, commentsList));
    }

    private void commentListUpdate(String actorKey, String name, String pfp, List<Comment> list) {
        for (int i = 0; i < list.size(); i++) {
            Comment c = list.get(i);
            if (c.getActorName().equals("Anon")) {
                list.set(i, new Comment(name != null ? name : "Anon", c.getContent(), c.getRating(), pfp));
                recyclerViewUlasan.getAdapter().notifyItemChanged(i);
                break;
            }
        }
    }


    private void loadSellerInfo(String sellerKey) {
        dbSeller.child(sellerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sellerName = snapshot.child("name").getValue(String.class);
                Double rating = snapshot.child("rating").getValue(Double.class);
                String city = snapshot.child("kota").getValue(String.class);
                String imgUrl = snapshot.child("img").getValue(String.class);

                if (sellerName != null) {
                    sellerNameTv.setText(sellerName);
                    sellerCityTv.setText(city != null ? city : "-");
                    sellerNameGlobal = sellerName; // store globally for intent

                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Glide.with(ProductDetailActivity.this)
                                .load(imgUrl)
                                .placeholder(R.drawable.ic_caregiver)
                                .into(sellerImage);
                    }

                    // Make the seller layout clickable
                    sellerLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(ProductDetailActivity.this, SellerActivity.class);
                        intent.putExtra("seller_name", sellerNameGlobal);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductDetail", "Failed to load seller info: " + error.getMessage());
            }
        });
    }

}
