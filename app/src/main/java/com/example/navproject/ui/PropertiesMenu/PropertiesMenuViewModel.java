package com.example.navproject.ui.PropertiesMenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PropertiesMenuViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PropertiesMenuViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}