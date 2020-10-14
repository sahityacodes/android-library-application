package com.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lib.map.MapsActivity;

public class Contactus extends AppCompatActivity {

	TextView website,contactno,location;

	private static final int REQUEST_PHONE_CALL = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactus);
		website = findViewById(R.id.website);
		contactno = findViewById(R.id.contactno);
		location = findViewById(R.id.location);
		Toolbar toolbar = findViewById(R.id.toolbar_contactus);
		toolbar.setTitle("Contact Us");
		setSupportActionBar(toolbar);
		location.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(Contactus.this, MapsActivity.class);
				startActivity(intent);
			}
		});

		website.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.bibliotecauniversitaria.ge.it/it/"));
				startActivity(intent);
			}
		});
		contactno.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel://0102546464"));
				if (ContextCompat.checkSelfPermission(Contactus.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(Contactus.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
				}
				else
				{
					startActivity(intent);
				}
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		Intent i = new Intent(Contactus.this,LoginActivity.class);
		startActivity(i);
	}
}
