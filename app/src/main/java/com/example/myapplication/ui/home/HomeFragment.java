package com.example.myapplication.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.home.banner.BannerAdapter;
import com.example.myapplication.ui.home.products.Product;
import com.example.myapplication.ui.home.products.ProductAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
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

    // 🔹 Auto-scroll handler
    private final Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 Banner carousel
        bannerCarousel = root.findViewById(R.id.banner_carousel);
        bannerAdapter = new BannerAdapter(requireContext(), bannerUrls);
        bannerCarousel.setAdapter(bannerAdapter);

        changeViewPagerScrollSpeed(bannerCarousel, 1000);

        // Auto-scroll runnable
        sliderRunnable = () -> {
            if (!bannerUrls.isEmpty()) {
                int nextPos = (bannerCarousel.getCurrentItem() + 1) % bannerUrls.size();
                bannerCarousel.setCurrentItem(nextPos, true);
                sliderHandler.postDelayed(sliderRunnable, 3000); // every 3 sec
            }
        };

        // 🔹 Load banners from Firebase
        loadBannersFromFirebase();

        // 🔹 Product grid
        recyclerView = root.findViewById(R.id.recycler_recommendations);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        this.loadProductsFromFirebase();
//        List<Product> products = new ArrayList<>();
//        products.add(new Product("Tongkat jalan", "Rp 128.000", "Rp 200.000", "33%", R.drawable.ic_caregiver));
//        products.add(new Product("Tensiometer", "Rp 612.000", "Rp 700.000", "12%", R.drawable.ic_caregiver));
//        products.add(new Product("Kursi roda", "Rp 2.300.000", "Rp 2.800.000", "18%", R.drawable.ic_caregiver));
//        products.add(new Product("Masker medis", "Rp 50.000", "Rp 75.000", "33%", R.drawable.ic_caregiver));
//
//        productAdapter = new ProductAdapter(products);
//        recyclerView.setAdapter(productAdapter);

        uploadBannerDrawable();

        return root;
    }

    private void uploadBannerDrawable() {
        // 1. Get bitmap from drawable
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.article_2);
        Bitmap pfp = BitmapFactory.decodeResource(getResources(), R.drawable.pfp_2);

        // 2. Save bitmap into a temporary file (PNG)
        File tempFile = new File(requireContext().getCacheDir(), "article_2.png");
        File tempPfp = new File(requireContext().getCacheDir(), "pfp_2.png");

        try {
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            FileOutputStream outputPfp = new FileOutputStream(tempPfp);
            pfp.compress(Bitmap.CompressFormat.PNG, 100, outputPfp);
            outputPfp.flush();
            outputPfp.close();

            // 3. Define path inside Supabase bucket
            String uploadPath = "thumbnail/article_2.png";
            String uploadPfp = "profile/pfp_2.png";

            // 4. Upload in background
            new Thread(() -> {
                try {
                    String supabaseUrl = SupabaseClient.uploadImage(
                            requireContext(),
                            tempFile,
                            uploadPath
                    );

                    String supabaseUrlPfp = SupabaseClient.uploadImage(
                            requireContext(),
                            tempPfp,
                            uploadPfp
                    );

                    requireActivity().runOnUiThread(() -> {
                        Log.d("ArticleUpload", "Articles URL: " + supabaseUrl + " " + supabaseUrlPfp);
                        saveBannerToFirebase("article_2", "Article 2", supabaseUrl, supabaseUrlPfp);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBannerToFirebase(String id, String placeholder, String imgUrl, String pfpUrl) {
        DatabaseReference bannerRef = FirebaseDatabase.getInstance().getReference("article");

        Map<String, Object> bannerData = new HashMap<>();
        bannerData.put("name", placeholder);
        bannerData.put("img", imgUrl);
        bannerData.put("pfp", pfpUrl);
        bannerData.put("title", "Makanan bernutrisi untuk keluarga dan lansia");
        bannerData.put("subtext", "Makanan bergizi dengan porsi secukupnya");
        bannerData.put("author", "Kiana Reeves");
        bannerData.put("date", "18 Jul 2025");

        bannerRef.child(id).setValue(bannerData)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Item saved to Firebase!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(error ->
                        Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
                );
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

                // Start auto-scroll once data is ready
                if (!bannerUrls.isEmpty()) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to load banners: " + error.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable); // stop when leaving
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bannerUrls.isEmpty()) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    private void changeViewPagerScrollSpeed(ViewPager2 viewPager, int duration) {
        try {
            // Get RecyclerView inside ViewPager2
            RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
            Class<?> recyclerViewClass = recyclerView.getClass().getSuperclass();

            Field layoutManagerField = recyclerViewClass.getDeclaredField("mLayout");
            layoutManagerField.setAccessible(true);

            RecyclerView.LayoutManager layoutManager = (RecyclerView.LayoutManager) layoutManagerField.get(recyclerView);

            Field mSmoothScroller = layoutManager.getClass().getDeclaredField("mSmoothScroller");
            mSmoothScroller.setAccessible(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        protected int calculateTimeForScrolling(int dx) {
                            return duration; // custom duration
                        }
                    };
                    scroller.setTargetPosition(position);
                    startSmoothScroll(scroller);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProductsFromFirebase() {
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shop");
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.child("name").getValue(String.class);
                    Long priceLong = itemSnapshot.child("price").getValue(Long.class);
                    Double ratingDouble = itemSnapshot.child("rating").getValue(Double.class);
                    Double discDouble = itemSnapshot.child("disc").getValue(Double.class);
                    String imgUrl = itemSnapshot.child("img").getValue(String.class);

                    Double DiscPrice = discDouble != null?priceLong - (priceLong * discDouble) : priceLong;

                    String priceStr = priceLong != null ? "Rp " + String.format("%,d", priceLong) : "Rp -";
                    String discountStr = discDouble != null ? String.format("%d%%", (int)(discDouble * 100)) : null;
                    float rating = ratingDouble != null ? ratingDouble.floatValue() : 0f;
                    String discPrice = DiscPrice != null ? "Rp " + String.format("%,.0f", DiscPrice) : "Rp -";

                    products.add(new Product(name, priceStr, discPrice, discountStr, imgUrl, rating));
                }

                // Set up adapter
                productAdapter = new ProductAdapter(products);
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load products: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

