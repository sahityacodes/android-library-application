package com.lib.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lib.R;
import com.lib.database.UniGeDataBaseRepo;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    String NoOfBooksStr=null,studIdStr;
    int counterBookHistory = 0, counterBookSearch = 0,
            counterStudentDetails = 0;
    Cursor curStudentsDB, curBookSearchDB,curBookReservationDB,curBooksDb;
    ListView listViewBookHistory;
    UniGeDataBaseRepo db;
    SharedPreferences preferences;
    ArrayList<String> listBookName = null;
    ArrayList<String> listBookISBN = null;
    ArrayList<String> listDOIssue = null;
    ArrayList<String> listDOReturn = null;
    ArrayList<String> listSNo = null;
    StudBookHistoryAdapter studBookHistoryAdapter;
    TextView nohistory;
    HorizontalScrollView scrollview_stud;

    String  bookIsbnStr = null, bookReservedStr = null, bookNameStr = null,
            dOfIssueStr = null, dOReturnStr = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        db = new UniGeDataBaseRepo(getActivity().getApplicationContext());
        db.openDB();
        scrollview_stud = root.findViewById(R.id.scrollview_stud);
        nohistory = root.findViewById(R.id.nohistory);
        listViewBookHistory = root.findViewById(R.id.listView_stud_book_history);
        studBookHistoryAdapter = new StudBookHistoryAdapter(getActivity().getApplicationContext());
        listBookName = new ArrayList<String>();
        listBookISBN = new ArrayList<String>();
        listDOIssue = new ArrayList<String>();
        listDOReturn = new ArrayList<String>();
        listSNo = new ArrayList<String>();
        preferences = getActivity().getApplicationContext().getSharedPreferences(
                "libAppPref", 0);
        studIdStr = preferences.getString("username", "");
        getStudentDetailsFromDB();

        for (int i = 0; i < Integer.valueOf(NoOfBooksStr); i++) {
            listSNo.add((i + 1) + ".");
        }
        if (curBookReservationDB.moveToFirst()) {
            do {
                bookIsbnStr = curBookReservationDB
                        .getString(curBookReservationDB
                                .getColumnIndex(curBookReservationDB
                                        .getColumnName(0)));
                bookReservedStr = curBookReservationDB
                        .getString(curBookReservationDB
                                .getColumnIndex(curBookReservationDB
                                        .getColumnName(2)));
                curBooksDb = db.getBookNames(bookIsbnStr);

                if (curBooksDb.getCount() > 0) {
                    if (curBooksDb.moveToFirst()) {
                        do {
                            bookNameStr = curBooksDb
                                    .getString(curBooksDb
                                            .getColumnIndex(curBooksDb
                                                    .getColumnName(0)));
                        } while (curBooksDb.moveToNext());
                    }
                }
                System.out.println(bookNameStr);
                listBookName.add(bookNameStr);
                listBookISBN.add(bookIsbnStr);
                listDOIssue.add(bookReservedStr);
                listDOReturn.add("Not issued yet");
            } while (curBookReservationDB.moveToNext());
        }
        // to get all books of a student present in the BOOK_SEARCH data base
        if (curBookSearchDB.moveToFirst()) {
            do {
                bookIsbnStr = curBookSearchDB.getString(curBookSearchDB
                        .getColumnIndex(curBookSearchDB.getColumnName(0)));
                dOfIssueStr = curBookSearchDB.getString(curBookSearchDB
                        .getColumnIndex(curBookSearchDB.getColumnName(1)));
                dOReturnStr = curBookSearchDB.getString(curBookSearchDB
                        .getColumnIndex(curBookSearchDB.getColumnName(2)));

                curBooksDb = db.getBookNames(bookIsbnStr);

                if (curBooksDb.getCount() > 0) {
                    if (curBooksDb.moveToFirst()) {
                        do {
                            bookNameStr = curBooksDb
                                    .getString(curBooksDb
                                            .getColumnIndex(curBooksDb
                                                    .getColumnName(0)));
                        } while (curBooksDb.moveToNext());
                    }
                }
                listBookName.add(bookNameStr);
                listBookISBN.add(bookIsbnStr);
                listDOIssue.add(dOfIssueStr);
                listDOReturn.add(dOReturnStr);
            } while (curBookSearchDB.moveToNext());
        }
        listViewBookHistory.setAdapter(studBookHistoryAdapter);
        studBookHistoryAdapter.notifyDataSetChanged();
        return root;
    }

    public void getStudentDetailsFromDB() {
        curStudentsDB = db.getStudentDetails(studIdStr);
        curBookSearchDB = db.getStudentBookHistory(studIdStr);
        curBookReservationDB = db.getStudentBookCountFromReservation(studIdStr);
        NoOfBooksStr = String.valueOf(curBookSearchDB.getCount()
                + curBookReservationDB.getCount());
        if (NoOfBooksStr.equals("0")) {
            nohistory.setVisibility(View.VISIBLE);
            scrollview_stud.setVisibility(View.INVISIBLE);
        } else {
            nohistory.setVisibility(View.INVISIBLE);
            scrollview_stud.setVisibility(View.VISIBLE);
        }
    }

    public class StudBookHistoryAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        private Context context;

        public StudBookHistoryAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listBookISBN.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint({ "ViewHolder", "InflateParams" })
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View myview = inflater.inflate(
                    R.layout.book_history_listview_row_items, null);

            TextView slno = myview
                    .findViewById(R.id.txt_book_history_sno);

            TextView bookName = myview
                    .findViewById(R.id.txt_book_history_book_name);

            TextView bookISBN = myview
                    .findViewById(R.id.txt_book_history_book_isbn);

            TextView dateOfIssue = myview
                    .findViewById(R.id.txt_book_history_doissue);

            TextView dateOfReturn = myview
                    .findViewById(R.id.txt_book_history_doreturn);

            slno.setText(listSNo.get(position));
            bookName.setText(listBookName.get(position));
            bookISBN.setText(listBookISBN.get(position));
            dateOfIssue.setText(listDOIssue.get(position));
            dateOfReturn.setText(listDOReturn.get(position));
            return myview;
        }

        @Override
        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();
        }
    }

}