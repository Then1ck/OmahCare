package com.example.myapplication.main_func.home_care;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeCareSelectActivity extends AppCompatActivity {

    private RecyclerView caregiverRecyclerView;
    private HomeCareSelectAdapter caregiverAdapter;
    private List<HomeCareModel> caregiverList;
    private List<HomeCareModel> filteredList;

    private DatabaseReference caregiverRef;
    private SearchView searchView;

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_select);

        // 🔹 Receive data (optional for next steps)
        Intent intent = getIntent();
        String lansiaId = null, name = null, age = null, gender = null,
                religion = null, relation = null, complaint = null;
        int durationDays = 0;

        if (intent != null) {
            lansiaId = intent.getStringExtra("lansiaId");
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            religion = intent.getStringExtra("religion");
            relation = intent.getStringExtra("relation");
            complaint = intent.getStringExtra("complaint");
            durationDays = intent.getIntExtra("durationDays", 0);
        }

        btnBack = findViewById(R.id.backIcon);

        btnBack.setOnClickListener(v -> finish());

        // 🔹 Setup RecyclerView
        caregiverRecyclerView = findViewById(R.id.caregiverRecyclerView);
        caregiverRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        caregiverRecyclerView.setHasFixedSize(true);

        // 🔹 Initialize lists and adapter
        caregiverList = new ArrayList<>();
        filteredList = new ArrayList<>();

        caregiverAdapter = new HomeCareSelectAdapter(this, filteredList);
        caregiverAdapter.setExtraData(
                lansiaId, name, age, gender, religion, relation, complaint, durationDays
        );
        caregiverRecyclerView.setAdapter(caregiverAdapter);


        // 🔹 Firebase reference
        caregiverRef = FirebaseDatabase.getInstance().getReference("caregiver");

        // 🔹 Load caregivers
        loadCaregiversFromFirebase();

        // 🔹 SearchView setup
        searchView = findViewById(R.id.searchBar);
        setupSearch();
    }

    private void loadCaregiversFromFirebase() {
        caregiverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                caregiverList.clear();
                filteredList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    HomeCareModel caregiver = data.getValue(HomeCareModel.class);
                    if (caregiver != null) {
                        caregiver.setCaregiverId(data.getKey()); // ✅ store Firebase key as caregiverId
                        caregiverList.add(caregiver);
                    }
                }

                // Initially display all caregivers
                filteredList.addAll(caregiverList);
                caregiverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load caregivers: " + error.getMessage());
                Toast.makeText(HomeCareSelectActivity.this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCaregivers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCaregivers(newText);
                return false;
            }
        });
    }

    private void filterCaregivers(String text) {
        filteredList.clear();
        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(caregiverList);
        } else {
            String query = text.toLowerCase();
            for (HomeCareModel caregiver : caregiverList) {
                if (caregiver.getName().toLowerCase().contains(query)
                        || String.valueOf(caregiver.getExp()).toLowerCase().contains(query)
                        || String.valueOf(caregiver.getDist()).toLowerCase().contains(query)
                        || String.valueOf(caregiver.getCost()).toLowerCase().contains(query)) {
                    filteredList.add(caregiver);
                }
            }
        }
        caregiverAdapter.notifyDataSetChanged();
    }
}
