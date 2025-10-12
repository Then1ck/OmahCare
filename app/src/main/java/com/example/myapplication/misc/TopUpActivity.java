package com.example.myapplication.misc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.misc.BankingActivity;
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
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        recyclerBank = findViewById(R.id.recyclerBank);
        recyclerEmoney = findViewById(R.id.recyclerEmoney);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🔹 Set up adapters with click listener
        bankAdapter = new PaymentAdapter(this, banksList, this::onPaymentMethodClicked);
        eMoneyAdapter = new PaymentAdapter(this, eMoneyList, this::onPaymentMethodClicked);

        recyclerBank.setLayoutManager(new LinearLayoutManager(this));
        recyclerBank.setAdapter(bankAdapter);

        recyclerEmoney.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmoney.setAdapter(eMoneyAdapter);

        fetchPaymentMethods();
    }

    // 🔹 Fetch data from Firebase
    private void fetchPaymentMethods() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("payment");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Banks
                if (snapshot.child("banks").exists()) {
                    for (DataSnapshot bankSnap : snapshot.child("banks").getChildren()) {
                        String name = bankSnap.child("name").getValue(String.class);
                        String img = bankSnap.child("img").getValue(String.class);
                        banksList.add(new PaymentMethod(name, img));
                    }
                    bankAdapter.notifyDataSetChanged();
                }

                // E-Money
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
                // Handle errors here
            }
        });
    }

    // 🔹 Handle item click → move to BankingActivity
    private void onPaymentMethodClicked(PaymentMethod method) {
        Intent intent = new Intent(this, BankingActivity.class);
        intent.putExtra("name", method.getName());
        intent.putExtra("img", method.getImgUrl());
        startActivity(intent);
    }
}
