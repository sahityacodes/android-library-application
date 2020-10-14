package com.lib.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lib.R;
import com.lib.database.UniGeDataBaseRepo;

import java.util.ArrayList;
import java.util.Calendar;

public class LibrarianStudReservedBooksFragment extends Fragment {

    private LibrarianStudReservedBooksViewModel reservedBooksViewModel;
    TextView txtWelcome, txtStudReservedHeading;
    Button btnQuit, btnGetReservedBooks;
    ListView listViewStudReservedBooks;
    UniGeDataBaseRepo db;
    Cursor curStudentsDB, curBookReservationDB, curBooksDB;

    String welcomeStr = null, librarianIdStr = null, bookReservedStr = null,
            bookIsbnStr = null, bookNameStr = null, bookImageStr = null,
            bookAuthorStr = null;
    StudReservedBookAdapter studReservedBooksAdapter;

    ArrayList<String> listBookName = null;
    ArrayList<String> listBookISBN = null;
    ArrayList<String> listBookAuthor = null;
    ArrayList<String> listBookImage = null;

    RelativeLayout relLayoutStudReservedBooksHeading;

    SharedPreferences preferences;
    EditText edtStudId;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reservedBooksViewModel = ViewModelProviders.of(this).get(LibrarianStudReservedBooksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_studreservedbooks, container, false);
        db = new UniGeDataBaseRepo(getActivity().getApplicationContext());
        db.openDB();
        preferences = getActivity().getApplicationContext().getSharedPreferences("libAppPref", 0);
        edtStudId = root.findViewById(R.id.edt_lib_stud_id);
        relLayoutStudReservedBooksHeading = root.findViewById(R.id.rel_layout_stud_reserved_books_heading);
        relLayoutStudReservedBooksHeading.setVisibility(View.GONE);
        listViewStudReservedBooks = root.findViewById(R.id.listView_book_search);
        listViewStudReservedBooks.setVisibility(View.GONE);
        studReservedBooksAdapter = new StudReservedBookAdapter(getContext()); //

        listViewStudReservedBooks
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            db.insertReservedBookDetailsIntoBookSearchDb(
                                    listBookISBN.get(position), edtStudId.getText()
                                            .toString().trim(),getDateOfIssue() ,getDateOfReturn());

                            db.deleteReservedBookDetailsFromBookReservationDb(
                                    listBookISBN.get(position), edtStudId.getText()
                                            .toString().trim());
                            db.insertReservedBookDetailsIntoStudentDueCheckDb(
                                    listBookISBN.get(position), edtStudId.getText()
                                            .toString().trim(),"na", "1" );
                            new AlertDialog.Builder(getContext()).setTitle("Issued book ISBN" +listBookISBN.get(position)+" to " +edtStudId.getText().toString().trim()).
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                            getStudentReservedBooksDetailsFromDB();
                        }
                    });
            btnGetReservedBooks = root.findViewById(R.id.btn_get_reserved_books);

            btnGetReservedBooks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edtStudId.getText().toString().trim().length() == 0) {
                        new AlertDialog.Builder(getContext()).setTitle("Please Enter Student ID").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    } else {
                        getStudentReservedBooksDetailsFromDB();
                    }
                }
            });
        return root;
    }

    public void getStudentReservedBooksDetailsFromDB() {

        String studIdStr=edtStudId.getText().toString().trim();
        curBookReservationDB = db.getStudentBookCountFromReservation(studIdStr);

        if (curBookReservationDB.getCount() > 0) {

            listBookISBN = new ArrayList<>();
            listBookName = new ArrayList<>();
            listBookAuthor = new ArrayList<>();
            listBookImage = new ArrayList<>();

            if (curBookReservationDB.moveToFirst()) {
                do {
                    bookIsbnStr = curBookReservationDB
                            .getString(curBookReservationDB
                                    .getColumnIndex(curBookReservationDB
                                            .getColumnName(0)));

                    curBooksDB = db.getFullBookDetailsBasedOnIsbn(bookIsbnStr);

                    if (curBooksDB.getCount() > 0) {
                        if (curBooksDB.moveToFirst()) {
                            do {
                                bookNameStr = curBooksDB.getString(curBooksDB
                                        .getColumnIndex(curBooksDB
                                                .getColumnName(0)));
                                bookAuthorStr = curBooksDB.getString(curBooksDB
                                        .getColumnIndex(curBooksDB
                                                .getColumnName(1)));
                                bookImageStr = curBooksDB.getString(curBooksDB
                                        .getColumnIndex(curBooksDB
                                                .getColumnName(2)));
                            } while (curBooksDB.moveToNext());
                        }
                    }

                    listBookISBN.add(bookIsbnStr);
                    listBookName.add(bookNameStr);
                    listBookAuthor.add(bookAuthorStr);
                    listBookImage.add(bookImageStr);

                } while (curBookReservationDB.moveToNext());
            }
            relLayoutStudReservedBooksHeading.setVisibility(View.VISIBLE);
            listViewStudReservedBooks.setVisibility(View.VISIBLE);
            listViewStudReservedBooks.setAdapter(studReservedBooksAdapter);
            studReservedBooksAdapter.notifyDataSetChanged();
        } else {
            relLayoutStudReservedBooksHeading.setVisibility(View.GONE);
            listViewStudReservedBooks.setVisibility(View.GONE);
            curStudentsDB = db.getStudentDetails(studIdStr);
            if (curStudentsDB.getCount()==0) {
                new AlertDialog.Builder(getContext()).setTitle("Entered Student Id is not a Registered Student").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                } else{
                new AlertDialog.Builder(getContext()).setTitle("Does not contain any reserved books for "+ edtStudId.getText().toString().trim()).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        }

    }
    private int year, month, day;
    private int sysyear, sysmonth, sysday;
    String dateOfIssueStr = null,dateOfReturnStr = null;

    public String getDateOfIssue() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        sysyear = year;
        month = c.get(Calendar.MONTH);
        sysmonth = month;
        day = c.get(Calendar.DAY_OF_MONTH);
        sysday = day;
        dateOfIssueStr = pad(month + 1) + "/" + pad(day) + "/" + year;
        return dateOfIssueStr;
    }

    public String getDateOfReturn() {
        dateOfReturnStr = pad(month + 2) + "/" + pad(day) + "/" + year;
        return dateOfReturnStr;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public class StudReservedBookAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        private Context context;

        public StudReservedBookAdapter(Context context) {
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
            View myview = inflater.inflate(R.layout.booksearch_listview_items,
                    null);

            TextView bookName = myview
                    .findViewById(R.id.txt_book_search_name);

            TextView bookISBN = myview
                    .findViewById(R.id.txt_book_search_isbn);

            TextView bookAuthor = myview
                    .findViewById(R.id.txt_book_search_author);

            ImageView bookImage = myview
                    .findViewById(R.id.img_view_book_search_image);

            bookName.setText(listBookName.get(position));
            bookISBN.setText(listBookISBN.get(position));
            bookAuthor.setText(listBookAuthor.get(position));

            String bookImageName = listBookImage.get(position);
            int id = context.getResources().getIdentifier(bookImageName,
                    "drawable", context.getPackageName());
            bookImage.setImageResource(id);

            return myview;
        }

        @Override
        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();
        }
    }
}