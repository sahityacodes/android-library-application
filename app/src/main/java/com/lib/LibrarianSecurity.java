package com.lib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lib.database.UniGeDataBaseRepo;

public class LibrarianSecurity extends Activity {

	TextView txtWelcome;
	Button btnQuit, btnSubmit;
	UniGeDataBaseRepo db;
	Cursor curLibDB;
	SharedPreferences.Editor editor;
	String welcomeStr = null, librarianIdStr = null;
	SharedPreferences preferences;
	EditText edtLibSecurityPin;
	boolean doubleBack = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_librarian_security);
		db = new UniGeDataBaseRepo(LibrarianSecurity.this);
		db.openDB(); // opens db
		preferences = getApplicationContext().getSharedPreferences(
				"libAppPref", 0);
		editor = preferences.edit();
		txtWelcome = findViewById(R.id.welcomeMessage);
		edtLibSecurityPin = findViewById(R.id.librarianPassword);
		librarianIdStr = preferences.getString("username", "");
		txtWelcome.setText("Welcome " + librarianIdStr);
		btnSubmit = findViewById(R.id.libloginButton);
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edtLibSecurityPin.getText().toString().trim().length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please enter your security pin...", Toast.LENGTH_SHORT)
							.show();
				}else if (!edtLibSecurityPin
						.getText()
						.toString()
						.equalsIgnoreCase(
								getLibSecurityPin(librarianIdStr))) {
					Toast.makeText(getApplicationContext(),
							"Incorrect Pin", Toast.LENGTH_SHORT)
							.show();
				} else {
					//to start librarian home page
					startActivity(new Intent(LibrarianSecurity.this,LibrarianBottomNavigation.class));
					finish();
					
				}
			}
		});

	}
	
	// this method is used to get student password from database and send
		public String getLibSecurityPin(String userId) {
			String studPwd_db = null;
			// cursor which contains student password
			curLibDB = db.getLibSecurity(userId);
			// checking whether cursor contains more than one record or not
			if (curLibDB.getCount() > 0) {
				// moving cursor from first to end to get all values
				if (curLibDB.moveToFirst()) {
					studPwd_db = curLibDB.getString(curLibDB
							.getColumnIndex(curLibDB.getColumnName(0)));
				}

			}
			return studPwd_db;
		}

	protected void onDestroy() {
		super.onDestroy();
		db.closeDB();
	}

	@Override
	public void onBackPressed() {
		if (doubleBack) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to exit?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
							signOutApp();
						}
					})
					.setNeutralButton("Signout", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							signOutApp();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			doubleBack = true;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBack = false;
				}
			}, 2000);
		}
	}

	public void signOutApp(){
		if(preferences.contains("stu_is_signed_in")) {
			editor.remove("stu_is_signed_in");
			editor.commit();
		}
		else if (preferences.contains("lib_is_signed_in")){
			editor.remove("lib_is_signed_in");
			editor.commit();
		}
		Intent i = new Intent(this,LoginActivity.class);
		startActivity(i);
		finish();
	}

}
