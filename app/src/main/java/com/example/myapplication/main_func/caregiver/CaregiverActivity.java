package com.example.myapplication.main_func.caregiver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CaregiverActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CaregiverAdapter adapter;
    private List<FamilyMember> members;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private ImageView btnBack;
    private String currentUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver);

        recyclerView = findViewById(R.id.familyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        members = new ArrayList<>();
        adapter = new CaregiverAdapter(members);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("profile");


        btnBack = findViewById(R.id.back_button);

        btnBack.setOnClickListener(v -> finish());

        loadFamilyDataForCurrentUser();

        // ✅ when clicking an existing member — open edit mode
        // Normal click → CaregiverDurationActivity
        adapter.setOnItemClickListener(member -> {
            Intent intent = new Intent(CaregiverActivity.this, CaregiverDurationActivity.class);
            intent.putExtra("lansiaId", member.getLansiaId());
            intent.putExtra("name", member.getName());
            intent.putExtra("age", member.getAge());
            intent.putExtra("gender", member.getGender());
            intent.putExtra("religion", member.getReligion());
            intent.putExtra("relation", member.getRelation());
            intent.putExtra("complaint", member.getComplaint());
            startActivity(intent);
        });

        // More options click → CaregiverEditActivity
        adapter.setOnMoreOptionsClickListener(member -> {
            Intent intent = new Intent(CaregiverActivity.this, CaregiverEditActivity.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("lansiaId", member.getLansiaId());
            intent.putExtra("name", member.getName());
            intent.putExtra("age", member.getAge());
            intent.putExtra("gender", member.getGender());
            intent.putExtra("religion", member.getReligion());
            intent.putExtra("relation", member.getRelation());
            intent.putExtra("complaint", member.getComplaint());
            startActivity(intent);
        });


        // ✅ “Add Patient” → open empty edit form
        ImageView addIcon = findViewById(R.id.add_icon);
        TextView addPatientText = findViewById(R.id.add_patient_text);

        View.OnClickListener openAdd = v -> {
            Intent intent = new Intent(CaregiverActivity.this, CaregiverEditActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        };

        addIcon.setOnClickListener(openAdd);
        addPatientText.setOnClickListener(openAdd);
    }

    private void loadFamilyDataForCurrentUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;
        String currentEmail = currentUser.getEmail();
        if (currentEmail == null) return;

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                members.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (email != null && email.equalsIgnoreCase(currentEmail)) {
                        currentUserKey = userSnapshot.getKey();

                        DataSnapshot lansiaGroup = userSnapshot.child("lansia");
                        for (DataSnapshot lansiaSnapshot : lansiaGroup.getChildren()) {
                            String lansiaId = lansiaSnapshot.getKey();
                            String name = lansiaSnapshot.child("name").getValue(String.class);
                            String age = lansiaSnapshot.child("age").getValue(String.class);
                            String gender = lansiaSnapshot.child("gender").getValue(String.class);
                            String religion = lansiaSnapshot.child("religion").getValue(String.class);
                            String relation = lansiaSnapshot.child("relation").getValue(String.class);
                            String complaint = lansiaSnapshot.child("complaint").getValue(String.class);

                            FamilyMember member = new FamilyMember(lansiaId, name, age);
                            member.setGender(gender);
                            member.setReligion(religion);
                            member.setRelation(relation);
                            member.setComplaint(complaint);

                            members.add(member);
                        }
                        break;
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadFamilyDataForCurrentUser();
    }

}
