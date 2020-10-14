package com.lib.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LibrarianTrackingStudentHistoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LibrarianTrackingStudentHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is student history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}