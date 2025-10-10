package com.example.myapplication.main_func.shop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack;
    private TextView tvProductName, tvProductPrice, tvDiscount, tvOldPrice, tvQuantity;
    private TextView tvAlamat, tvDiscountLabel, tvTotalHargaValue, tvProteksiValue, tvTotalPembayaranValue, tvBottomTotal;
    private Button btnBuatPesanan;

    private DatabaseReference dbShop;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize UI
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvOldPrice = findViewById(R.id.tvOldPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvDiscountLabel = findViewById(R.id.tvDiscountLabel);
        tvTotalHargaValue = findViewById(R.id.tvTotalHargaValue);
        tvProteksiValue = findViewById(R.id.tvProteksiValue);
        tvTotalPembayaranValue = findViewById(R.id.tvTotalPembayaranValue);
        tvBottomTotal = findViewById(R.id.tvBottomTotal);
        btnBuatPesanan = findViewById(R.id.btnBuatPesanan);

        btnBack.setOnClickListener(v -> finish());

        // Get product name from Intent
        String productName = getIntent().getStringExtra("name");
        if (productName == null || productName.isEmpty()) {
            Toast.makeText(this, "Nama produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbShop = FirebaseDatabase.getInstance().getReference("shop");
        loadProductFromFirebase(productName);

        btnBuatPesanan.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                saveCheckoutForEmail(user.getEmail(), productName);
            } else {
                Toast.makeText(OrderActivity.this, "User belum login", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveCheckoutForEmail(String targetEmail, String productName) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile");

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    if (email != null && email.equalsIgnoreCase(targetEmail)) {
                        DatabaseReference checkoutRef = userSnap.getRef().child("checkout");

                        // Get the key of the product from /shop
                        dbShop.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot shopSnap) {
                                for (DataSnapshot itemSnap : shopSnap.getChildren()) {
                                    String name = itemSnap.child("name").getValue(String.class);
                                    if (name != null && name.equalsIgnoreCase(productName)) {
                                        String itemKey = itemSnap.getKey(); // final inside this scope

                                        // directly use itemKey here
                                        checkoutRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot checkoutSnap) {
                                                long count = checkoutSnap.getChildrenCount();
                                                String newKey = "item_" + (count + 1);
                                                checkoutRef.child(newKey).setValue(itemKey)
                                                        .addOnSuccessListener(aVoid ->
                                                                Toast.makeText(OrderActivity.this,
                                                                        "Produk ditambahkan ke checkout!", Toast.LENGTH_SHORT).show()
                                                        )
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(OrderActivity.this,
                                                                        "Gagal menambahkan: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                        );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(OrderActivity.this, "Gagal mengakses checkout", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        break; // stop loop, found the product
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(OrderActivity.this, "Gagal memuat data produk", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return; // stop looping, email found
                    }
                }
                Toast.makeText(OrderActivity.this, "User dengan email " + targetEmail + " tidak ditemukan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderActivity.this, "Gagal mengakses profil pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void loadProductFromFirebase(String productName) {
        dbShop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String name = itemSnap.child("name").getValue(String.class);
                    if (name != null && name.equalsIgnoreCase(productName)) {
                        found = true;

                        Long price = itemSnap.child("price").getValue(Long.class);
                        Double disc = itemSnap.child("disc").getValue(Double.class); // fractional (0.33)
                        String imgUrl = itemSnap.child("img").getValue(String.class);

                        updateUI(name, imgUrl, price, disc);
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(OrderActivity.this, "Produk tidak ditemukan di Firebase", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderActivity", "Firebase error: " + error.getMessage());
                Toast.makeText(OrderActivity.this, "Gagal memuat data produk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(String name, String imgUrl, Long price, Double disc) {
        if (price == null) price = 0L;
        long proteksi = 3000;

        tvProductName.setText(name);
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ic_cart)
                .into(imgProduct);

        // Handle discount
        long discountedPrice = price;
        if (disc != null && disc > 0) {
            discountedPrice = (long) (price * (1 - disc));
            tvDiscount.setText(String.format(Locale.getDefault(), "%.0f%% OFF", disc * 100));
            tvDiscount.setVisibility(View.VISIBLE);

            tvOldPrice.setText("Rp " + nf.format(price));
            tvOldPrice.setVisibility(View.VISIBLE);
        } else {
            tvDiscount.setVisibility(View.GONE);
            tvOldPrice.setVisibility(View.GONE);
        }

        // Display calculated totals
        tvProductPrice.setText("Rp " + nf.format(discountedPrice));
        tvTotalHargaValue.setText("Rp " + nf.format(discountedPrice));
        tvProteksiValue.setText("Rp " + nf.format(proteksi));

        long total = discountedPrice + proteksi;
        tvTotalPembayaranValue.setText("Rp " + nf.format(total));
        tvBottomTotal.setText("Rp " + nf.format(total));
    }
}
