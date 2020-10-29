package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    EditText email,pass;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        pd = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }


    public void login(View view) {
        final String inpemail = email.getText().toString().trim(),inppass = pass.getText().toString().trim();

        if(TextUtils.isEmpty(inpemail)){
            email.setError("Email is Required.");
            return;
        }

        if(TextUtils.isEmpty(inppass)){
            pass.setError("Password is Required.");
            return;
        }

        if(inppass.length() < 6){
            pass.setError("Password Must be >= 6 Characters");
            return;
        }
        pd.setMessage("Logging In");

        pd.show();

        mAuth.signInWithEmailAndPassword(inpemail,inppass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    if (inpemail.equals("admin@gmail.com"))
                    {
                        startActivity(new Intent(getApplicationContext(),Navigation.class));
                        Toast.makeText(login.this, "admin", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }

                }
                else {
                    pd.dismiss();
                    Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void move_to_register(View view) {
        startActivity(new Intent(getApplicationContext(),register.class));
    }

    public void move_to_phange_password(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));

    }
}
