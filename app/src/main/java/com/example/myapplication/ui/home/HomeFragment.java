package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.home.banner.BannerAdapter;
import com.example.myapplication.ui.home.products.Product;
import com.example.myapplication.ui.home.products.ProductAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.myapplication.system.SupabaseClient;
import androidx.viewpager2.widget.ViewPager2;
import com.google.firebase.database.*;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ViewPager2 bannerCarousel;
    private BannerAdapter bannerAdapter;
    private final List<String> bannerUrls = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 Banner carousel
        bannerCarousel = root.findViewById(R.id.banner_carousel);
        bannerAdapter = new BannerAdapter(requireContext(), bannerUrls);
        bannerCarousel.setAdapter(bannerAdapter);

        // 🔹 Load banners from Firebase
        loadBannersFromFirebase();

        // 🔹 Product grid
        recyclerView = root.findViewById(R.id.recycler_recommendations);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Product> products = new ArrayList<>();
        products.add(new Product("Tongkat jalan", "Rp 128.000", "Rp 200.000", "33%", R.drawable.ic_caregiver));
        products.add(new Product("Tensiometer", "Rp 612.000", "Rp 700.000", "12%", R.drawable.ic_caregiver));
        products.add(new Product("Kursi roda", "Rp 2.300.000", "Rp 2.800.000", "18%", R.drawable.ic_caregiver));
        products.add(new Product("Masker medis", "Rp 50.000", "Rp 75.000", "33%", R.drawable.ic_caregiver));

        productAdapter = new ProductAdapter(products);
        recyclerView.setAdapter(productAdapter);

        return root;
    }

    private void loadBannersFromFirebase() {
        DatabaseReference bannersRef = FirebaseDatabase.getInstance().getReference("banners");
        bannersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerUrls.clear();
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    String imgUrl = bannerSnapshot.child("img").getValue(String.class);
                    if (imgUrl != null) {
                        bannerUrls.add(imgUrl);
                    }
                }
                bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to load banners: " + error.getMessage());
            }
        });
    }
}
