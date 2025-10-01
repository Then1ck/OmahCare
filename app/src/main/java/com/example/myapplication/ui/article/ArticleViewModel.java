package com.example.myapplication.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArticleViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ArticleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is article fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}