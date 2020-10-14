package com.lib.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lib.R;
import com.lib.database.UniGeDataBaseRepo;

import java.util.ArrayList;

public class LibrarianTrackingStudentHistoryFragment extends Fragment {
    TextView txtHeading, txtWelcome, txtGmail, txtFName, txtLName,
            txtNoOfBooks,txtStudDetails,txtBtnHistoryHeading, txtBookHistoryAvailability;
    Button btnGetStudHistory;

    ListView listViewBookHistory;
    UniGeDataBaseRepo db;
    Cursor curStudentsDB, curBookSearchDB, curBooksDb, curBookReservationDB;

    String  studIdStr = null, gmailStr = null,
            fNameStr = null, lNameStr = null, NoOfBooksStr = null,
            bookIsbnStr = null, bookReservedStr = null, bookNameStr = null,
            dOfIssueStr = null, dOReturnStr = null;
    LinearLayout linearLayoutStudBooksHistory;

    StudBookHistoryAdapter studBookHistoryAdapter;

    ArrayList<String> listBookName = null;
    ArrayList<String> listBookISBN = null;
    ArrayList<String> listDOIssue = null;
    ArrayList<String> listDOReturn = null;
    ArrayList<String> listSNo = null;

    int counterBookHistory = 0, counterBookSearch = 0,
            counterStudentDetails = 1;

    TableLayout tableStudDetails;

    Typeface faceHeadings;

    SharedPreferences preferences;
    EditText edtStudId;
    View SplitLine_hor2, SplitLine_hor1;
    private LibrarianTrackingStudentHistoryViewModel trackingStudentHistoryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackingStudentHistoryViewModel = ViewModelProviders.of(this).get(LibrarianTrackingStudentHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trackingstudhistory, container, false);
        db = new UniGeDataBaseRepo(getActivity().getApplicationContext());
        db.openDB();

        preferences = getActivity().getApplicationContext().getSharedPreferences(
                "libAppPref", 0);

        txtGmail = root.findViewById(R.id.txt_stud_gmail);
        txtFName = root.findViewById(R.id.txt__stud_fname);
        txtLName = root.findViewById(R.id.txt_stud_lname);
        txtNoOfBooks = root.findViewById(R.id.txt_stud_no_books);
        txtStudDetails = root.findViewById(R.id.txt_stud_details);
        txtBtnHistoryHeading = root.findViewById(R.id.txt_btn_history_heading);


        edtStudId = root.findViewById(R.id.edt_lib_stud_id);
        linearLayoutStudBooksHistory = root.findViewById(R.id.linear_layout_stud_books_history);
        SplitLine_hor2 = root.findViewById(R.id.SplitLine_hor2);
        SplitLine_hor1 = root.findViewById(R.id.SplitLine_hor1);
        tableStudDetails = root.findViewById(R.id.table_stud_details);
        txtBookHistoryAvailability = root.findViewById(R.id.txt_book_history_availability);


        linearLayoutStudBooksHistory.setVisibility(View.GONE);
        SplitLine_hor2.setVisibility(View.GONE);
        SplitLine_hor1.setVisibility(View.GONE);
        tableStudDetails.setVisibility(View.GONE);
        txtBookHistoryAvailability.setVisibility(View.GONE);
        txtStudDetails.setVisibility(View.GONE);
        txtBtnHistoryHeading.setVisibility(View.GONE);
        edtStudId.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                linearLayoutStudBooksHistory.setVisibility(View.GONE);
                SplitLine_hor2.setVisibility(View.GONE);
                SplitLine_hor1.setVisibility(View.GONE);
                tableStudDetails.setVisibility(View.GONE);
                txtBookHistoryAvailability.setVisibility(View.GONE);
                txtStudDetails.setVisibility(View.GONE);
                txtBtnHistoryHeading.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        listViewBookHistory = root.findViewById(R.id.listView_stud_book_history);
        studBookHistoryAdapter = new StudBookHistoryAdapter(getContext());

        btnGetStudHistory = root.findViewById(R.id.btn_get_stud_history);
        // getStudentDetailsFromDB();

        btnGetStudHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtStudId.getText().toString().trim().length() == 0) {
                    new AlertDialog.Builder(getContext()).setTitle("Please enter student id").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else {
                    studIdStr = edtStudId.getText().toString().trim();
                    getStudentDetailsFromDB();
                }

            }
        });
        return root;
    }

    public void getStudentDetailsFromDB() {
        curStudentsDB = db.getStudentDetails(studIdStr);

        if (curStudentsDB.getCount() > 0) {
            if (curStudentsDB.moveToFirst()) {
                do {
                    gmailStr = curStudentsDB.getString(curStudentsDB
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

        listBookName = new ArrayList<String>();
        listBookISBN = new ArrayList<String>();
        listDOIssue = new ArrayList<String>();
        listDOReturn = new ArrayList<String>();
        listSNo = new ArrayList<String>();

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
                listBookName.add(bookNameStr);
                listBookISBN.add(bookIsbnStr);
                listDOIssue.add(bookReservedStr);
                listDOReturn.add(bookReservedStr);

            } while (curBookReservationDB.moveToNext());
        }
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

        txtGmail.setText(": " + gmailStr);
        txtFName.setText(": " + fNameStr);
        txtLName.setText(": " + lNameStr);
        txtNoOfBooks.setText(": " + NoOfBooksStr);

        if (curStudentsDB.getCount()==0) {
            new AlertDialog.Builder(getContext()).setTitle("Entered Student Id is not a Registered Student").
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        } else
        {
            if (NoOfBooksStr.equals("0")) {
                SplitLine_hor2.setVisibility(View.VISIBLE);
                SplitLine_hor1.setVisibility(View.VISIBLE);
                linearLayoutStudBooksHistory.setVisibility(View.GONE);
                txtBookHistoryAvailability.setVisibility(View.VISIBLE);
                txtBookHistoryAvailability.setText("No books to show");
                txtStudDetails.setVisibility(View.VISIBLE);
                txtBtnHistoryHeading.setVisibility(View.VISIBLE);
                tableStudDetails.setVisibility(View.VISIBLE);
            } else {
                SplitLine_hor2.setVisibility(View.VISIBLE);
                SplitLine_hor1.setVisibility(View.VISIBLE);
                linearLayoutStudBooksHistory.setVisibility(View.VISIBLE);
                tableStudDetails.setVisibility(View.VISIBLE);
                txtBookHistoryAvailability.setVisibility(View.GONE);
                txtStudDetails.setVisibility(View.VISIBLE);
                txtBtnHistoryHeading.setVisibility(View.VISIBLE);
            }

            listViewBookHistory.setAdapter(studBookHistoryAdapter);
            studBookHistoryAdapter.notifyDataSetChanged();
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