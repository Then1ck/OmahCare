package com.example.myapplication.system;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatRiderActivity extends AppCompatActivity {

    private static final String TAG = "ChatRiderActivity";

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;
    private EditText etMessage;
    private ImageView btnBack, btnSend;

    private DatabaseReference dbRef;
    private String currentUserEmail;
    private String userNode;      // current user's profile key
    private String chatWithName;  // rider name
    private String chatWithId;    // rider Firebase key

    private ImageView imgRider;
    private TextView tvRiderName, tvRiderVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // reuse same layout

        chatWithName = getIntent().getStringExtra("chat_with_name");
        chatWithId = getIntent().getStringExtra("chat_with_id");

        if (chatWithName != null) setTitle("Chat with " + chatWithName);
        if (chatWithId == null) {
            Log.e(TAG, "No rider ID passed!");
            finish();
            return;
        }

        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        imgRider = findViewById(R.id.profileImage);
        tvRiderName = findViewById(R.id.tvShopName);
        tvRiderVehicle = findViewById(R.id.tvOnlineStatus);

        btnBack.setOnClickListener(v -> finish());

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        dbRef = FirebaseDatabase.getInstance().getReference();

        // get authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        } else {
            Toast.makeText(this, "No user signed in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserNode();
        loadRiderInfo(chatWithId);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (msg.isEmpty()) return;

            if (userNode == null) {
                Toast.makeText(this, "User profile not resolved yet. Try again shortly.", Toast.LENGTH_SHORT).show();
                return;
            }

            String timeNow = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            ChatMessage optimistic = new ChatMessage(msg, true, timeNow);
            chatList.add(optimistic);
            chatAdapter.notifyItemInserted(chatList.size() - 1);
            recyclerChat.scrollToPosition(chatList.size() - 1);

            sendMessage(userNode, chatWithId, msg);
            etMessage.setText("");
        });
    }

    private void loadUserNode() {
        dbRef.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNode = null;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String email = snap.child("email").getValue(String.class);
                    if (email != null && email.equals(currentUserEmail)) {
                        userNode = snap.getKey();
                        break;
                    }
                }

                if (userNode != null) loadChatMessages();
                else Toast.makeText(ChatRiderActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadChatMessages() {
        DatabaseReference chatRef = dbRef.child("profile").child(userNode)
                .child("chat").child(chatWithId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot msgSnap : snapshot.getChildren()) {
                    String from = msgSnap.child("from").getValue(String.class);
                    String msg = msgSnap.child("msg").getValue(String.class);
                    String time = msgSnap.child("time").getValue(String.class);

                    if (from != null && msg != null) {
                        boolean isUser = from.equals(userNode);
                        chatList.add(new ChatMessage(msg, isUser, time));
                    }
                }
                chatAdapter.notifyDataSetChanged();
                if (!chatList.isEmpty())
                    recyclerChat.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String senderNode, String receiverNode, String message) {
        DatabaseReference userChatRef = dbRef.child("profile").child(senderNode)
                .child("chat").child(receiverNode);
        DatabaseReference riderChatRef = dbRef.child("rider").child(receiverNode)
                .child("chat").child(senderNode);

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Map<String, Object> msgData = new HashMap<>();
        msgData.put("from", senderNode);
        msgData.put("msg", message);
        msgData.put("time", time);

        String key = userChatRef.push().getKey();
        if (key != null) {
            userChatRef.child(key).setValue(msgData);
            riderChatRef.child(key).setValue(msgData);
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRiderInfo(String riderId) {
        DatabaseReference riderRef = dbRef.child("rider").child(riderId);
        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String vehicle = snapshot.child("vehicle").getValue(String.class);
                String imgUrl = snapshot.child("img").getValue(String.class);

                if (name != null) tvRiderName.setText(name);
                if (vehicle != null) tvRiderVehicle.setText(vehicle);
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    Glide.with(ChatRiderActivity.this)
                            .load(imgUrl)
                            .placeholder(R.drawable.ic_caregiver)
                            .circleCrop()
                            .into(imgRider);
                } else {
                    imgRider.setImageResource(R.drawable.ic_caregiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
