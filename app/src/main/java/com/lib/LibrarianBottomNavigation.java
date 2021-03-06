package com.lib;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LibrarianBottomNavigation extends AppCompatActivity {

    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    BottomNavigationView navView;
    boolean doubleBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar_lib);
        setSupportActionBar(toolbar);
        preferences = getApplicationContext().getSharedPreferences(
                "libAppPref", 0);
        editor = preferences.edit();
        navView = findViewById(R.id.nav_lib_view);
        BottomNavigationView navView = findViewById(R.id.nav_lib_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_student_history, R.id.navigation_reserved_books)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_lib_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            signOutApp();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int seletedItemId = navView.getSelectedItemId();
        if (R.id.navigation_profile != seletedItemId) {
            navView.setSelectedItemId(R.id.navigation_profile);
        } else {
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
