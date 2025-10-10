package com.example.myapplication.system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class ActivityVerified extends AppCompatActivity {

    private ImageView backButton; // ← This is your custom ImageView back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);

        // Get reference to your custom back ImageView
        backButton = findViewById(R.id.imageView2);

        // Handle click → go back to Home or finish
        backButton.setOnClickListener(v -> goBackHome());
    }

    @Override
    public void onBackPressed() {
        // Handle the device's physical back button as well
        super.onBackPressed();
        goBackHome();
    }

    private void goBackHome() {
        // If you want to fully return to HomeActivity/MainActivity
        Intent intent = new Intent(ActivityVerified.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Or if you only want to close this screen (stay in same activity stack),
        // replace with: finish();

        finish();
    }
}
