package com.lib;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.lib.database.UniGeDataBaseRepo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoadingScreen extends AppCompatActivity {

    UniGeDataBaseRepo db;
    protected int _splashTime = 2000;
    public Thread splashTread;
    boolean flag, stopActivity = false;
    Cursor c;
    SharedPreferences pref;
    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        splash = findViewById(R.id.splash);
        final LoadingScreen splashScreen = this;
        db = new UniGeDataBaseRepo(LoadingScreen.this, null, null, 0);
        db.openDB();

        try {

            callMethod();

        } catch (Exception e) {
            // destination path where our database file will be placed
            String destPath = "/data/data/" + getPackageName()
                    + "/databases/UNIGE_DB";
            // creating a file to be saved in the destination path
            File f = new File(destPath);

            // checking whether file exists or not
            if (f.exists()) {
                Log.v("Status", "Data path is arranged");

                try {
                    // copying our database to destination path
                    CopyDB(getBaseContext().getAssets()
                            .open("librarydb.sqlite"), new FileOutputStream(
                            destPath));
                    db.close();
                } catch (FileNotFoundException e1) {// executes when file is not
                    // found
                    e1.printStackTrace();
                } catch (IOException e1) {// executes when input or output error
                    // arise
                    e1.printStackTrace();
                }
            }
        }
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        splash.setImageResource(R.drawable.logo);
                        wait(_splashTime);
                    }

                } catch (InterruptedException e) {
                } finally {
                    Intent i = new Intent();
                    i.setClass(splashScreen, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        splashTread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            flag = callMethod();

            System.out.println("flag " + flag);

            if (!flag) {
                Toast.makeText(this, "DataBase Not Injected",
                        Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean callMethod() {
        boolean b = false;
        c = db.getAllBookDetails();
        if (c.getCount() > 0) {
            b = true;
            return b;
        }

        else {
            return b;
        }

    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        Log.v("Status", "Data is copied");
        inputStream.close();
        outputStream.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }

}


