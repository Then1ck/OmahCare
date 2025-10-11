package com.example.myapplication.ui.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> allOrders; // full list for filtering

    private FirebaseAuth mAuth;

    public ActivityFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ordersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, getContext());
        recyclerView.setAdapter(orderAdapter);

        mAuth = FirebaseAuth.getInstance();

        Button catAll = view.findViewById(R.id.cat_all);
        Button catCaregiver = view.findViewById(R.id.cat_caregiver);
        Button catHomecare = view.findViewById(R.id.cat_homecare);
        Button catOmahmart = view.findViewById(R.id.cat_omahmart);

        catAll.setOnClickListener(v -> {
            filterOrders("All");
            updateCategoryButtonStyles(catAll, catAll, catCaregiver, catHomecare, catOmahmart);
        });
        catCaregiver.setOnClickListener(v -> {
            filterOrders("Caregiver");
            updateCategoryButtonStyles(catCaregiver, catAll, catCaregiver, catHomecare, catOmahmart);
        });
        catHomecare.setOnClickListener(v -> {
            filterOrders("HomeCare");
            updateCategoryButtonStyles(catHomecare, catAll, catCaregiver, catHomecare, catOmahmart);
        });
        catOmahmart.setOnClickListener(v -> {
            filterOrders("OmahMart");
            updateCategoryButtonStyles(catOmahmart, catAll, catCaregiver, catHomecare, catOmahmart);
        });

        loadUserActivities();
    }

    private void updateCategoryButtonStyles(Button selectedButton, Button... allButtons) {
        for (Button btn : allButtons) {
            if (btn == selectedButton) {
                // Selected style
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5e89e5")));
                btn.setTextColor(Color.WHITE);
            } else {
                // Default style
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btn.setTextColor(Color.parseColor("#5e89e5"));
            }
        }
    }

    private void filterOrders(String category) {
        orderList.clear();

        if (category.equals("All")) {
            orderList.addAll(allOrders);
        } else {
            for (Order order : allOrders) {
                if (order.getCategory().equals(category)) {
                    orderList.add(order);
                }
            }
        }

        orderAdapter.notifyDataSetChanged();
    }

    private void loadUserActivities() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        final String email = currentUser.getEmail();

        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile");
        DatabaseReference caregiversRef = FirebaseDatabase.getInstance().getReference("caregiver");
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shop");

        caregiversRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                Map<String, String> caregiverNames = new HashMap<>();
                Map<String, String> caregiverImgs = new HashMap<>();
                for (DataSnapshot cSnap : caregiverSnapshot.getChildren()) {
                    String id = cSnap.getKey();
                    caregiverNames.put(id, cSnap.child("name").getValue(String.class));
                    caregiverImgs.put(id, cSnap.child("img").getValue(String.class));
                }

                shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                        Map<String, String> shopItemNames = new HashMap<>();
                        Map<String, Double> shopItemPrices = new HashMap<>();
                        Map<String, String> shopItemImgs = new HashMap<>();
                        for (DataSnapshot sSnap : shopSnapshot.getChildren()) {
                            String id = sSnap.getKey();
                            shopItemNames.put(id, sSnap.child("name").getValue(String.class));
                            shopItemPrices.put(id, sSnap.child("price").getValue(Double.class));
                            shopItemImgs.put(id, sSnap.child("img").getValue(String.class));
                        }

                        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                orderList.clear();
                                allOrders.clear();

                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userEmail = userSnapshot.child("email").getValue(String.class);
                                    if (email != null && email.equals(userEmail)) {
                                        DataSnapshot activities = userSnapshot.child("activity");
                                        DataSnapshot lansiaSnapshot = userSnapshot.child("lansia");

                                        // Caregiver
                                        DataSnapshot caregiverActivities = activities.child("caregiver");
                                        for (DataSnapshot actSnap : caregiverActivities.getChildren()) {
                                            String caregiverKey = actSnap.child("caregiver").getValue(String.class);
                                            String date = actSnap.child("date").getValue(String.class);
                                            Long durationLong = actSnap.child("duration").getValue(Long.class);
                                            String duration = durationLong != null ? String.valueOf(durationLong) : "0";
                                            String extra = actSnap.child("extra").getValue(String.class);
                                            String notes = actSnap.child("notes").getValue(String.class);
                                            String familyKey = actSnap.child("family").getValue(String.class);

                                            String caregiverName = caregiverNames.getOrDefault(caregiverKey, caregiverKey);
                                            String caregiverImg = caregiverImgs.getOrDefault(caregiverKey, "");
                                            String familyName = familyKey != null ?
                                                    lansiaSnapshot.child(familyKey).child("name").getValue(String.class)
                                                    : familyKey;

                                            Order order = new Order(
                                                    "Caregiver: " + caregiverName,
                                                    "Family: " + familyName + ", Date: " + date + ", Duration: " + duration + "h",
                                                    "Notes: " + notes + " (" + extra + ")",
                                                    caregiverImg,
                                                    "Caregiver"
                                            );
                                            orderList.add(order);
                                            allOrders.add(order);
                                        }

                                        // HomeCare
                                        DataSnapshot homecareActivities = activities.child("homecare");
                                        for (DataSnapshot actSnap : homecareActivities.getChildren()) {
                                            String caregiverKey = actSnap.child("caregiver").getValue(String.class);
                                            String date = actSnap.child("date").getValue(String.class);
                                            String extra = actSnap.child("extra").getValue(String.class);
                                            String notes = actSnap.child("notes").getValue(String.class);
                                            String familyKey = actSnap.child("family").getValue(String.class);
                                            Long typeLong = actSnap.child("type").getValue(Long.class);
                                            String type = typeLong != null ? (typeLong == 1 ? "HomeCare" : "Other") : "Unknown";

                                            String caregiverName = caregiverNames.getOrDefault(caregiverKey, caregiverKey);
                                            String caregiverImg = caregiverImgs.getOrDefault(caregiverKey, "");
                                            String familyName = familyKey != null ?
                                                    lansiaSnapshot.child(familyKey).child("name").getValue(String.class)
                                                    : familyKey;

                                            Order order = new Order(
                                                    type + ": " + caregiverName,
                                                    "Family: " + familyName + ", Date: " + date,
                                                    "Notes: " + notes + " (" + extra + ")",
                                                    caregiverImg,
                                                    "HomeCare"
                                            );
                                            orderList.add(order);
                                            allOrders.add(order);
                                        }

                                        // Shop
                                        DataSnapshot shopActivities = activities.child("shop");
                                        for (DataSnapshot actSnap : shopActivities.getChildren()) {
                                            String itemId = actSnap.child("id").getValue(String.class);
                                            Long quantityLong = actSnap.child("quantity").getValue(Long.class);
                                            int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                                            String itemName = shopItemNames.getOrDefault(itemId, itemId);
                                            Double price = shopItemPrices.getOrDefault(itemId, 0.0);
                                            String itemImg = shopItemImgs.getOrDefault(itemId, "");

                                            Order order = new Order(
                                                    "Shop Item: " + itemName,
                                                    "Quantity: " + quantity + ", Price: Rp " + price,
                                                    "",
                                                    itemImg,
                                                    "OmahMart"
                                            );
                                            orderList.add(order);
                                            allOrders.add(order);
                                        }

                                        break; // Found user
                                    }
                                }

                                orderAdapter.notifyDataSetChanged();
                                if (orderList.isEmpty()) {
                                    Toast.makeText(getContext(), "No activities found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load activities", Toast.LENGTH_SHORT).show();
                                Log.e("ActivityFragment", "Firebase error: " + error.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ActivityFragment", "Shop load error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ActivityFragment", "Caregiver load error: " + error.getMessage());
            }
        });
    }
}
