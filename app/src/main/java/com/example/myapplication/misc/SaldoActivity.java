package com.example.myapplication.misc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SaldoActivity extends AppCompatActivity {

    private TextView tvTotalSaldo;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Button isiSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo); // Make sure your XML is named activity_saldo.xml

        // Initialize views
        tvTotalSaldo = findViewById(R.id.tvTotalSaldo);
        isiSaldo = findViewById(R.id.btnIsiSaldo);
        isiSaldo.setOnClickListener(v -> {
            Intent intent = new Intent(SaldoActivity.this, TopUpActivity.class);
            startActivity(intent);
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("profile");

        fetchUserSaldo();
    }

    private void fetchUserSaldo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userEmail = currentUser.getEmail();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null && email.equals(userEmail)) {
                        Double money = userSnapshot.child("money").getValue(Double.class);
                        if (money != null) {
                            tvTotalSaldo.setText("Rp " + String.format("%,.2f", money));
                        } else {
                            tvTotalSaldo.setText("Rp 0,00");
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(SaldoActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                    tvTotalSaldo.setText("Rp 0,00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SaldoActivity.this, "Failed to load saldo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
