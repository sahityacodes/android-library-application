package com.lib;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

public class Aboutus extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		Toolbar toolbar = findViewById(R.id.toolbar_aboutus);
		toolbar.setTitle("About Us");
		setSupportActionBar(toolbar);
	}

	@Override
	public void onBackPressed(){
		Intent i = new Intent(Aboutus.this,LoginActivity.class);
		startActivity(i);
	}
}
