package com.example.myapplication.main_func.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.system.ActivityVerified;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalAmount;
    private Button checkoutButton;
    private ImageView btnBack;
    private CheckoutAdapter adapter;
    private ArrayList<CheckoutItem> checkoutItems = new ArrayList<>();
    private double total = 0.0;

    // Simulate currently logged in user
    private final String currentUserEmail = "pass@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartRecyclerView);
        totalAmount = findViewById(R.id.totalAmount);
        checkoutButton = findViewById(R.id.checkoutButton);
        btnBack = findViewById(R.id.backButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutAdapter(checkoutItems, this::onItemCheckedChanged);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        checkoutButton.setOnClickListener(v -> performCheckout());

        loadUserCheckout();
    }

    private void performCheckout() {
        ArrayList<CheckoutItem> checkedItems = new ArrayList<>();
        double totalCost = 0;

        // Collect checked items
        for (CheckoutItem item : checkoutItems) {
            if (item.isSelected()) {
                checkedItems.add(item);
                totalCost += item.getPrice() * item.getQuantity();
            }
        }

        if (checkedItems.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        final double finalTotalCost = totalCost; // <-- Make it effectively final

        FirebaseDatabase.getInstance().getReference("profile")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DataSnapshot userSnap : task.getResult().getChildren()) {
                            String email = userSnap.child("email").getValue(String.class);
                            if (currentUserEmail.equals(email)) {
                                String userId = userSnap.getKey();

                                // Get current money safely
                                double currentMoney = userSnap.child("money").getValue(Double.class) != null
                                        ? userSnap.child("money").getValue(Double.class)
                                        : 0.0;
                                final double newMoney = currentMoney - finalTotalCost; // effectively final

                                // Remove checked items from checkout
                                for (DataSnapshot itemSnap : userSnap.child("checkout").getChildren()) {
                                    String key = itemSnap.getValue(String.class);
                                    for (CheckoutItem cItem : checkedItems) {
                                        if (cItem.getId().equals(key)) {
                                            FirebaseDatabase.getInstance().getReference("profile")
                                                    .child(userId)
                                                    .child("checkout")
                                                    .child(itemSnap.getKey())
                                                    .removeValue();
                                        }
                                    }
                                }

                                // Deduct money
                                FirebaseDatabase.getInstance().getReference("profile")
                                        .child(userId)
                                        .child("money")
                                        .setValue(newMoney);

                                // Add to activity/shop
                                for (CheckoutItem cItem : checkedItems) {
                                    String activityKey = FirebaseDatabase.getInstance().getReference("profile")
                                            .child(userId)
                                            .child("activity")
                                            .child("shop")
                                            .push().getKey();

                                    FirebaseDatabase.getInstance().getReference("profile")
                                            .child(userId)
                                            .child("activity")
                                            .child("shop")
                                            .child(activityKey)
                                            .setValue(new PurchasedItem(cItem));
                                }

                                Toast.makeText(this, "Checkout successful", Toast.LENGTH_SHORT).show();
                                recalcTotal();
                                adapter.notifyDataSetChanged();

                                startActivity(new Intent(CheckoutActivity.this, ActivityVerified.class));
                                finish(); // optional: remove CheckoutActivity from back stack

                                break;
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load user", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadUserCheckout() {
        FirebaseDatabase.getInstance().getReference("profile")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DataSnapshot userSnap : task.getResult().getChildren()) {
                            String email = userSnap.child("email").getValue(String.class);
                            if (currentUserEmail.equals(email)) {
                                // Found the logged-in user
                                DataSnapshot checkoutSnap = userSnap.child("checkout");
                                loadCheckoutItems(checkoutSnap);
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCheckoutItems(DataSnapshot checkoutSnap) {
        FirebaseDatabase.getInstance().getReference("shop")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DataSnapshot itemSnap : checkoutSnap.getChildren()) {
                            String itemKey = itemSnap.getValue(String.class);
                            if (itemKey != null && task.getResult().hasChild(itemKey)) {
                                DataSnapshot shopItemSnap = task.getResult().child(itemKey);
                                String name = shopItemSnap.child("name").getValue(String.class);
                                String img = shopItemSnap.child("img").getValue(String.class);
                                double price = shopItemSnap.child("price").getValue(Double.class) != null
                                        ? shopItemSnap.child("price").getValue(Double.class)
                                        : 0.0;
                                double disc = shopItemSnap.child("disc").getValue(Double.class) != null
                                        ? shopItemSnap.child("disc").getValue(Double.class)
                                        : 0.0;

                                double discountedPrice = price * (1 - disc);
                                boolean hasDiscount = disc > 0;

                                CheckoutItem item = new CheckoutItem(itemKey, name, img, discountedPrice, price, hasDiscount);
                                adapter.addItem(item);
                                total += discountedPrice * item.getQuantity();

                            }
                        }

                        adapter.notifyDataSetChanged();
                        totalAmount.setText("Rp " + String.format("%.0f", total));
                    }
                });
    }

    // Callback when checkbox toggled
    private void onItemCheckedChanged(CheckoutItem item, boolean isChecked) {
        item.setSelected(isChecked);
        recalcTotal();
    }

    private void recalcTotal() {
        total = 0.0;
        for (CheckoutItem item : checkoutItems) {
            if (item.isSelected()) {
                total += item.getPrice();
            }
        }
        totalAmount.setText("Rp " + String.format("%.0f", total));
    }
}
