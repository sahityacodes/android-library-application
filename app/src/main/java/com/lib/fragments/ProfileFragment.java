package com.lib.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lib.R;
import com.lib.database.UniGeDataBaseRepo;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    Cursor curStudentsDB, curBookSearchDB,curBookReservationDB;
    UniGeDataBaseRepo db;
    SharedPreferences preferences;
    String welcomeStr = null, studIdStr = null, emailStr = null,
            fNameStr = null, lNameStr = null, NoOfBooksStr = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        db = new UniGeDataBaseRepo(getActivity().getApplicationContext());
        db.openDB();
        preferences = getContext().getSharedPreferences(
                "libAppPref", 0);
        final TextView fname = root.findViewById(R.id.firstname);
        final TextView lname = root.findViewById(R.id.lastname);
        final TextView email = root.findViewById(R.id.emailid);
        final TextView noofBooks = root.findViewById(R.id.bookcount);
        studIdStr = preferences.getString("username", "");
        getStudentDetailsFromDB();
        fname.setText(fNameStr);
        lname.setText(lNameStr);
        email.setText(emailStr);
        noofBooks.setText(NoOfBooksStr);
        return root;
    }

    public void getStudentDetailsFromDB() {
        curStudentsDB = db.getStudentDetails(studIdStr);
        if (curStudentsDB.getCount() > 0) {
            if (curStudentsDB.moveToFirst()) {
                do {
                    emailStr = curStudentsDB.getString(curStudentsDB
                            .getColumnIndex(curStudentsDB.getColumnName(1)));
                    fNameStr = curStudentsDB.getString(curStudentsDB
                            .getColumnIndex(curStudentsDB.getColumnName(2)));
                    lNameStr = curStudentsDB.getString(curStudentsDB
                            .getColumnIndex(curStudentsDB.getColumnName(3)));
                } while (curStudentsDB.moveToNext());
            }
        }
        curBookSearchDB = db.getStudentBookHistory(studIdStr);
        curBookReservationDB = db.getStudentBookCountFromReservation(studIdStr);
        NoOfBooksStr = String.valueOf(curBookSearchDB.getCount()
                + curBookReservationDB.getCount());
    }


}