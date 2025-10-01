package com.example.myapplication.ui.article;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentArticleBinding;

public class ArticleFragment extends Fragment {

    private FragmentArticleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArticleViewModel articleViewModel =
                new ViewModelProvider(this).get(ArticleViewModel.class);

        binding = FragmentArticleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textArticle;
        articleViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}