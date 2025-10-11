package com.example.myapplication.system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnboardingActivity extends AppCompatActivity {

    private ImageView bgImage;
    private TextView titleText, subtitleText;
    private Button startButton;
    private DatabaseReference startRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        bgImage = findViewById(R.id.bgImage);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        startButton = findViewById(R.id.startButton);

        // Firebase reference to "start/start_1"
        startRef = FirebaseDatabase.getInstance().getReference("start").child("start_1");

        // Load data from Firebase
        startRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String title = snapshot.child("pitch").getValue(String.class);
                    String desc = snapshot.child("desc").getValue(String.class);
                    String imgUrl = snapshot.child("img").getValue(String.class);

                    titleText.setText(title);
                    subtitleText.setText(desc);

                    // Load image with Glide
                    Glide.with(OnboardingActivity.this)
                            .load(imgUrl)
                            .centerCrop()
                            .into(bgImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Start button click: navigate to HomeCare (or main activity)
        startButton.setOnClickListener(v -> {
             Intent intent = new Intent(OnboardingActivity.this, ActivitySignUp.class);
             startActivity(intent);
             finish();
        });
    }
}
