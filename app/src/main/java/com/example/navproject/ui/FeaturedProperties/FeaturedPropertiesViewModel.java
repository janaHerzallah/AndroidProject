package com.example.navproject.ui.FeaturedProperties;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FeaturedPropertiesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public FeaturedPropertiesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to Featured Properties!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
