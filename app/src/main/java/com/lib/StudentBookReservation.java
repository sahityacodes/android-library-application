package com.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lib.database.UniGeDataBaseRepo;

import java.util.Calendar;

public class StudentBookReservation extends Activity {

    TextView txtBookName, txtBookISBN, txtBookAuthor,
            txtBookDescription, txtBookAvailability,
            txtBookReservationEligibility;
    UniGeDataBaseRepo db;
    Cursor curBooksDB, curStudentDueCheckDB, curStudentsDb,
            curBookReservationDb,curStudentBookCount;


    String  isbnFromPref = null,nameFromPref=null,
            bookNameStr = null, bookImageStr = null,
            bookAuthorStr = null, bookDescription = null,
            bookAvailabilityStr = null, studLibraryDueStr = null,
            bookIsbnStr=null;
    LinearLayout linearLayoutBookImg, linearLayoutBookImageText,
            linear_lay_reservation_eligibility;
    int counterBookImageShow = 0;
    private RadioButton radioBtnBookReserve; // radio button which is used to
    SharedPreferences preferences;
    ImageView imgViewBookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_resevation);
        db = new UniGeDataBaseRepo(this);

        db.openDB();

        preferences = getApplicationContext().getSharedPreferences(
                "libAppPref", 0);

        isbnFromPref = preferences.getString("isbnFrom", "");
        nameFromPref = preferences.getString("nameFrom", "");

        txtBookName = findViewById(R.id.txt_book_res_book_name);
        txtBookAuthor = findViewById(R.id.txt_book_res_book_author);
        txtBookISBN = findViewById(R.id.txt_book_res_book_isbn);
        txtBookDescription = findViewById(R.id.txt_book_res_book_description);
        txtBookAvailability = findViewById(R.id.txt_book_res_book_availability);
        txtBookReservationEligibility = findViewById(R.id.txt_book_res_book_res_eligibility);
        txtBookReservationEligibility.setVisibility(View.GONE);
        imgViewBookImage = findViewById(R.id.imgview_book_res_book_image);
        linear_lay_reservation_eligibility = findViewById(R.id.linear_lay_reservation_eligibility);
        linear_lay_reservation_eligibility.setVisibility(View.GONE);
        linearLayoutBookImg = findViewById(R.id.linear_lay_book_img);
        linearLayoutBookImageText = findViewById(R.id.lin_lay_book_image_txt);
        linearLayoutBookImageText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counterBookImageShow == 0) {
                    linearLayoutBookImg.setVisibility(View.VISIBLE);
                    counterBookImageShow = 1;
                } else {
                    linearLayoutBookImg.setVisibility(View.GONE);
                    counterBookImageShow = 0;
                }
            }
        });

        radioBtnBookReserve = findViewById(R.id.radio_btn_book_reserve);
        radioBtnBookReserve.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                reserveBook();
                new AlertDialog.Builder(StudentBookReservation.this).
                        setTitle("Book Reserved").setMessage("Your book with ISBN:"+txtBookISBN.getText().toString().trim()+
                        " is reserved, Please take the book within 48hrs")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(StudentBookReservation.this, StudentNavigation.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
            }
        });
        setBookDetails();
    }

    public void setBookDetails() {
        if (isbnFromPref.equals("booklist")) {
            txtBookISBN.setText(preferences.getString("bookSearch_isbn", "")
                    .trim());
            getBookDetailsBasedOnIsbnFromDB();
        } else if(isbnFromPref.equals("isbnInput")){
            txtBookISBN.setText(preferences.getString("bookSearch_isbn",""));
            getBookDetailsBasedOnIsbnFromDB();
        }else if(isbnFromPref.equals("nameInput")) {
            txtBookName.setText(preferences.getString("bookSearch_name",""));
            getBookDetailsBasedOnNameFromDB();
        }
    }

    public void getBookDetailsBasedOnIsbnFromDB() {
        curBooksDB = db.getFullBookDetailsBasedOnIsbn(txtBookISBN.getText()
                .toString().trim());

        if (curBooksDB.getCount() > 0) {
            if (curBooksDB.moveToFirst()) {
                do {
                    bookNameStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(0)));
                    bookAuthorStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(1)));
                    bookImageStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(2)));
                    bookDescription = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(3)));
                    bookAvailabilityStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(4)));
                } while (curBooksDB.moveToNext());
            }
            txtBookName.setText(bookNameStr.trim());
            txtBookAuthor.setText(bookAuthorStr.trim());
            int id = getApplicationContext().getResources().getIdentifier(bookImageStr,
                    "drawable", getApplicationContext().getPackageName());
            imgViewBookImage.setImageResource(id);
            txtBookDescription.setText(bookDescription);
            getBookAvailability();

        }
    }

    public void getBookDetailsBasedOnNameFromDB() {
        curBooksDB = db.getFullBookDetailsBasedOnName(txtBookName.getText()
                .toString().trim());
        if (curBooksDB.getCount() > 0) {
            if (curBooksDB.moveToFirst()) {
                do {
                    bookNameStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(0)));
                    bookIsbnStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(5)));
                    bookAuthorStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(1)));
                    bookImageStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(2)));
                    bookDescription = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(3)));
                    bookAvailabilityStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(4)));
                } while (curBooksDB.moveToNext());
            }
            txtBookName.setText(bookNameStr.trim());
            txtBookAuthor.setText(bookAuthorStr.trim());
            txtBookISBN.setText(bookIsbnStr);
            int id = getResources().getIdentifier(bookImageStr, "drawable",
                    getPackageName());
            imgViewBookImage.setImageResource(id);
            txtBookDescription.setText(bookDescription);
            getBookAvailability();

        }
    }
    // to get Book Availability
    public void getBookAvailability() {
        getBookReservationEligibility();
    }

    public void getBookReservationEligibility() {
        curStudentDueCheckDB = db.getStudentLibraryDue(
                preferences.getString("username", ""), "Fine Amt Due");
        Log.i("curStudentDueCheckDB: ", "" + curStudentDueCheckDB.getCount());
        curBookReservationDb = db
                .getStudentBookCountFromReservation(preferences.getString(
                        "username", ""));
        Log.i("curBookReservationDb: ", "" + curBookReservationDb.getCount());
        int totalBooks = db.getNumberofBooksFromBookSearchonId(preferences.getString("username", ""))
                + curBookReservationDb.getCount();
        //Book availability
        if ( bookAvailabilityStr.equals("1")) {
            txtBookAvailability.setText("Not Available in Library");
            radioBtnBookReserve.setVisibility(View.INVISIBLE);
            linear_lay_reservation_eligibility.setVisibility(View.GONE);
        }else  if (bookAvailabilityStr.equals("0")) {
            txtBookAvailability.setText("Available in the library...");
            radioBtnBookReserve.setVisibility(View.VISIBLE);
            linear_lay_reservation_eligibility.setVisibility(View.VISIBLE);
        }else if (bookAvailabilityStr.equals("2")) {
            txtBookAvailability.setText("Reserved by somebody");
            radioBtnBookReserve.setVisibility(View.INVISIBLE);
            linear_lay_reservation_eligibility.setVisibility(View.GONE);
        }
        //student eligibility
        if (curStudentDueCheckDB.getCount() > 0) {
            studLibraryDueStr = "Please pay your fine";
            radioBtnBookReserve.setVisibility(View.INVISIBLE);
            txtBookReservationEligibility.setVisibility(View.VISIBLE);
            txtBookReservationEligibility.setText(studLibraryDueStr);
        } else if(totalBooks>3){
                radioBtnBookReserve.setVisibility(View.INVISIBLE);
                txtBookReservationEligibility.setVisibility(View.VISIBLE);
                txtBookReservationEligibility.setText("Reached Maximum Number of books to reserve");
        }else {
            radioBtnBookReserve.setVisibility(View.VISIBLE);
            txtBookReservationEligibility.setVisibility(View.VISIBLE);
            txtBookReservationEligibility.setText("Eligible to reserve");
        }
    }


    public void reserveBook() {
        db.updateBookAvailabilityInBooksDb(txtBookISBN.getText().toString()
                .trim(), "2");
        db.insertReseredBookDetails(txtBookISBN.getText().toString().trim(),
                preferences.getString("username", ""), getReservedDate());
    }

    private int year, month, day;
    private int sysyear, sysmonth, sysday;
    String sysDate = null;

    public String getReservedDate() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        sysyear = year;
        month = c.get(Calendar.MONTH);
        sysmonth = month;
        day = c.get(Calendar.DAY_OF_MONTH);
        sysday = day;
        sysDate = pad(month + 1) + "/" + pad(day) + "/" + year;
        return sysDate;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}
