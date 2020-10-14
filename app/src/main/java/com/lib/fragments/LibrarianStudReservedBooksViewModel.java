package com.lib.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LibrarianStudReservedBooksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LibrarianStudReservedBooksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is student reserved books fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}