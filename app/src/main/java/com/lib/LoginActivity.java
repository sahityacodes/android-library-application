package com.lib;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.lib.database.UniGeDataBaseRepo;


public class LoginActivity extends AppCompatActivity {
    EditText edtLoginId, edtPassword;
    Button btnSignIn;
    Spinner sp;
    TextInputLayout userId,password,spinner;
    UniGeDataBaseRepo db;
    Cursor curLogin;
    SharedPreferences preferences;
    int studstr = 0;
    ImageView contactUs,aboutUs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        db = new UniGeDataBaseRepo(LoginActivity.this);
        db.openDB();
        preferences = getApplicationContext().getSharedPreferences(
                "libAppPref", 0);
        editor = preferences.edit();
        if(preferences.contains("stu_is_signed_in")) {
        launchStudentNavigation();
        }
        else if (preferences.contains("lib_is_signed_in")){
        launchLibrarianNavigation();
        }

       edtLoginId = findViewById(R.id.loginId);
       sp = findViewById(R.id.usertypes);
       edtPassword = findViewById(R.id.loginPassword);
       userId = findViewById(R.id.textUserId);
       password = findViewById(R.id.textInputPassword);
       spinner = findViewById(R.id.userTypeInput);
       contactUs = findViewById(R.id.contactus);
       aboutUs = findViewById(R.id.aboutus);
       edtLoginId.setText(preferences.getString("username",null));
       edtPassword.setText(preferences.getString("password",null));
       sp.setSelection(preferences.getInt("login_type",0 ));
       contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent();
                contactIntent.setClass(LoginActivity.this,Contactus.class);
                startActivity(contactIntent);
                finish();
            }
            });

       aboutUs.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent aboutusIntent = new Intent();
               aboutusIntent.setClass(LoginActivity.this,Aboutus.class);
               startActivity(aboutusIntent);
               finish();
           }
       });

       sp = findViewById(R.id.usertypes);
       sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view,
                                      int arg2, long id) {
               if (arg2>0) {
                   studstr = arg2;
               }
               }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
               // TODO Auto-generated method stub

           }
       });
           btnSignIn = findViewById(R.id.cirLoginButton);
           btnSignIn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   editor.putInt("login_type",studstr);
                   editor.putString("username", edtLoginId.getText()
                           .toString().trim());
                   editor.putString("password", edtPassword.getText()
                           .toString().trim());
                   editor.commit();
                   if (studstr==1) {
                       spinner.setError("");
                       if (edtLoginId.getText().toString().trim().length() == 0) {
                           userId.setError("Please Enter Your Login Id");
                       }
                       if (!db.checkStudentUserId(edtLoginId.getText()
                               .toString().trim())) {
                           userId.setError("Please Enter Valid Login Id ");
                       }
                       if (edtPassword.getText().toString().trim().length() == 0) {
                           password.setError("Please Enter Your Password");
                       }  if (!edtPassword
                               .getText()
                               .toString()
                               .equalsIgnoreCase(
                                       getStudPassword(edtLoginId.getText()
                                               .toString().trim()))) {
                           password.setError("Incorrect Password");
                       } else {
                           Toast.makeText(getApplicationContext(), "Success",
                           Toast.LENGTH_SHORT).show();
                           editor.putString("stu_is_signed_in","true");
                           editor.commit();
                           launchStudentNavigation();
                       }
                   } else if (studstr==2) {
                       spinner.setError("");
                       if (edtLoginId.getText().toString().trim().length() == 0) {
                           userId.setError("Please Enter Your Login Id");
                       } else if (!db.checkLibrarianUserId(edtLoginId.getText()
                               .toString().trim())) {
                           userId.setError("Please Enter Your Valid Login Id");
                       } else if (edtPassword.getText().toString().trim().length() == 0) {
                           password.setError("Please Enter password");
                       } else if (!edtPassword
                               .getText()
                               .toString()
                               .equalsIgnoreCase(
                                       getLibrarianPassword(edtLoginId.getText()
                                               .toString().trim()))) {
                           password.setError("Incorrect Password");
                       } else {
                           Toast.makeText(getApplicationContext(), "Success",
                                   Toast.LENGTH_SHORT).show();
                           editor.putString("lib_is_signed_in","true");
                           editor.commit();
                           launchLibrarianNavigation();
                       }
                   }else{
                       spinner.setError("Please select User Type");
                   }
               }
           });
       }

    public String getStudPassword(String userId) {
        String studPwd_db = null;
        curLogin = db.getStudentPassword(userId);
        if (curLogin.getCount() > 0) {
            if (curLogin.moveToFirst()) {
                studPwd_db = curLogin.getString(curLogin
                        .getColumnIndex(curLogin.getColumnName(0)));
            }
        }
        return studPwd_db;
    }

    public String getLibrarianPassword(String userId) {
        String studPwd_db = null;
        curLogin = db.getLibrarianPassword(userId);
        if (curLogin.getCount() > 0) {
            if (curLogin.moveToFirst()) {
                studPwd_db = curLogin.getString(curLogin
                        .getColumnIndex(curLogin.getColumnName(0)));
            }
        }
        return studPwd_db;
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    public void launchStudentNavigation(){
        Intent i = new Intent();
        i.setClass(LoginActivity.this, StudentNavigation.class);
        startActivity(i);
        finish();
    }

    public void launchLibrarianNavigation(){
        Intent i = new Intent(LoginActivity.this, LibrarianSecurity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
