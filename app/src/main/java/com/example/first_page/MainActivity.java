package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView t,t_print,name;
    Switch s;
    Button b;

    AlertDialog.Builder builder;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = findViewById(R.id.tv1);
        t_print = findViewById(R.id.tv_print);
        s = findViewById(R.id.sw1);
        b = findViewById(R.id.bt1);
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.tv2);
        builder = new AlertDialog.Builder(this);

        if (mAuth.getCurrentUser() == null ) {
            Intent intent = new Intent(getApplicationContext(), register.class);
            startActivity(intent);
            finish();
        }
        else if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com"))
        {
            Intent intent = new Intent(getApplicationContext(), Navigation.class);
            startActivity(intent);
            finish();
        }
        else {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name.setText("Hello "+dataSnapshot.child("name").getValue().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        t_print.setText("Playfair Algorithm");
                    } else {
                        t_print.setText("Diffie Hellman Algorithm");

                    }
                }
            });
            TextPaint paint = t.getPaint();

            float width = paint.measureText("Let's Encrypt!");

            Shader textShader = new LinearGradient(0, 0, width, t.getTextSize(),
                    new int[]{
                            Color.parseColor("#AB47BC"),
                            Color.parseColor("#FFE91E63"),
                            /*Color.parseColor("#64B678"),
                            Color.parseColor("#478AEA"),
                            Color.parseColor("#8446CC"),*/
                    }, null, Shader.TileMode.CLAMP);
            t.getPaint().setShader(textShader);
        }
    }

    public void algo_change(View view) {
        if(s.isChecked())
        {
            //playfair code goes here!
            Intent Playfair = new Intent(this, playfair_inp.class);
            startActivity(Playfair);
        }
        else
        {
            //diffie hellman algorithm code goes here!
            Intent Diffie = new Intent(this, deffi_key.class);
            startActivity(Diffie);

        }
    }
    public void onBackPressed() {

        builder.setMessage("Are you sure that you want to exit ?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                System.exit(0);
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Exit the Application");
        alert.show();
    }
}
