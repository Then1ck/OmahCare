package com.example.myapplication.ui.article;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentArticleBinding;
import com.example.myapplication.ui.article.item.Article;
import com.example.myapplication.ui.article.item.ArticleAdapter;

import java.util.ArrayList;

public class ArticleFragment extends Fragment {

    private FragmentArticleBinding binding;
    private ArticleAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArticleViewModel articleViewModel =
                new ViewModelProvider(this).get(ArticleViewModel.class);

        binding = FragmentArticleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        binding.recyclerViewArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArticleAdapter(new ArrayList<>());
        binding.recyclerViewArticles.setAdapter(adapter);

        // Observe Firebase data
        articleViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> {
            adapter = new ArticleAdapter(articles);
            binding.recyclerViewArticles.setAdapter(adapter);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
