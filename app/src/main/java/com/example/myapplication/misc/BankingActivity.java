package com.example.myapplication.misc;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BankingActivity extends AppCompatActivity {

    private ImageView btnBack, bankLogo;
    private TextView tvTitle, tvBankName;
    private LinearLayout layoutServices;

    private DatabaseReference userRef;
    private static final int BONUS_AMOUNT = 1_000_000; // 💰 amount per click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banking);

        // Bind views
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        bankLogo = findViewById(R.id.imgBankLogo);
        tvBankName = findViewById(R.id.tvBankName);
        layoutServices = findViewById(R.id.layoutServices);

        // Get data from Intent
        String bankName = getIntent().getStringExtra("name");
        String bankImg = getIntent().getStringExtra("img");

        // Update UI dynamically
        tvTitle.setText(bankName);
        tvBankName.setText(bankName);
        Glide.with(this).load(bankImg).into(bankLogo);

        // Firebase reference (change "user_1" if dynamic user is used)
        userRef = FirebaseDatabase.getInstance()
                .getReference("profile")
                .child("user_1");

        // Back button — normal behavior
        btnBack.setOnClickListener(v -> onBackPressed());

        // 🟢 Add click listeners to all service options dynamically
        addClickListenersToServices(layoutServices);
    }

    private void addClickListenersToServices(ViewGroup parent) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            // Only attach listener to layouts (each service line)
            if (child instanceof LinearLayout) {
                child.setOnClickListener(v -> addMoney(BONUS_AMOUNT));
            }
        }
    }

    private void addMoney(int amount) {
        userRef.child("money").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double currentMoney = 0;
                if (snapshot.exists() && snapshot.getValue() instanceof Number) {
                    currentMoney = ((Number) snapshot.getValue()).doubleValue();
                }

                double newMoney = currentMoney + amount;

                userRef.child("money").setValue(newMoney)
                        .addOnSuccessListener(aVoid -> Toast.makeText(BankingActivity.this,
                                "Added Rp " + amount + "! New total: Rp " + newMoney,
                                Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(BankingActivity.this,
                                "Failed to update money: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BankingActivity.this,
                        "Database error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
