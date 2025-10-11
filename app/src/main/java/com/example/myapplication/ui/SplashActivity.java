package com.example.myapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.system.ActivityLogIn;
import com.example.myapplication.system.ActivitySignUp;
import com.example.myapplication.R;
import com.example.myapplication.system.OnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;
                if (currentUser != null) {
                    // User is logged in
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
