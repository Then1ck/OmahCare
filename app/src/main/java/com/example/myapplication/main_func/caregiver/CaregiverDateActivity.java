package com.example.myapplication.main_func.caregiver;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaregiverDateActivity extends AppCompatActivity {

    private GridLayout gridDays;
    private TextView tvMonthYear;
    private TextView selectedDayView = null;

    // Caregiver card UI
    private ImageView imageCaregiver;
    private TextView textName, textExperience, textDistance;
    private ImageView btnBack;

    // Notes and extra info fields
    private EditText etNotes, etExtra;
    private Button btnVerify;

    // Data
    private String caregiverName, caregiverImg, selectedDate, familyKey, caregiverId;
    private double caregiverExp, caregiverDist, caregiverCost;
    private int durationDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_date);

        // UI references
        gridDays = findViewById(R.id.gridDays);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnBack = findViewById(R.id.btn_back);

        imageCaregiver = findViewById(R.id.imageCaregiver);
        textName = findViewById(R.id.textName);
        textExperience = findViewById(R.id.textExperience);
        textDistance = findViewById(R.id.textDistance);

        etNotes = findViewById(R.id.etNotes); // ← Add EditText in XML for notes
        etExtra = findViewById(R.id.etExtra); // ← Add EditText in XML for extra info
        btnVerify = findViewById(R.id.btnVerify); // ← Add Button in XML for verification

        btnBack.setOnClickListener(v -> finish());

        // Get data passed from previous activity
        getIntentData();

        // Bind caregiver info
        bindCaregiverData();

        // Setup calendar
        setupCalendar();

        // Button to confirm booking
        btnVerify.setOnClickListener(v -> saveCaregiverActivity());
    }

    private void getIntentData() {
        if (getIntent() != null) {
            caregiverName = getIntent().getStringExtra("caregiverName");
            caregiverImg = getIntent().getStringExtra("caregiverImg");
            caregiverExp = getIntent().getDoubleExtra("caregiverExp", 0);
            caregiverDist = getIntent().getDoubleExtra("caregiverDist", 0);
            caregiverCost = getIntent().getDoubleExtra("caregiverCost", 0);
            caregiverId = getIntent().getStringExtra("caregiverId");
            familyKey = getIntent().getStringExtra("familyKey");
            durationDays = getIntent().getIntExtra("durationDays", 0);
        }
    }

    private void bindCaregiverData() {
        textName.setText(caregiverName != null ? caregiverName : "Unknown Caregiver");
        textExperience.setText(String.format(Locale.getDefault(), "%.0f tahun pengalaman", caregiverExp));
        textDistance.setText(String.format(Locale.getDefault(), "%.1f km dari lokasi anda", caregiverDist));

        if (caregiverImg != null && !caregiverImg.isEmpty()) {
            Glide.with(this)
                    .load(caregiverImg)
                    .placeholder(R.drawable.ic_caregiver)
                    .error(R.drawable.ic_caregiver)
                    .circleCrop()
                    .into(imageCaregiver);
        } else {
            imageCaregiver.setImageResource(R.drawable.ic_caregiver);
        }
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthFormat.format(calendar.getTime()));

        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        gridDays.removeAllViews();

        for (int i = 0; i < firstDayOfWeek + daysInMonth; i++) {
            TextView tv = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, 24, 0, 24);
            tv.setTextSize(16f);

            if (i >= firstDayOfWeek) {
                final int day = i - firstDayOfWeek + 1;
                tv.setText(String.valueOf(day));

                if (day == today) {
                    tv.setTypeface(null, Typeface.BOLD);
                }

                tv.setOnClickListener(v -> selectDay(tv, day, month, year));
            }

            gridDays.addView(tv);
        }
    }

    private void selectDay(TextView tv, int day, int month, int year) {
        if (selectedDayView != null) {
            selectedDayView.setBackgroundResource(android.R.color.transparent);
            selectedDayView.setTextColor(getResources().getColor(android.R.color.black));
            selectedDayView.setTypeface(null, Typeface.NORMAL);
        }

        tv.setBackgroundResource(R.drawable.bg_date_selected);
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setTypeface(null, Typeface.BOLD);
        selectedDayView = tv;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        selectedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(cal.getTime());
        Toast.makeText(this, "Picked: " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private void saveCaregiverActivity() {
        if (selectedDate == null) {
            Toast.makeText(this, "Pilih tanggal terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = etNotes.getText().toString().trim();
        String extra = etExtra.getText().toString().trim();

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Gagal menemukan akun pengguna.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile");

        // Find which user_X node matches the current email
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null && email.equals(userEmail)) {
                        DatabaseReference activityRef = userSnapshot.getRef()
                                .child("activity")
                                .child("caregiver");

                        // Find current count
                        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long count = snapshot.getChildrenCount();
                                String newKey = "caregiver_" + (count + 1);

                                Map<String, Object> newActivity = new HashMap<>();
                                newActivity.put("family", familyKey);
                                newActivity.put("caregiver", caregiverId);
                                newActivity.put("duration", durationDays);
                                newActivity.put("date", selectedDate);
                                newActivity.put("notes", notes);
                                newActivity.put("extra", extra);

                                activityRef.child(newKey).setValue(newActivity)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CaregiverDateActivity.this, "Aktivitas berhasil disimpan!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CaregiverDateActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(CaregiverDateActivity.this, "Gagal menyimpan aktivitas.", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CaregiverDateActivity.this, "Gagal membaca data aktivitas.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
                Toast.makeText(CaregiverDateActivity.this, "Profil pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CaregiverDateActivity.this, "Gagal memeriksa profil pengguna.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
