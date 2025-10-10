package com.example.myapplication.main_func.shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.products.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopProductAdapter productAdapter;
    private ImageView backButton, cartButton;
    private TextView titleView;
    private SearchView searchView;

    private List<Product> allProducts = new ArrayList<>(); // all products from Firebase
    private List<Product> filteredProducts = new ArrayList<>(); // filtered results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omahmart); // your XML filename

        recyclerView = findViewById(R.id.recycler_recommendations);
        backButton = findViewById(R.id.imageView2);
        titleView = findViewById(R.id.textView4);
        searchView = findViewById(R.id.searchBar);

        cartButton = findViewById(R.id.imageView3);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });

        titleView.setText("OmahMart");

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ShopProductAdapter(this, filteredProducts);
        recyclerView.setAdapter(productAdapter);

        loadProductsFromFirebase();

        backButton.setOnClickListener(v -> finish());

        // 🔹 Search functionality
        setupSearchView();
    }

    private void loadProductsFromFirebase() {
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shop");

        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                filteredProducts.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    try {
                        String name = itemSnapshot.child("name").getValue(String.class);
                        Long priceLong = itemSnapshot.child("price").getValue(Long.class);
                        Double ratingDouble = itemSnapshot.child("rating").getValue(Double.class);
                        Double discDouble = itemSnapshot.child("disc").getValue(Double.class);
                        String imgUrl = itemSnapshot.child("img").getValue(String.class);

                        double discountedPrice = (priceLong != null && discDouble != null)
                                ? priceLong - (priceLong * discDouble)
                                : (priceLong != null ? priceLong : 0);

                        String priceStr = priceLong != null ? "Rp " + String.format("%,d", priceLong) : "Rp -";
                        String discPrice = "Rp " + String.format("%,.0f", discountedPrice);
                        String discountStr = discDouble != null ? String.format("%d%%", (int) (discDouble * 100)) : null;
                        float rating = ratingDouble != null ? ratingDouble.floatValue() : 0f;

                        Product p = new Product(name, priceStr, discPrice, discountStr, imgUrl, rating);
                        allProducts.add(p);
                    } catch (Exception e) {
                        Log.e("ShopActivity", "Error parsing product: " + e.getMessage());
                    }
                }

                // Show all products by default
                filteredProducts.addAll(allProducts);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShopActivity.this, "Failed to load products: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Product p : allProducts) {
                if (p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) {
                    filteredProducts.add(p);
                }
            }
        }

        productAdapter.notifyDataSetChanged();
    }
}
