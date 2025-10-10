package com.example.myapplication.main_func.shop;

import android.content.Context;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.tvReviewerName.setText(comment.getActorName());
        holder.tvReviewText.setText(comment.getContent());
        holder.tvReviewRating.setText(String.valueOf(comment.getRating()));

        // You can use an icon for star or real star icons later
        Glide.with(context)
                .load(comment.getProfileImg())
                .placeholder(R.drawable.ic_caregiver)
                .circleCrop()
                .into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewText, tvReviewRating;
        ImageView ivProfile;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvReviewRating = itemView.findViewById(R.id.tvReviewRating);
            ivProfile = itemView.findViewById(R.id.star); // use 'star' ImageView for profile for now
        }
    }
}
