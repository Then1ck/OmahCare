package com.example.myapplication.ui.article.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.subtitle.setText(article.getSubtitle());
        holder.authorName.setText(article.getAuthor());
        holder.date.setText(article.getDate());
        // You can load images into articleImage and authorAvatar with Glide/Picasso
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, authorName, date;
        ImageView articleImage, authorAvatar;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.articleImage);
            title = itemView.findViewById(R.id.articleTitle);
            subtitle = itemView.findViewById(R.id.articleSubtitle);
            authorAvatar = itemView.findViewById(R.id.authorAvatar);
            authorName = itemView.findViewById(R.id.authorName);
            date = itemView.findViewById(R.id.articleDate);
        }
    }
}
