package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProfileBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Example: Set profile name and email (you can load these dynamically)
        TextView tvName = root.findViewById(R.id.tvName);
        TextView tvEmail = root.findViewById(R.id.tvEmail);
        ImageView btnEditProfile = root.findViewById(R.id.btnEditProfile);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            tvName.setText(name != null ? name : "No Name");
            tvEmail.setText(email != null ? email : "No Email");
        } else {
            tvName.setText("Guest");
            tvEmail.setText("Not signed in");
        }


        // Pencil edit button click
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            // You can navigate to EditProfileFragment or open a dialog here
        });

        // Handle settings clicks
        View settingList = root.findViewById(R.id.settingsList);

        LinearLayout rowNotification = settingList.findViewWithTag("Notification");
        LinearLayout rowLanguage = settingList.findViewWithTag("Language");
        LinearLayout rowTransaction = settingList.findViewWithTag("Transaction Method");
        LinearLayout rowAbout = settingList.findViewWithTag("About OmahCare");
        LinearLayout rowHelp = settingList.findViewWithTag("Help");
        LinearLayout rowPrivacy = settingList.findViewWithTag("Privacy Policy");
        LinearLayout rowRate = settingList.findViewWithTag("Rate OmahCare");

        if (rowNotification != null) {
            rowNotification.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Notification clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowLanguage != null) {
            rowLanguage.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Language clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowTransaction != null) {
            rowTransaction.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Transaction Method clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowAbout != null) {
            rowAbout.setOnClickListener(v ->
                    Toast.makeText(getContext(), "About OmahCare clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowHelp != null) {
            rowHelp.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Help clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowPrivacy != null) {
            rowPrivacy.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Privacy Policy clicked", Toast.LENGTH_SHORT).show());
        }
        if (rowRate != null) {
            rowRate.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Rate OmahCare clicked", Toast.LENGTH_SHORT).show());
        }

        // Handle Sign Out
        LinearLayout rowSignOut = root.findViewById(R.id.rowSignOut);
        rowSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();

            // Redirect back to Sign-In or Sign-Up activity
            Intent intent = new Intent(getActivity(), com.example.myapplication.system.ActivitySignUp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
