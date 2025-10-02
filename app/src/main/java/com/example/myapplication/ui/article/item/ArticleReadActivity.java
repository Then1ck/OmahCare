package com.example.myapplication.ui.article.item;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class ArticleReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_read);

        ImageView backButton = findViewById(R.id.backButton);
        ImageView banner = findViewById(R.id.articleBanner);
        ImageView pfp = findViewById(R.id.authorAvatar);
        TextView title = findViewById(R.id.articleTitle);
        TextView authorInfo = findViewById(R.id.authorInfoText);
        TextView content = findViewById(R.id.articleContent);

        // 🔙 Back button handler
        backButton.setOnClickListener(v -> onBackPressed());

        // Get data from intent
        Intent intent = getIntent();
        String titleStr = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String date = intent.getStringExtra("date");
        String contentStr = intent.getStringExtra("content");
        String img = intent.getStringExtra("img");
        String pfpUrl = intent.getStringExtra("pfp");

        // Set values
        title.setText(titleStr);
        authorInfo.setText(author + " • " + date);
        content.setText(contentStr);

        Glide.with(this).load(img).into(banner);
        Glide.with(this).load(pfpUrl).circleCrop().into(pfp);
    }
}
