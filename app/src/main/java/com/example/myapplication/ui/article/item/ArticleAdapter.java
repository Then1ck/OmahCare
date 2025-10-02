package com.example.myapplication.ui.article.item;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        holder.subtitle.setText(article.getSubtext());
        holder.authorName.setText(article.getAuthor());
        holder.date.setText(article.getDate());

        Glide.with(holder.itemView.getContext())
                .load(article.getImg())
                .placeholder(R.drawable.ic_caregiver)
                .into(holder.articleImage);

        Glide.with(holder.itemView.getContext())
                .load(article.getPfp())
                .placeholder(R.drawable.ic_caregiver)
                .circleCrop()
                .into(holder.authorAvatar);

        // CLICK → go to ArticleReadActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ArticleReadActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("author", article.getAuthor());
            intent.putExtra("date", article.getDate());
            intent.putExtra("subtext", article.getSubtext());
            intent.putExtra("img", article.getImg());
            intent.putExtra("pfp", article.getPfp());
            intent.putExtra("content", article.getContent());
            holder.itemView.getContext().startActivity(intent);
        });
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
