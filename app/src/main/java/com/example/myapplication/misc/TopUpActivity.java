package com.example.myapplication.misc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

public class TopUpActivity extends AppCompatActivity {

    private RecyclerView recyclerBank, recyclerEmoney;
    private List<PaymentMethod> banksList = new ArrayList<>();
    private List<PaymentMethod> eMoneyList = new ArrayList<>();
    private PaymentAdapter bankAdapter, eMoneyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        recyclerBank = findViewById(R.id.recyclerBank);
        recyclerEmoney = findViewById(R.id.recyclerEmoney);

        // Setup adapters
        bankAdapter = new PaymentAdapter(this, banksList);
        eMoneyAdapter = new PaymentAdapter(this, eMoneyList);

        recyclerBank.setLayoutManager(new LinearLayoutManager(this));
        recyclerBank.setAdapter(bankAdapter);

        recyclerEmoney.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmoney.setAdapter(eMoneyAdapter);

        fetchPaymentMethods();
    }

    private void fetchPaymentMethods() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("payment");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Fetch banks
                if (snapshot.child("banks").exists()) {
                    for (DataSnapshot bankSnap : snapshot.child("banks").getChildren()) {
                        String name = bankSnap.child("name").getValue(String.class);
                        String img = bankSnap.child("img").getValue(String.class);
                        banksList.add(new PaymentMethod(name, img));
                    }
                    bankAdapter.notifyDataSetChanged();
                }

                // Fetch e-money
                if (snapshot.child("e-money").exists()) {
                    for (DataSnapshot eSnap : snapshot.child("e-money").getChildren()) {
                        String name = eSnap.child("name").getValue(String.class);
                        String img = eSnap.child("img").getValue(String.class);
                        eMoneyList.add(new PaymentMethod(name, img));
                    }
                    eMoneyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
            }
        });
    }
}
