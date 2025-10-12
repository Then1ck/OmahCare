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
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.R;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ActivitySignUp extends AppCompatActivity {

    // Views
    private EditText fullnameInput, emailInput, passwordInput;
    private CheckBox termsCheckbox;
    private Button signUpButton, googleSignUpButton;
    private TextView signInHere;

    // Firebase and Google
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseRef;
//    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        fullnameInput = findViewById(R.id.fullname_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        signUpButton = findViewById(R.id.sign_up_button);
        googleSignUpButton = findViewById(R.id.google_sign_up_button);
        signInHere = findViewById(R.id.sign_in_here);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        databaseRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("profile");


        // Configure Google Sign-In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id)) // set in strings.xml
//                .requestEmail()
//                .build();
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sign Up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEmailSignUp();
            }
        });

        // Google Sign Up button click
        googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handleGoogleSignUp();
            }
        });

        // Sign In Here click
        signInHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User already signed in, go to HomeActivity
            startHomeActivity();
        }
    }
    private void handleEmailSignUp() {
        String fullname = fullnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "You must agree to Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase email sign-up
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update Firebase user profile to include display name
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullname)
                                            .build())
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            writeUserProfile(fullname, email);
                                        } else {
                                            Toast.makeText(ActivitySignUp.this,
                                                    "Profile update failed: " + profileTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // ✅ Handle specific FirebaseAuth exceptions
                        Exception exception = task.getException();
                        if (exception != null) {
                            String message;
                            if (exception instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                message = "This email is already registered. Please log in instead.";
                            } else {
                                message = "Sign Up Failed: " + exception.getMessage();
                            }
                            Toast.makeText(ActivitySignUp.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



//    private void handleGoogleSignUp() {
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == RC_SIGN_IN) {
//            try {
//                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivitySignUp.this, "Google Sign-Up Successful", Toast.LENGTH_SHORT).show();
                        startHomeActivity();
                    } else {
                        Toast.makeText(ActivitySignUp.this, "Google Sign-Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void writeUserProfile(String fullname, String email) {
        // Read the current list to find the last user number
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                String userId = "user_" + (count + 1);

                DatabaseReference newUserRef = databaseRef.child(userId);
                newUserRef.child("name").setValue(fullname);
                newUserRef.child("money").setValue(10000000);
                newUserRef.child("email").setValue(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ActivitySignUp.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                startHomeActivity();
                            } else {
                                Toast.makeText(ActivitySignUp.this, "Database write failed: " +
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivitySignUp.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startHomeActivity() {
        Intent intent = new Intent(ActivitySignUp.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close sign-up activity
    }

    private void navigateToSignIn() {
        // TODO: Navigate to Sign In Activity
        Toast.makeText(this, "Navigate to Sign In screen", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ActivitySignUp.this, ActivityLogIn.class);
        startActivity(intent);
        finish();
    }
}
