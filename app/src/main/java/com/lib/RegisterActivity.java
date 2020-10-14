package com.lib;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lib.database.UniGeDataBaseRepo;


public class RegisterActivity extends AppCompatActivity {
    Button reg_submit;
    EditText edtFName, edtLName, edtId, edtPwd, edtEmailId;
    UniGeDataBaseRepo db;
    String fnameStr="",lnameStr="",idStr="",pwdStr="",emailidStr="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        edtFName = findViewById(R.id.fname);
        edtLName = findViewById(R.id.lname);
        edtId = findViewById(R.id.matricula);
        edtPwd = findViewById(R.id.pwd);
        edtEmailId = findViewById(R.id.emailid);

        db = new UniGeDataBaseRepo(RegisterActivity.this);
        db.openDB();

        reg_submit = findViewById(R.id.cirRegisterButton);
        reg_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean registrationChecks=true;
                if (edtFName.getText().toString().trim().length() == 0) {
                        edtFName.setError("Please enter your First name");
                        registrationChecks=false;
                    }
                    if (edtLName.getText().toString().trim().length() == 0) {
                        edtLName.setError("Please enter your Last name");
                        registrationChecks=false;
                    }
                    if (edtId.getText().toString().trim().length() == 0) {
                        edtId.setError("Please enter valid Matricula number");
                        registrationChecks=false;
                    }else if (db.checkMatricula(edtId.getText().toString()) != 0) {
                        edtId.setError("Account exists with this Matricula number");
                        registrationChecks=false;
                    }
                    if (edtPwd.getText().toString().trim().length() == 0) {
                        registrationChecks=false;
                        edtPwd.setError("Please enter a valid password");
                    } else if (!(edtPwd.getText().toString().trim().length() > 7) &&
                            !(edtPwd.getText().toString().trim().matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$"))) {
                        edtPwd.setError("Password should contain 8 alphanumeric characters");
                        registrationChecks=false;
                    }
                    if (edtEmailId.getText().toString().trim().length() == 0) {
                        edtEmailId.setError("Please enter a valid email address");
                        registrationChecks=false;
                    }
                if(registrationChecks) {
                    fnameStr = edtFName.getText().toString().trim();
                    lnameStr = edtLName.getText().toString().trim();
                    idStr = edtId.getText().toString().trim();
                    pwdStr = edtPwd.getText().toString().trim();
                    emailidStr = edtEmailId.getText().toString().trim();
                    insertRegistrationDetails();
                    Toast.makeText(getApplicationContext(),
                            "Registered Successfully", Toast.LENGTH_LONG)
                            .show();
                    Intent page = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(page);
                    finish();
                }
            }
        });
    }

    public void insertRegistrationDetails() {
        db.insertRegistration(fnameStr, lnameStr, idStr, pwdStr, emailidStr);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }


}
