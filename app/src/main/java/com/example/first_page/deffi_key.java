package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class deffi_key extends AppCompatActivity {

    EditText a_key,b_key,p_k1,p_k2;
    AlertDialog.Builder builder;
    long aliceid;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    long bobid,keyid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deffi_key);
        p_k1 = findViewById(R.id.gkey_p);
        p_k2 = findViewById(R.id.gkey_g);
        a_key = findViewById(R.id.p_k1);
        b_key = findViewById(R.id.p_k2);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        }
        reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid());
        reference.child("Alice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    aliceid = dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("bob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    bobid = dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    keyid = dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        builder = new AlertDialog.Builder(this);
    }




    public long key_gen(long p,long g,long key_a,long key_b)
    {
        long x,y;
        x = (int)Math.pow(g,key_a) % p;
        y = (int)Math.pow(g,key_b) % p;
        key_a = (int)Math.pow(y,key_a) % p;
        key_b = (int)Math.pow(x,key_b) % p;
        System.out.println(key_a+" "+key_b);
        return key_a;
    }

    public void clear(View view) {
        p_k1.setText(null);
        p_k2.setText(null);
        a_key.setText(null);
        b_key.setText(null);
    }

    public void submit(View view) {
        String key_a1 =a_key.getText().toString(), key_b1 = b_key.getText().toString(), p1 = p_k1.getText().toString(), g1 = p_k2.getText().toString();

        if (key_a1.equals(""))
            a_key.setError("Field is empty!");
        if (key_b1.equals(""))
            b_key.setError("Field is empty!");
        if (p1.equals(""))
            p_k1.setError("Field is empty!");
        if (g1.equals(""))
            p_k2.setError("Field is empty!");

        else{

            long key_a = Long.parseLong(key_a1), key_b = Long.parseLong(key_b1), p = Long.parseLong(p1), g = Long.parseLong(g1);
            Intent intent = new Intent(this, diffie_display.class);
            long secret_key = key_gen(p,g,key_a,key_b);
            intent.putExtra("secret_key",secret_key);
            intent.putExtra("p",p);
            intent.putExtra("g",g);
            intent.putExtra("alice",key_a);
            intent.putExtra("bob",key_b);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com"))
        {
            getMenuInflater().inflate(R.menu.admin,menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.removedb,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {

            switch (id) {

                case R.id.logout:
                    builder.setMessage("Are you sure that you want to logout ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), login.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("logout.");
                    alertDialog.show();
                    break;

                case R.id.viewhis:
                    startActivity(new Intent(this, playfair_display.class));
                    break;

                case R.id.clrhistory:
                    builder.setMessage("Do you want Clear Your History ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.removeValue();
                            aliceid = 0;
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Clear History");
                    alert.show();
                    break;
                case R.id.mainpage:
                    builder.setMessage("Are you sure that you want to go to the main page ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getApplicationContext(),Navigation.class));
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertd = builder.create();
                    alertd.setTitle("main Menu");
                    alertd.show();
                    break;
            }
        } else {
            switch (id) {

                case R.id.logout:
                    builder.setMessage("Are you sure that you want to logout ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), login.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("logout.");
                    alertDialog.show();
                    break;

                case R.id.viewhis:
                    startActivity(new Intent(this, diffie_history.class));
                    break;

                case R.id.clrhistory:
                    builder.setMessage("Do you want Clear Your History ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.removeValue();
                            aliceid = 0;
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Clear History");
                    alert.show();
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
