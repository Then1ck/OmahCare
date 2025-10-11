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

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;
    private EditText etMessage;
    private ImageView btnBack, btnSend;

    private DatabaseReference dbRef;
    private String currentUserEmail;           // filled from FirebaseAuth
    private String userNode;                   // profile node key for current user (e.g., "user_1")
    private String chatWith, chatWithId;                   // receiver identifier (should be seller key ideally)
    private ImageView imgSeller;
    private TextView tvSellerName, tvSellerLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // --- IMPORTANT: set class field, do NOT redeclare local variable ---
        chatWith = getIntent().getStringExtra("chat_with_name"); // assign to class field
        chatWithId = getIntent().getStringExtra("chat_with_id"); // assign to class field

        if (chatWith != null) setTitle("Chat with " + chatWith);
        if (chatWithId == null) {
            Log.e("ChatActivity", "No seller ID passed!");
            finish();
            return;
        }
        this.chatWithId = chatWithId;


        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        imgSeller = findViewById(R.id.profileImage);
        tvSellerName = findViewById(R.id.tvShopName);
        tvSellerLocation = findViewById(R.id.tvOnlineStatus);


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
            Log.d(TAG, "Signed in user: " + currentUserEmail);
        } else {
            Toast.makeText(this, "No user signed in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // load user profile node -> set userNode and then load chat
        loadUserChat();
        if (chatWith != null) {
            loadSellerInfo(chatWithId);
        }


        // send button
        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (msg.isEmpty()) return;

            if (userNode == null) {
                Toast.makeText(this, "User profile not resolved yet. Try again shortly.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatWith == null) {
                Toast.makeText(this, "Chat target unknown.", Toast.LENGTH_SHORT).show();
                return;
            }

            // optimistic UI update
            String timeNow = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            ChatMessage optimistic = new ChatMessage(msg, true, timeNow);
            chatList.add(optimistic);
            chatAdapter.notifyItemInserted(chatList.size() - 1);
            recyclerChat.scrollToPosition(chatList.size() - 1);

            // send to firebase
            sendMessage(userNode, chatWith, msg);
            etMessage.setText("");
        });
    }

    private void loadUserChat() {
        // do not shadow the class userNode (no "String userNode = ...")
        userNode = null;

        dbRef.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Find the user by email and set the class field userNode
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    if (email != null && email.equals(currentUserEmail)) {
                        userNode = userSnap.getKey();
                        break;
                    }
                }

                if (userNode != null) {
                    Log.d(TAG, "Found userNode: " + userNode);
                    loadChatMessages(userNode);
                } else {
                    Log.e(TAG, "User not found for email: " + currentUserEmail);
                    Toast.makeText(ChatActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error reading profile: " + error.getMessage());
            }
        });
    }

    private void loadChatMessages(String userNode) {
        // If chatWith is provided, only observe that conversation; otherwise observe all chats.
        DatabaseReference chatRef = dbRef.child("profile")
                .child(userNode)
                .child("chat")
                .child(chatWithId);


        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                // snapshot children will either be messages and a "to" field (if a single conversation),
                // or multiple conversation nodes each containing messages.
                // If chatWith != null we already pointed to the specific conversation so snapshot children are message nodes.
                if (chatWith != null) {
                    for (DataSnapshot messageSnap : snapshot.getChildren()) {
                        String key = messageSnap.getKey();
                        if ("to".equals(key)) continue;

                        String from = messageSnap.child("from").getValue(String.class);
                        String msg = messageSnap.child("msg").getValue(String.class);
                        String time = messageSnap.child("time").getValue(String.class);

                        if (msg != null && from != null) {
                            boolean isUser = from.equals(userNode);
                            chatList.add(new ChatMessage(msg, isUser, time));
                        }
                    }
                } else {
                    // load all conversations
                    for (DataSnapshot chatSessionSnap : snapshot.getChildren()) {
                        for (DataSnapshot messageSnap : chatSessionSnap.getChildren()) {
                            if ("to".equals(messageSnap.getKey())) continue;

                            String from = messageSnap.child("from").getValue(String.class);
                            String msg = messageSnap.child("msg").getValue(String.class);
                            String time = messageSnap.child("time").getValue(String.class);

                            if (msg != null && from != null) {
                                boolean isUser = from.equals(userNode);
                                chatList.add(new ChatMessage(msg, isUser, time));
                            }
                        }
                    }
                }

                chatAdapter.notifyDataSetChanged();
                if (!chatList.isEmpty()) {
                    recyclerChat.scrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading chats: " + error.getMessage());
            }
        });
    }

    private void sendMessage(String userNode, String receiverName, String message) {
        // userNode = current user's profile key (user_x)
        // receiverName should be the seller key (e.g., "seller_1") for consistent DB paths.
        DatabaseReference userChatRef = dbRef.child("profile").child(userNode).child("chat").child(chatWithId);
        DatabaseReference receiverChatRef = dbRef.child("seller").child(chatWithId).child("chat").child(userNode);

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("from", userNode);
        messageData.put("msg", message);
        messageData.put("time", time);

        String key = userChatRef.push().getKey();
        if (key != null) {
            userChatRef.child(key).setValue(messageData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Message saved to profile node"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save message to profile: " + e.getMessage()));

            receiverChatRef.child(key).setValue(messageData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Message mirrored to seller node"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to mirror message to seller: " + e.getMessage()));
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSellerInfo(String sellerId) {
        DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("seller").child(sellerId);

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String location = snapshot.child("kota").getValue(String.class);
                String imageUrl = snapshot.child("img").getValue(String.class);

                if (name != null) tvSellerName.setText(name);
                if (location != null) tvSellerLocation.setText(location);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // requires Glide dependency (already in your project)
                    Glide.with(ChatActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_caregiver)
                            .circleCrop()
                            .into(imgSeller);
                } else {
                    imgSeller.setImageResource(R.drawable.ic_caregiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load seller info: " + error.getMessage());
            }
        });
    }

}
