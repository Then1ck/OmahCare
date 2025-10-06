package com.example.myapplication.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogIn extends AppCompatActivity {

    // Views
    private EditText emailInput, passwordInput;
    private CheckBox rememberCheckbox;
    private Button loginButton, googleLoginButton;
    private TextView signUpHere, forgotPassword;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Bind views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
//        rememberCheckbox = findViewById(R.id.terms_checkbox);
        loginButton = findViewById(R.id.sign_in_button);
        googleLoginButton = findViewById(R.id.google_sign_in_button);
        signUpHere = findViewById(R.id.sign_up_here);
        forgotPassword = findViewById(R.id.forgot_password);

        // Handle normal email login
        loginButton.setOnClickListener(v -> handleEmailLogin());

        // Handle Google login (optional, if you add Google Sign-In later)
        googleLoginButton.setOnClickListener(v ->
                Toast.makeText(this, "Google login not yet implemented", Toast.LENGTH_SHORT).show()
        );

        // Navigate to Sign-Up
        signUpHere.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityLogIn.this, ActivitySignUp.class);
            startActivity(intent);
        });

        // Forgot password handler
        forgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User already logged in, go to main screen
            startHomeActivity();
        }
    }

    private void handleEmailLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Sign In
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startHomeActivity();
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleForgotPassword() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startHomeActivity() {
        Intent intent = new Intent(ActivityLogIn.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
