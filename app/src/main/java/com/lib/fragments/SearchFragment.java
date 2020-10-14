package com.lib.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lib.R;
import com.lib.StudentBookReservation;
import com.lib.database.UniGeDataBaseRepo;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel dashboardViewModel;
    AutoCompleteTextView autoCompleteEdtISBNInput,autoCompleteNameInput;
    Button  btnBookSearch,btnBookSearchName;
    ListView listViewBookSearch;
    UniGeDataBaseRepo db;
    Cursor curBooksDB;
    String bookIsbnStr = null,
            bookNameStr = null, bookImageStr = null, bookAuthorStr = null;

    BooksAdapter booksAdapter;

    ArrayList<String> listAllBook = null;

    ArrayList<String> listBookName = null;
    ArrayList<String> listBookISBN = null;
    ArrayList<String> listBookAuthor = null;
    ArrayList<String> listBookImage = null;
    ArrayList<String> listSNo = null;

    RelativeLayout relLayIsbnInput,relLayNameInput;
    View SplitLineAbvBooklist, SplitLineHorup;


    SharedPreferences preferences;

    private RadioGroup radiogp_btn_isbn_booklist;
    private RadioButton radioBtnSearchIsbn;
    private RadioButton radioBtnBookList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        db = new UniGeDataBaseRepo(getActivity().getApplicationContext());
        db.openDB();

        preferences = getActivity().getApplicationContext().getSharedPreferences(
                "libAppPref", 0);

        autoCompleteEdtISBNInput = root.findViewById(R.id.auto_complete_edt_txt_isbn);
        autoCompleteNameInput = root.findViewById(R.id.auto_complete_edt_txt_name);

        relLayIsbnInput = root.findViewById(R.id.rel_lay_isbn_input);
        relLayIsbnInput.setVisibility(View.GONE);

        relLayNameInput = root.findViewById(R.id.rel_lay_name_input);
        relLayNameInput.setVisibility(View.GONE);

        SplitLineHorup = root.findViewById(R.id.SplitLine_horup);
        SplitLineHorup.setVisibility(View.GONE);

        SplitLineAbvBooklist = root.findViewById(R.id.SplitLine_abv_booklist);
        SplitLineAbvBooklist.setVisibility(View.GONE);

        listViewBookSearch = root.findViewById(R.id.listView_book_search);
        listViewBookSearch.setVisibility(View.GONE);
        booksAdapter = new BooksAdapter(getActivity().getApplicationContext());

        getAvailableBooksFromDB();

        listViewBookSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("isbnFrom", "booklist");
                editor.putString("bookSearch_isbn", listBookISBN.get(position));
                editor.commit();
                // to call StudentBookReservation activity
                Intent intent = new Intent();
                intent.setClass(getContext(),
                            StudentBookReservation.class);
                startActivity(intent);
            }
        });

        radiogp_btn_isbn_booklist = root.findViewById(R.id.radiogp_btn_isbn_booklist);
        radioBtnSearchIsbn = root.findViewById(R.id.radio_btn_search_isbn);
        radioBtnBookList = root.findViewById(R.id.radio_btn_book_list);

        radiogp_btn_isbn_booklist
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int checkedRadioButtonID = radiogp_btn_isbn_booklist
                                .getCheckedRadioButtonId();
                        switch (checkedRadioButtonID) {
                            case R.id.radio_btn_search_isbn:
                                relLayNameInput.setVisibility(View.GONE);
                                relLayIsbnInput.setVisibility(View.VISIBLE);
                                SplitLineAbvBooklist.setVisibility(View.VISIBLE);
                                SplitLineHorup.setVisibility(View.VISIBLE);
                                listViewBookSearch.setVisibility(View.GONE);
                                break;
                            case R.id.radio_btn_book_list:
                                relLayNameInput.setVisibility(View.GONE);
                                relLayIsbnInput.setVisibility(View.GONE);
                                SplitLineAbvBooklist.setVisibility(View.GONE);
                                SplitLineHorup.setVisibility(View.GONE);
                                listViewBookSearch.setVisibility(View.VISIBLE);
                                break;
                            case R.id.radio_btn_search_name:
                                relLayIsbnInput.setVisibility(View.GONE);
                                relLayNameInput.setVisibility(View.VISIBLE);
                                SplitLineAbvBooklist.setVisibility(View.GONE);
                                SplitLineHorup.setVisibility(View.GONE);
                                listViewBookSearch.setVisibility(View.GONE);
                                break;

                            default:
                                break;
                        }
                    }
                });

        btnBookSearch = root.findViewById(R.id.btn_search_book);
        btnBookSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to check whether autoCompleteEdtISBNInput is empty or not
                if (autoCompleteEdtISBNInput.getText().toString().trim()
                        .length() == 0) {
                    new AlertDialog.Builder(getContext()).setTitle("Please Enter ISBN of Book").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else if (autoCompleteEdtISBNInput.getText().toString().trim()
                        .length() != 10) // to check whether
                {
                    new AlertDialog.Builder(getContext()).setTitle("Please Enter valid 10 digit Book ISBN").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else {
                    if(db.getSpecificBookByISBN(autoCompleteEdtISBNInput.getText().toString().trim()).getCount()>0) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("isbnFrom", "isbnInput");
                        editor.putString("bookSearch_isbn", autoCompleteEdtISBNInput.getText().toString());
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(getContext(),StudentBookReservation.class);
                        startActivity(intent);
                    }
                    else{
                        new AlertDialog.Builder(getContext()).setTitle("Please a Valid ISBN").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }}).show();
                    }
                }
            }
        });
        btnBookSearchName = root.findViewById(R.id.btn_search_book_name);
        btnBookSearchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompleteNameInput.getText().toString().length() == 0) {
                    new AlertDialog.Builder(getContext()).setTitle("Please Enter the Name of the Book").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("isbnFrom", "nameInput");
                    editor.putString("bookSearch_isbn",null);
                    editor.putString("bookSearch_name",
                            autoCompleteNameInput.getText().toString());
                    editor.commit();
                    if(db.getSpecificBookByName(autoCompleteNameInput.getText().toString()).getCount()==1) {
                        Intent intent = new Intent();
                        intent.setClass(getContext(),StudentBookReservation.class);
                        startActivity(intent);
                    }else{
                        new AlertDialog.Builder(getContext()).setTitle("Please a Valid Name").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }
                }
            }
        });
        return root;

    }
     public void getAvailableBooksFromDB() {
      curBooksDB = db.getAvailableBooks("0");

            if (curBooksDB.getCount() > 0) {

                listBookISBN = new ArrayList<String>();
                listBookName = new ArrayList<String>();
                listBookAuthor = new ArrayList<String>();
                listBookImage = new ArrayList<String>();

                if (curBooksDB.moveToFirst()) {
                    do {
                        bookIsbnStr = curBooksDB.getString(curBooksDB
                                .getColumnIndex(curBooksDB.getColumnName(0)));
                        bookNameStr = curBooksDB.getString(curBooksDB
                                .getColumnIndex(curBooksDB.getColumnName(1)));
                        bookAuthorStr = curBooksDB.getString(curBooksDB
                                .getColumnIndex(curBooksDB.getColumnName(2)));
                        bookImageStr = curBooksDB.getString(curBooksDB
                                .getColumnIndex(curBooksDB.getColumnName(3)));

                        listBookISBN.add(bookIsbnStr);
                        listBookName.add(bookNameStr);
                        listBookAuthor.add(bookAuthorStr);
                        listBookImage.add(bookImageStr);

                    } while (curBooksDB.moveToNext());
                }
            }
            listViewBookSearch.setAdapter(booksAdapter);
            booksAdapter.notifyDataSetChanged();
            setIsbnNosToAutoCompleteEdittext();
            setNameToAutoCompleteEdittext();
     }

    public void setNameToAutoCompleteEdittext() {
        curBooksDB = db.getAllBooks();
        listAllBook = new ArrayList<String>();

        if (curBooksDB.moveToFirst()) {
            do {
                bookNameStr= curBooksDB.getString(curBooksDB
                        .getColumnIndex(curBooksDB.getColumnName(1)));

                listAllBook.add(bookNameStr);

            } while (curBooksDB.moveToNext());
        }

        String[] isbnNosArray = listAllBook
                .toArray(new String[listAllBook.size()]);
        SpinnerAdapter adapter = new SpinnerAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, isbnNosArray);
        autoCompleteNameInput.setAdapter(adapter);
        autoCompleteNameInput.setThreshold(2);
    }

        public void setIsbnNosToAutoCompleteEdittext() {
            curBooksDB = db.getAllBooks();
            listAllBook= new ArrayList<>();
            if (curBooksDB.moveToFirst()) {
                do {
                    bookIsbnStr = curBooksDB.getString(curBooksDB
                            .getColumnIndex(curBooksDB.getColumnName(0)));
                    listAllBook.add(bookIsbnStr);

                } while (curBooksDB.moveToNext());
            }

            String[] isbnNosArray = listAllBook
                    .toArray(new String[listAllBook.size()]);
            SpinnerAdapter adapter = new SpinnerAdapter(getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_item, isbnNosArray);

            autoCompleteEdtISBNInput.setAdapter(adapter);
            autoCompleteEdtISBNInput.setThreshold(2);
        }

            // this adapter is used to set listview items values i.e book list
            public class BooksAdapter extends BaseAdapter {
                @SuppressWarnings("unused")
                private Context context;

                public BooksAdapter(Context context) {
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
                    Context context = bookImage.getContext();
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

            // spinneradapter is used to get auto complete tex for isbn input
            class SpinnerAdapter extends ArrayAdapter<String> {
                Context context;
                String[] items = new String[]{};

                public SpinnerAdapter(final Context context,
                                      final int textViewResourceId, final String[] objects) {
                    super(context, textViewResourceId, objects);
                    this.items = objects;
                    this.context = context;
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {

                    if (convertView == null) {
                        LayoutInflater inflater = LayoutInflater.from(context);
                        convertView = inflater.inflate(R.layout.spinner_text, parent,
                                false);
                    }

                    TextView tv = convertView
                            .findViewById(R.id.spinnerTarget);
                    tv.setText(getItem(position).trim());
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTextSize(20);
                    tv.setHeight(45);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setGravity(Gravity.LEFT);
                    return convertView;
                }

                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        LayoutInflater inflater = LayoutInflater.from(context);
                        convertView = inflater.inflate(R.layout.spinner_text, parent,
                                false);
                    }

                    TextView tv = convertView
                            .findViewById(R.id.spinnerTarget);
                    tv.setText(getItem(position).trim());
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextSize(20);
                    tv.setHeight(45);
                    return convertView;
                }
            }

}