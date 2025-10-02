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
import java.util.List;

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

        // Dummy data (you can fetch from ViewModel later)
        List<Article> articleList = new ArrayList<>();
        articleList.add(new Article("Hidup bahagia bersama lansia-mu. Mari olahraga!",
                "Lansia sehat, keluarga bahagia",
                "Marina Steves",
                "16 Jul 2025"));
        articleList.add(new Article("Makanan bernutrisi untuk keluarga dan lansia",
                "Makanan bergizi dengan porsi se...",
                "Kiana Reeves",
                "18 Jul 2025"));
        articleList.add(new Article("Caregiver sebagai teman dan perawat lansia",
                "Caregiver tidak hanya membant...",
                "Kezia Ameiz",
                "18 Jul 2025"));

        adapter = new ArticleAdapter(articleList);
        binding.recyclerViewArticles.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
