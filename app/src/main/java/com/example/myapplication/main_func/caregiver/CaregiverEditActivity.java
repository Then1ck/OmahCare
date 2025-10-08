package com.example.myapplication.main_func.caregiver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CaregiverEditActivity extends AppCompatActivity {

    private EditText etNama, etUmur, etAgama, etHubungan, etKeluhan;
    private Button btnSimpan, btnLaki, btnPerempuan;
    private ImageView btnBack;

    private String selectedGender = "";
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    private boolean isEdit = false;
    private String lansiaId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caregiver_edit);

        databaseRef = FirebaseDatabase.getInstance().getReference("profile");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnLaki = findViewById(R.id.btnLaki);
        btnPerempuan = findViewById(R.id.btnPerempuan);
        etNama = findViewById(R.id.etNama);
        etUmur = findViewById(R.id.etUmur);
        etAgama = findViewById(R.id.etAgama);
        etHubungan = findViewById(R.id.etHubungan);
        etKeluhan = findViewById(R.id.etKeluhan);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        int textActive = ContextCompat.getColor(this, android.R.color.white);
        int textInactive = ContextCompat.getColor(this, android.R.color.darker_gray);

        btnLaki.setOnClickListener(v -> {
            selectedGender = "Laki-laki";
            btnLaki.setBackgroundResource(R.drawable.bg_rounded_button);
            btnPerempuan.setBackgroundResource(R.drawable.rounded_white_box);
            btnLaki.setTextColor(textActive);
            btnPerempuan.setTextColor(textInactive);
        });

        btnPerempuan.setOnClickListener(v -> {
            selectedGender = "Perempuan";
            btnPerempuan.setBackgroundResource(R.drawable.bg_rounded_button);
            btnLaki.setBackgroundResource(R.drawable.rounded_white_box);
            btnPerempuan.setTextColor(textActive);
            btnLaki.setTextColor(textInactive);
        });

        // ✅ Get extras for editing
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            lansiaId = getIntent().getStringExtra("lansiaId");
            etNama.setText(getIntent().getStringExtra("name"));
            etUmur.setText(getIntent().getStringExtra("age"));
            etAgama.setText(getIntent().getStringExtra("religion"));
            etHubungan.setText(getIntent().getStringExtra("relation"));
            etKeluhan.setText(getIntent().getStringExtra("complaint"));
            selectedGender = getIntent().getStringExtra("gender");

            if ("Laki-laki".equals(selectedGender)) btnLaki.performClick();
            else if ("Perempuan".equals(selectedGender)) btnPerempuan.performClick();
        }

        btnSimpan.setOnClickListener(v -> saveLansiaData());
    }

    private void saveLansiaData() {
        String nama = etNama.getText().toString().trim();
        String umurStr = etUmur.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String hubungan = etHubungan.getText().toString().trim();
        String keluhan = etKeluhan.getText().toString().trim();

        if (nama.isEmpty() || umurStr.isEmpty() || agama.isEmpty() || selectedGender.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data pasien!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentEmail = currentUser != null ? currentUser.getEmail() : null;
        if (currentEmail == null) {
            Toast.makeText(this, "Gagal mendapatkan akun pengguna!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    if (email != null && email.equalsIgnoreCase(currentEmail)) {
                        DatabaseReference lansiaRef = userSnap.getRef().child("lansia");

                        Map<String, Object> data = new HashMap<>();
                        data.put("name", nama);
                        data.put("age", umurStr);
                        data.put("gender", selectedGender);
                        data.put("religion", agama);
                        data.put("relation", hubungan);
                        data.put("complaint", keluhan);

                        if (isEdit && lansiaId != null) {
                            lansiaRef.child(lansiaId).updateChildren(data)
                                    .addOnSuccessListener(a -> finish());
                        } else {
                            String newId = lansiaRef.push().getKey();
                            lansiaRef.child(newId).setValue(data)
                                    .addOnSuccessListener(a -> finish());
                        }
                        return;
                    }
                }
                Toast.makeText(CaregiverEditActivity.this, "Profil tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
