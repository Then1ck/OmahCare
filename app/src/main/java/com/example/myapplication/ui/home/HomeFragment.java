package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.home.products.Product;
import com.example.myapplication.ui.home.products.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate your main layout (with header, menu, and recommendation section)
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the RecyclerView from recommendation_section.xml
        recyclerView = root.findViewById(R.id.recycler_recommendations);

        // Use a grid with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Prepare sample product data
        List<Product> products = new ArrayList<>();
        products.add(new Product("Tongkat jalan", "Rp 128.000", "Rp 200.000", "33%", R.drawable.img));
        products.add(new Product("Tensiometer", "Rp 612.000", "Rp 700.000", "12%", R.drawable.img));
        products.add(new Product("Kursi roda", "Rp 2.300.000", "Rp 2.800.000", "18%", R.drawable.img));
        products.add(new Product("Masker medis", "Rp 50.000", "Rp 75.000", "33%", R.drawable.img));
        products.add(new Product("Kursi roda", "Rp 2.300.000", "Rp 2.800.000", "18%", R.drawable.img));
        products.add(new Product("Masker medis", "Rp 50.000", "Rp 75.000", "33%", R.drawable.img));

        // Attach adapter
        productAdapter = new ProductAdapter(products);
        recyclerView.setAdapter(productAdapter);

        return root;
    }
}
