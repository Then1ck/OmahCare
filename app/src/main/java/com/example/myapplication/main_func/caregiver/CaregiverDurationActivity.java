package com.example.myapplication.main_func.caregiver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;

public class CaregiverDurationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private CardView card3Hari, card5Hari, card10Hari, card15Hari, card30Hari;

    // Data received from previous screen
    private String lansiaId, name, age, gender, religion, relation, complaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_duration);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        card3Hari = findViewById(R.id.card_3hari);
        card5Hari = findViewById(R.id.card_5hari);
        card10Hari = findViewById(R.id.card_10hari);
        card15Hari = findViewById(R.id.card_15hari);
        card30Hari = findViewById(R.id.card_30hari);

        // Receive data from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            lansiaId = intent.getStringExtra("lansiaId");
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            religion = intent.getStringExtra("religion");
            relation = intent.getStringExtra("relation");
            complaint = intent.getStringExtra("complaint");
        }

        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Handle duration card selections
        card3Hari.setOnClickListener(v -> handleDurationSelection(3));
        card5Hari.setOnClickListener(v -> handleDurationSelection(5));
        card10Hari.setOnClickListener(v -> handleDurationSelection(10));
        card15Hari.setOnClickListener(v -> handleDurationSelection(15));
        card30Hari.setOnClickListener(v -> handleDurationSelection(30));
    }

    private void handleDurationSelection(int days) {
        Toast.makeText(this, "Durasi dipilih: " + days + " hari", Toast.LENGTH_SHORT).show();

        // ✅ Go to CaregiverSelectActivity, send all data
        Intent nextIntent = new Intent(this, CaregiverSelectActivity.class);
        nextIntent.putExtra("lansiaId", lansiaId);
        nextIntent.putExtra("name", name);
        nextIntent.putExtra("age", age);
        nextIntent.putExtra("gender", gender);
        nextIntent.putExtra("religion", religion);
        nextIntent.putExtra("relation", relation);
        nextIntent.putExtra("complaint", complaint);
        nextIntent.putExtra("durationDays", days);
        startActivity(nextIntent);
    }
}
