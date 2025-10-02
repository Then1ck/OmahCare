package com.example.myapplication.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.article.item.Article;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArticleViewModel extends ViewModel {

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();

    public ArticleViewModel() {
        loadArticles();
    }

    private void loadArticles() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("article");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Article> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Article article = child.getValue(Article.class);
                    if (article != null) {
                        list.add(article);
                    }
                }
                articles.setValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }
}
